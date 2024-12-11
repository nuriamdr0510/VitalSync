package com.dam.vitalsync.screens.profile

import android.content.Context
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.dam.vitalsync.screens.home.BottomNavigationBar
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

import com.dam.vitalsync.R
import com.dam.vitalsync.navigation.Screens
import com.dam.vitalsync.ui.theme.Pink40
import com.dam.vitalsync.ui.theme.Purple40
import com.dam.vitalsync.ui.theme.PurpleGrey80
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.rememberCoroutineScope
//import coil.compose.AsyncImage



import kotlinx.coroutines.tasks.await


@Composable
fun Profile(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var generatedPassword by remember { mutableStateOf("") }
    val context = LocalContext.current
/*
   val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }


    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))*/

    Box(modifier = Modifier.fillMaxSize()) {
        var name by remember { mutableStateOf("Cargando...") }
        var email by remember { mutableStateOf("Cargando...") }

        LaunchedEffect(Unit) {
            val user = FirebaseAuth.getInstance().currentUser

            if (user != null) {
                val userId = user.uid
                val db = FirebaseFirestore.getInstance()


                try {

                    val querySnapshot = db.collection("users")
                        .whereEqualTo("user_Id", userId)
                        .get()
                        .await()

                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        name = document.getString("nombre") ?: "Sin nombre"
                        email = document.getString("correo") ?: "Sin email"

                        Log.d("Firestore", "Datos obtenidos correctamente: $name, $email")
                    } else {
                        name = "Documento no encontrado"
                        email = "No disponible"

                        Log.e("Firestore", "No se encontró un documento con user_Id: $userId")
                    }
                } catch (e: Exception) {
                    name = "Error al cargar datos"
                    email = "Error"

                    Log.e("Firestore", "Error al obtener datos: ${e.message}")
                }
            } else {
                name = "Usuario no autenticado"
                email = "No disponible"

                Log.e("Auth", "El usuario no está autenticado.")
            }

        }






        Column(

            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF181848)) // Color de fondo
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Imagen de perfil
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable {
                        //pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }, // Abrir el selector de imágenes al hacer clic
                contentAlignment = Alignment.Center
            ) {

            }

            Spacer(modifier = Modifier.height(16.dp))

            // Nombre del usuario
            Text(
                text = name,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            // Correo del usuario
            Text(
                text = email,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botones interactivos

            ProfileButton(icon = R.drawable.bloquear, label = "Cambiar Contraseña") {
                val newPassword = generateRandomPassword() // Generar contraseña
                generatedPassword = newPassword
                updatePasswordInFirestore(newPassword, context) // Actualizar Firestore
                showPasswordDialog = true
            }
            ProfileButton(icon = R.drawable.llamar, label = "Tlfn") {}
            ProfileButton(icon = R.drawable.informacion, label = "Info") {
                showInfoDialog = true
            }
            ProfileButton(icon = R.drawable.salir, label = "Cerrar Sesión") {
                logOut(navController)

            }
            ProfileButton(icon = R.drawable.trash, label = "Eliminar Cuenta") {
                showDialog = true
            }


            if (showDialog) {
                ConfirmDeleteAccountDialog(
                    onConfirm = {
                        showDialog = false
                        //deleteAccount(navController)
                    },
                    onDismiss = {
                        showDialog = false
                    }
                )
            }
            // Diálogo informativo sobre la app
            if (showInfoDialog) {
                InfoDialog(
                    onDismiss = { showInfoDialog = false },
                    context = LocalContext.current // Pasar contexto para acceder a assets
                )
            }
            if (showPasswordDialog) {
                ShowNewPasswordDialog(
                    newPassword = generatedPassword,
                    onDismiss = { showPasswordDialog = false }
                )
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Esto coloca la barra en la parte inferior
                .fillMaxWidth()
        ) {
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun InfoDialog(onDismiss: () -> Unit, context: Context) {

    var infoText by remember { mutableStateOf("Cargando...") }

    LaunchedEffect(Unit) {
        infoText = loadTextFromAsset(context, "info.txt") ?: "Error al cargar información"
    }

    Dialog(onDismissRequest = { onDismiss() }) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.LightGray,
            elevation = 8.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
            ) {
                // Título
                Text(
                    text = "Información sobre la App",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Pink40
                )

                // Contenido
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = infoText,
                        style = MaterialTheme.typography.body1,
                        color = Color.Black
                    )
                }

                // Botón de Cerrar
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onDismiss() }) {
                        Text(text = "Cerrar", color = Color.Blue)
                    }
                }
            }
        }
    }
}

//  cargar texto desde un archivo de assets
fun loadTextFromAsset(context: Context, fileName: String): String? {
    return try {
        context.assets.open(fileName).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
@Composable
fun ProfileButton(icon: Int, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Purple40, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Start
        )
    }

}

fun logOut(navController: NavController) {
    FirebaseAuth.getInstance().signOut() // Cierra la sesión
    navController.navigate(Screens.StartScreen.name) {
        popUpTo(Screens.StartScreen.name) { inclusive = true } // Limpia la pila de navegación
    }
}
@Composable
fun ConfirmDeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Confirmar Eliminación", fontWeight = FontWeight.Bold)
        },
        text = {
            Text(text = "¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(text = "Eliminar", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(text = "Cancelar")
            }
        }
    )
}
/*@Composable
fun ConfirmPasswordChangeDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Cambiar Contraseña", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("¿Estás seguro de que deseas generar una nueva contraseña? Se actualizará en la base de datos y deberás guardarla.")
        },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}*/
fun updatePasswordInFirestore(newPassword: String, context: Context) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        val userId = user.uid
        val db = FirebaseFirestore.getInstance()
        val userRef = db.collection("users").document(userId)

        userRef.update("password", newPassword)
            .addOnSuccessListener {
                Log.d("Firestore", "Contraseña actualizada en Firestore.")
                Toast.makeText(context, "Contraseña actualizada.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al actualizar la contraseña: ${e.message}")
                Toast.makeText(context, "Error al actualizar la contraseña.", Toast.LENGTH_SHORT).show()
            }
    } else {
        Toast.makeText(context, "No se encontró un usuario autenticado.", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ShowNewPasswordDialog(newPassword: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(text = "Nueva Contraseña Generada")
        },
        text = {
            Text(
                text = "Tu nueva contraseña es:\n\n$newPassword\n\nGuárdala en un lugar seguro.",
                style = MaterialTheme.typography.body1
            )
        },
        confirmButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Entendido")
            }
        }
    )
}

fun generateRandomPassword(length: Int = 12): String {
    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#\$%^&*()_+"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}













