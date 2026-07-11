package com.example.drbuddy_medicalhealthanalyzer.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.drbuddy_medicalhealthanalyzer.viewmodel.AnalyzeViewModel

@Composable
fun AnalyzeScreen(viewModel: AnalyzeViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.selectImage(uri)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F7F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Text(
            text = "Analyze Your Health Report",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D3B3A)
        )
        Text(
            text = "Upload a report image or paste text for a professional AI summary.",
            fontSize = 14.sp,
            color = Color(0xFF4A6A65),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input Selector
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F8F7), shape = RoundedCornerShape(12.dp))
                        .padding(4.dp)
                ) {
                    TabButton(
                        text = "Image",
                        icon = Icons.Default.Image,
                        isSelected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    TabButton(
                        text = "Text",
                        icon = Icons.Default.Description,
                        isSelected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                when (selectedTab) {
                    0 -> ImagePickerBox(uiState.imageUri) { launcher.launch("image/*") }
                    1 -> TextInputBox(uiState.text) { viewModel.updateText(it) }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (uiState.error != null) {
                    Text(
                        text = uiState.error!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                    onClick = { viewModel.analyzeReport() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D8B)),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Analytics, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Start AI Analysis", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Analysis Result Display
        uiState.result?.let { result ->
            Spacer(modifier = Modifier.height(24.dp))
            AnalysisResultView(result)
        }
    }
}

@Composable
fun TabButton(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF0F9D8B) else Color.Transparent,
            contentColor = if (isSelected) Color.White else Color(0xFF4A6A65)
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun ImagePickerBox(uri: Uri?, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF8FCFB))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF0F9D8B))
                Text("Tap to select report image", color = Color(0xFF4A6A65))
            }
        }
    }
}

@Composable
fun TextInputBox(text: String, onValueChange: (String) -> Unit) {
    TextField(
        value = text,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().height(150.dp),
        placeholder = { Text("Paste the medical report text here...") },
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFF8FCFB),
            unfocusedContainerColor = Color(0xFFF8FCFB),
            focusedIndicatorColor = Color(0xFF0F9D8B)
        )
    )
}

@Composable
fun AnalysisResultView(result: AnalysisResult) {
    Column {
        SectionTitle("SUMMARY (සිංහල සාරාංශය)", Icons.Default.Translate)
        ResultCard {
            Text(
                text = result.sinhalaSummary,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1D3B3A)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("WHAT THE REPORT SAYS", Icons.Default.FactCheck)
        ResultCard {
            Text(result.medicalReportExplanation, fontSize = 14.sp, color = Color(0xFF1D3B3A))
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("WHAT IS HAPPENING IN YOUR BODY", Icons.Default.HealthAndSafety)
        ResultCard {
            Text(result.whatHappenedInBody, fontSize = 14.sp, color = Color(0xFF1D3B3A))
        }

        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle("WHAT YOU SHOULD DO NEXT", Icons.Default.ArrowCircleRight)
        ResultCard {
            Text(result.whatToDoNext, fontSize = 14.sp, color = Color(0xFF1D3B3A))
        }

        if (result.recommendations.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("RECOMMENDATIONS", Icons.Default.List)
            ResultCard {
                result.recommendations.forEach { rec ->
                    Row(modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0F9D8B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(rec, fontSize = 14.sp, color = Color(0xFF1D3B3A))
                    }
                }
            }
        }

        if (result.warnings.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle("IMPORTANT DISCLAIMERS", Icons.Default.Warning)
            ResultCard(containerColor = Color(0xFFFFF9E6)) {
                result.warnings.forEach { warning ->
                    Text("⚠️ $warning", fontSize = 12.sp, color = Color(0xFF856404), modifier = Modifier.padding(vertical = 2.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Note: This is an AI summary and NOT a medical diagnosis. Please consult a doctor for clinical confirmation.",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        )
    }
}

@Composable
fun SectionTitle(title: String, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFF0F9D8B), modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4A6A65))
    }
}

@Composable
fun ResultCard(containerColor: Color = Color.White, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}
