package com.dam.vitalsync.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val _dieta = MutableLiveData<Map<String, Any>>(emptyMap())
    val dieta: LiveData<Map<String, Any>> = _dieta
    private val _glucoseLevels = MutableLiveData<List<Pair<String, Int>>>()
    val glucoseLevels: LiveData<List<Pair<String, Int>>> = _glucoseLevels

    public fun saveDietPlanToFirestore(dietPlan: Map<String, String>) {
        val userId = auth.currentUser?.uid
        val dietPlanWithUserId = dietPlan + ("user_id" to userId)

        FirebaseFirestore.getInstance()
            .collection("dietPlans")
            .add(dietPlanWithUserId)
            .addOnSuccessListener { documentReference ->
                Log.d("MyDietPlan", "Plan de dieta guardado con ID: ${documentReference.id}")
            }
            .addOnFailureListener { exception ->
                Log.d("MyDietPlan", "Error al guardar el plan de dieta: ${exception.message}")
            }
    }


    init {
        fetchGlucoseLevels()
    }


    fun addGlucoseLevel(level: Int) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User not authenticated. Cannot save glucose level.")
            return
        }

        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val glucoseData = mapOf(
            "level" to level,
            "timestamp" to timestamp,
            "userId" to userId
        )

        firestore.collection("glucose_levels")
            .add(glucoseData)
            .addOnSuccessListener {
                Log.d("Firebase", "Glucose level saved: $glucoseData")
                fetchGlucoseLevels() //refresca
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error saving glucose level", e)
            }
    }

    fun getDietFromFirestore(userId: String, onComplete: (Map<String, String>) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("dietPlans")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    val data = document.data?.filterKeys { it != "user_id" } as? Map<String, String> ?: emptyMap()
                    onComplete(data)
                } else {
                    onComplete(emptyMap())
                }
            }
            .addOnFailureListener {
                onComplete(emptyMap())
            }
    }



    private fun fetchGlucoseLevels() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Log.e("Firebase", "User not authenticated. Cannot fetch glucose levels.")
            _glucoseLevels.value = emptyList()
            return
        }

        firestore.collection("glucose_levels")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp")
            .get()
            .addOnSuccessListener { snapshot ->
                val levels = snapshot.documents.mapNotNull { doc ->
                    val level = doc.getLong("level")?.toInt()
                    val timestamp = doc.getString("timestamp")
                    if (level != null && timestamp != null) Pair(timestamp, level) else null
                }
                _glucoseLevels.value = levels
                Log.d("Firebase", "Fetched glucose levels: $levels") // Agrega un log para verificar los datos
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error fetching glucose levels", e)
            }
    }



}