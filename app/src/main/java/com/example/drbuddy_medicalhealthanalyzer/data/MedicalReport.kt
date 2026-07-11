package com.example.drbuddy_medicalhealthanalyzer.data

import com.example.drbuddy_medicalhealthanalyzer.ui.screens.AnalysisResult
import java.util.Date

data class MedicalReport(
    val id: String = "",
    val date: Date = Date(),
    val result: AnalysisResult? = null,
    val type: String = "Unknown"
)
