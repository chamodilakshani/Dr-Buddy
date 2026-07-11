package com.example.drbuddy_medicalhealthanalyzer.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drbuddy_medicalhealthanalyzer.BuildConfig
import com.example.drbuddy_medicalhealthanalyzer.data.MedicalReport
import com.example.drbuddy_medicalhealthanalyzer.data.ReportRepository
import com.example.drbuddy_medicalhealthanalyzer.ui.screens.AnalysisResult
import com.example.drbuddy_medicalhealthanalyzer.ui.screens.AnalyzeUiState
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AnalyzeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(AnalyzeUiState())
    val uiState: StateFlow<AnalyzeUiState> = _uiState.asStateFlow()
    private val repository = ReportRepository()

    // Using the FREE version of Gemini (Firebase AI Logic SDK)
    private var generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel(modelName = "gemini-3-flash-preview")

    fun selectImage(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri, result = null, error = null) }
    }

    fun updateText(text: String) {
        _uiState.update { it.copy(text = text, result = null, error = null) }
    }

    fun analyzeReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val state = _uiState.value
                val imageUri = state.imageUri
                val textInput = state.text

                if (imageUri == null && textInput.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Please upload an image or paste text first!"
                        )
                    }
                    return@launch
                }

                val prompt = """
                    You are a professional medical report analyzer. 
                    Analyze the provided medical report (image or text) and provide a highly detailed response.
                    
                    The response MUST follow this structure and be valid JSON:
                    {
                      "englishSummary": "A concise professional summary in English.",
                      "sinhalaSummary": "A natural, warm, and easy-to-understand explanation in Sinhala for a patient. Use simple Sinhala words, avoid overly technical translation where a simple explanation is better.",
                      "medicalReportExplanation": "Detailed explanation of what the report values mean in simple terms.",
                      "whatHappenedInBody": "Explain what is happening inside the patient's body based on these results (e.g., 'Thyroid gland is underactive').",
                      "whatToDoNext": "Step-by-step instructions on what the patient should do now.",
                      "recommendations": ["Recommendation 1", "Recommendation 2"],
                      "warnings": ["Warning 1", "Warning 2"]
                    }
                    
                    IMPORTANT: Ensure the Sinhala summary is natural and conversational, like a kind doctor explaining to a patient.
                """.trimIndent()

                val response = performAnalysis(imageUri, textInput, prompt)
                val responseText = response.text ?: throw Exception("AI returned an empty response.")
                
                val result = parseJsonResponse(responseText)

                // Save to History
                repository.saveReport(MedicalReport(result = result))

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        result = result
                    )
                }

            } catch (e: Exception) {
                Log.e("AnalyzeViewModel", "Analysis failed", e)
                val errorMessage = e.message ?: "Unknown error"
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = if (errorMessage.contains("401")) {
                            "Invalid API Key. Please get a new key from aistudio.google.com"
                        } else if (errorMessage.contains("403")) {
                            "Permission Denied. Your region might not support the free tier, or the key is restricted."
                        } else {
                            "Analysis failed: $errorMessage"
                        }
                    )
                }
            }
        }
    }

    private suspend fun performAnalysis(imageUri: Uri?, textInput: String, prompt: String) = withContext(Dispatchers.IO) {
        if (imageUri != null) {
            val bitmap = loadBitmapFromUri(imageUri) ?: throw Exception("Failed to process image")
            val inputContent = content {
                image(bitmap)
                text(prompt)
                if (textInput.isNotEmpty()) text("Context: $textInput")
            }
            generativeModel.generateContent(inputContent)
        } else {
            generativeModel.generateContent("$prompt\n\nReport Text: $textInput")
        }
    }

    private suspend fun loadBitmapFromUri(uri: Uri): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val original = BitmapFactory.decodeStream(inputStream)
            
            original?.let {
                if (it.width > 2000 || it.height > 2000) {
                    val scale = 0.5f
                    Bitmap.createScaledBitmap(it, (it.width * scale).toInt(), (it.height * scale).toInt(), true)
                } else it
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseJsonResponse(responseText: String): AnalysisResult {
        // Clean markdown blocks if present
        var cleaned = responseText.trim()
        if (cleaned.contains("```json")) {
            cleaned = cleaned.substringAfter("```json").substringBeforeLast("```").trim()
        } else if (cleaned.contains("```")) {
            cleaned = cleaned.substringAfter("```").substringBeforeLast("```").trim()
        }

        return try {
            AnalysisResult(
                englishSummary = extractValue(cleaned, "englishSummary") ?: "No summary found",
                sinhalaSummary = extractValue(cleaned, "sinhalaSummary") ?: "",
                medicalReportExplanation = extractValue(cleaned, "medicalReportExplanation") ?: "",
                whatHappenedInBody = extractValue(cleaned, "whatHappenedInBody") ?: "",
                whatToDoNext = extractValue(cleaned, "whatToDoNext") ?: "",
                recommendations = extractList(cleaned, "recommendations"),
                warnings = extractList(cleaned, "warnings")
            )
        } catch (e: Exception) {
            Log.e("AnalyzeViewModel", "Parsing failed for: $cleaned", e)
            // Fallback for raw text
            AnalysisResult(
                englishSummary = responseText,
                sinhalaSummary = "AI විසින් සාරාංශයක් ලබා දුන් නමුත් එය නිවැරදිව සැකසීමට නොහැකි විය. (AI provided a summary but it couldn't be parsed correctly.)",
                medicalReportExplanation = "Raw AI Output follows:\n$responseText",
                whatHappenedInBody = "",
                whatToDoNext = "",
                recommendations = emptyList(),
                warnings = listOf("Parsing error or raw AI output.")
            )
        }
    }

    private fun extractValue(json: String, key: String): String? {
        // More robust regex for JSON extraction
        val pattern = "\"$key\"\\s*:\\s*\"(.*?)\"\\s*(?:,|})".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = pattern.find(json)
        return match?.groupValues?.get(1)
            ?.replace("\\n", "\n")
            ?.replace("\\\"", "\"")
            ?.replace("\\r", "")
            ?.trim()
    }

    private fun extractList(json: String, key: String): List<String> {
        val pattern = "\"$key\"\\s*:\\s*\\[(.*?)\\]".toRegex(RegexOption.DOT_MATCHES_ALL)
        val match = pattern.find(json)?.groupValues?.get(1) ?: return emptyList()
        return match.split("\",").map { 
            it.trim()
                .removeSurrounding("\"")
                .replace("\\n", "\n")
                .replace("\\\"", "\"")
        }.filter { it.isNotEmpty() }
    }
}
