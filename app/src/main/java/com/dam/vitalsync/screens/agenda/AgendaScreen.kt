package com.dam.vitalsync.screens.agenda

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dam.vitalsync.screens.home.BottomNavigationBar
import android.os.Bundle
import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.Keep
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.Icon
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter

@Composable
fun Agenda(navController: NavController) {
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val currentDay = remember { getDayOfWeek() }

    val firebaseFirestore = FirebaseFirestore.getInstance()
    val userId = getCurrentUserId()

    // Cargar los ejercicios del usuario actual
    LaunchedEffect(currentDay, userId) {
        if (userId != "unknown_user") {
            firebaseFirestore.collection("exercises")
                .whereEqualTo("userId", userId)
                .whereEqualTo("day", currentDay)
                .get()
                .addOnSuccessListener { snapshot ->
                    exercises = snapshot.documents.mapNotNull { document ->
                        document.toObject(Exercise::class.java)?.copy(id = document.id)
                    }
                }
                .addOnFailureListener {
                    exercises = emptyList()
                }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir ejercicio")
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181848))
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = currentDay,
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Ejercicios para hoy",
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )
            if (exercises.isEmpty()) {
                Text(
                    text = "No hay ejercicios asignados para hoy.",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                LazyColumn {
                    items(exercises) { exercise ->
                        ExerciseItem(
                            name = exercise.name,
                            category = exercise.category,
                            onDelete = {
                                deleteExercise(
                                    exercise = exercise,
                                    firebaseFirestore = firebaseFirestore
                                ) {
                                    exercises = exercises.filter { it.id != exercise.id }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddDialog) {
            AddExerciseDialog(
                onDismiss = { showAddDialog = false },
                onAddExercise = { name, category, day ->
                    val newExercise = Exercise(
                        name = name,
                        category = category,
                        day = day,
                        userId = userId
                    )
                    firebaseFirestore.collection("exercises")
                        .add(newExercise)
                        .addOnSuccessListener { documentReference ->
                            val exerciseWithId = newExercise.copy(id = documentReference.id)
                            if (day == currentDay) {
                                exercises = exercises + exerciseWithId
                            }
                            showAddDialog = false
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirestoreError", "Error al guardar el ejercicio", e)
                        }
                }
            )
        }
    }
}





@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onAddExercise: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Tren superior") }
    var day by remember { mutableStateOf("Lunes") }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotEmpty()) {
                    onAddExercise(name, category, day)
                }
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        text = {
            Column {
                Text("Añadir ejercicio")
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del ejercicio") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Categoría: $category",
                        modifier = Modifier.clickable { expandedCategory = true }
                    )
                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        listOf("Tren superior", "Tren inferior", "Cardio").forEach { option ->
                            DropdownMenuItem(onClick = {
                                category = option
                                expandedCategory = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Día: $day",
                        modifier = Modifier.clickable { expandedDay = true }
                    )
                    DropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false }
                    ) {
                        listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo").forEach { option ->
                            DropdownMenuItem(onClick = {
                                day = option
                                expandedDay = false
                            }) {
                                Text(option)
                            }
                        }
                    }
                }
            }
        }
    )
}
@Keep
data class Exercise(
    val id: String? = null,
    val name: String = "",
    val category: String = "",
    val day: String = "",
    val userId: String = ""
)
@Composable
fun ExerciseItem(name: String, category: String, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        //elevation = 4.dp,
        //backgroundColor = Color(0xFF3E2065)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = name,
                        color = Color.White,
                        fontSize = 18.sp,
                        //fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = category,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar ejercicio",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

fun deleteExercise(
    exercise: Exercise,
    firebaseFirestore: FirebaseFirestore,
    onDeleted: () -> Unit
) {
    exercise.id?.let { documentId ->
        firebaseFirestore.collection("exercises")
            .document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d("FirestoreSuccess", "Ejercicio eliminado correctamente.")
                onDeleted()
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreError", "Error al eliminar el ejercicio", e)
            }
    } ?: Log.e("FirestoreError", "El ejercicio no tiene un ID válido.")
}




fun getDayOfWeek(): String {
    val days = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
    val calendar = java.util.Calendar.getInstance()
    return days[(calendar.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7] // Ajustar para que Lunes sea el inicio
}

fun getCurrentUserId(): String {
    // Aquí deberías integrar el sistema de autenticación de Firebase
    return FirebaseAuth.getInstance().currentUser?.uid ?: "unknown_user"
}








