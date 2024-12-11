package com.dam.vitalsync.screens.chat

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import com.dam.vitalsync.screens.home.BottomNavigationBar


@Composable
fun Chat(navController: NavController) {
    var message by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var entrenadorId by remember { mutableStateOf("") }
    var chatId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val firebaseFirestore = FirebaseFirestore.getInstance()
    val currentUserId = "currentUserId" // Reemplázalo con el ID del usuario actual

    // Obtener el ID del Entrenador
    LaunchedEffect(Unit) {
        firebaseFirestore.collection("users")
            .whereEqualTo("tipoEntrenador", true)
            .get()
            .addOnSuccessListener { snapshot ->
                val entrenador = "SAvAnlQHSFOOLV6HznudGVIZ5Qe2"
                if (entrenador != null) {
                    entrenadorId = entrenador
                    chatId = "${currentUserId}_$entrenadorId" // Crear un ID único para este chat
                    isLoading = false
                } else {
                    errorMessage = "No se encontró un entrenador."
                    isLoading = false
                }
            }
            .addOnFailureListener { exception ->
                errorMessage = "Error al cargar el entrenador: ${exception.message}"
                isLoading = false
            }
    }

    // Cargar Mensajes en Tiempo Real
    LaunchedEffect(chatId) {
        if (chatId.isNotEmpty()) {
            firebaseFirestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        errorMessage = "Error al cargar mensajes: ${error.message}"
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        messages = snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                    }
                }
        }
    }

    Scaffold(
        bottomBar = {
            Column {
                ChatInput(
                    message = message,
                    onMessageChange = { message = it },
                    onSendMessage = {
                        if (message.isNotEmpty() && chatId.isNotEmpty()) {
                            val newMessage = Message(
                                senderId = currentUserId,
                                text = message,
                                timestamp = System.currentTimeMillis()
                            )
                            firebaseFirestore.collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .add(newMessage)
                            message = ""
                        }
                    }
                )
                BottomNavigationBar(navController = navController)
            }

        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFECECEC))
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (errorMessage.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = errorMessage, color = Color.Red)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    reverseLayout = true
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            message = message,
                            isCurrentUser = message.senderId == currentUserId
                        )
                    }
                }
            }
        }
    }


}

@Composable
fun ChatBubble(message: Message, isCurrentUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (isCurrentUser) Color(0xFFBBDEFB) else Color(0xFFFFF9C4),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            Text(
                text = message.text,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ChatInput(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White, shape = RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = onMessageChange,
            placeholder = { Text("Escribe un mensaje...") },
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        IconButton(onClick = onSendMessage) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar",
                tint = Color(0xFF673AB7)
            )
        }
    }
}

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L
)


