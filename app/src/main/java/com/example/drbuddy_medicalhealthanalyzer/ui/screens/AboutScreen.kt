package com.example.drbuddy_medicalhealthanalyzer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F7F5))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Color(0xFF0F9D8B),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🩺", fontSize = 40.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Dr.Buddy AI",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1D3B3A),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Text(
                    text = "Your personal health informatics assistant",
                    fontSize = 14.sp,
                    color = Color(0xFF4A6A65),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(
                    color = Color(0xFFE0E8E5),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = "Dr.Buddy AI reads scanned reports and pasted clinical text, then returns a plain-language summary powered by Gemini. Built for quick, on-the-go review of lab results and reports.",
                    fontSize = 14.sp,
                    color = Color(0xFF1D3B3A),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "⚠️ DISCLAIMER: Dr.Buddy AI provides informational summaries only and is not a substitute for professional medical advice, diagnosis, or treatment. Always consult a qualified healthcare provider.",
                            fontSize = 13.sp,
                            color = Color(0xFFB9791A),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("v1.0", color = Color(0xFF8A9A95), fontSize = 12.sp)
                    Text("•", color = Color(0xFF8A9A95))
                    Text("Built with ❤️", color = Color(0xFF8A9A95), fontSize = 12.sp)
                }
            }
        }
    }
}