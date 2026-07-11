package com.example.drbuddy_medicalhealthanalyzer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drbuddy_medicalhealthanalyzer.data.MedicalReport
import com.example.drbuddy_medicalhealthanalyzer.data.ReportRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryViewModel : ViewModel() {
    private val repository = ReportRepository()
    val reports: StateFlow<List<MedicalReport>> = repository.getReports()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

@Composable
fun HistoryScreen(viewModel: HistoryViewModel = viewModel()) {
    val reports by viewModel.reports.collectAsState()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F7F5))
            .padding(16.dp)
    ) {
        Text(
            text = "Patient Report Chart",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D3B3A),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (reports.isEmpty()) {
            EmptyHistoryView()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reports) { report ->
                    ReportCard(report, dateFormat)
                }
            }
        }
    }
}

@Composable
fun EmptyHistoryView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color(0xFF8A9A95))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "No reports charted yet", fontSize = 16.sp, color = Color(0xFF4A6A65))
            Text(text = "Analyzed reports will appear here", fontSize = 14.sp, color = Color(0xFF8A9A95))
        }
    }
}

@Composable
fun ReportCard(report: MedicalReport, dateFormat: SimpleDateFormat) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MEDICAL REPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F9D8B),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = dateFormat.format(report.date),
                        fontSize = 12.sp,
                        color = Color(0xFF8A9A95)
                    )
                }
                IconButton(onClick = { /* Share functionality */ }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF0F9D8B))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = report.result?.englishSummary ?: "No summary available",
                fontSize = 14.sp,
                color = Color(0xFF1D3B3A),
                maxLines = 3
            )
        }
    }
}
