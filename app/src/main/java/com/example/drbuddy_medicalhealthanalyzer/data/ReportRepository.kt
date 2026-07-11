package com.example.drbuddy_medicalhealthanalyzer.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ReportRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val reportCollection = firestore.collection("reports")

    suspend fun saveReport(report: MedicalReport) {
        try {
            reportCollection.add(report).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getReports(): Flow<List<MedicalReport>> = callbackFlow {
        val subscription = reportCollection
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reports = snapshot.toObjects(MedicalReport::class.java)
                    trySend(reports)
                }
            }
        awaitClose { subscription.remove() }
    }
}
