package com.example.drbuddy_medicalhealthanalyzer.ui.screens

import android.net.Uri

data class AnalyzeUiState(
    val imageUri: Uri? = null,
    val text: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val result: AnalysisResult? = null
)

data class AnalysisResult(
    val englishSummary: String,
    val sinhalaSummary: String,
    val medicalReportExplanation: String,
    val whatHappenedInBody: String,
    val whatToDoNext: String,
    val recommendations: List<String>,
    val warnings: List<String>
)
