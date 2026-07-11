package com.example.drbuddy_medicalhealthanalyzer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MedicalServices
import com.google.firebase.FirebaseApp
import com.example.drbuddy_medicalhealthanalyzer.ui.screens.AboutScreen
import com.example.drbuddy_medicalhealthanalyzer.ui.screens.AnalyzeScreen
import com.example.drbuddy_medicalhealthanalyzer.ui.screens.HistoryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            // Use MaterialTheme's built-in color scheme
            MaterialTheme(
                colorScheme = lightColorScheme(),
                typography = Typography()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DrBuddyApp()
                }
            }
        }
    }
}

@Composable
fun DrBuddyApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "analyze",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("analyze") { AnalyzeScreen() }
            composable("history") { HistoryScreen() }
            composable("about") { AboutScreen() }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(74.dp)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFF4A6A65)) },
            label = { Text("History", fontSize = 11.sp, color = Color(0xFF4A6A65)) },
            selected = false,
            onClick = { navController.navigate("history") }
        )

        NavigationBarItem(
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color(0xFF0F9D8B),
                            shape = RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MedicalServices, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            },
            label = { Text("Analyze", fontSize = 11.sp, color = Color(0xFF0F9D8B)) },
            selected = true,
            onClick = { navController.navigate("analyze") }
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF4A6A65)) },
            label = { Text("About", fontSize = 11.sp, color = Color(0xFF4A6A65)) },
            selected = false,
            onClick = { navController.navigate("about") }
        )
    }
}
