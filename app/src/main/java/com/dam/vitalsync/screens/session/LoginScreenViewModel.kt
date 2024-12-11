package com.dam.vitalsync.screens.session

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dam.vitalsync.model.User
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import java.util.Calendar

class LoginScreenViewModel : ViewModel(){
    // OJOO!!! DAR PERMISO INTERNET EN MANIFEST
    private val auth: FirebaseAuth = Firebase.auth //la estaremos usando a lo largo del proyecto
    // impide que se creen varios usuarios accidentalmente
    private val _loading = MutableLiveData(false)
    val _isAuthenticated = MutableLiveData<Boolean>(false)
    val isAuthenticated: LiveData<Boolean> get() = _isAuthenticated

    // Función para verificar si el usuario está autenticado
    fun checkUserAuthentication() {
        val user = auth.currentUser
        _isAuthenticated.value = user != null
    }

    // Llamada para hacer la verificación al inicio
    fun initCheck() {
        checkUserAuthentication()
    }

    fun signInWithGoogleCredential(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task -> //si la tarea tuve exito escribimos mensaje en log
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Google logueado!!!!")
                            home()
                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithGoogle: ${task.result.toString()}"
                            )
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Google: ${ex.message}")
            }
        }

    fun signInWithFacebook(credential: AuthCredential, home: () -> Unit) =
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task -> //si la tarea tuve exito escribimos mensaje en log
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "Facebook logueado!!!!")
                            home()
                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithFacebook: ${task.result.toString()}"
                            )
                        }
                    }
            } catch (ex: Exception) {
                Log.d("MyLogin", "Error al loguear con Facebook: ${ex.message}")
            }
        }

    fun signInWithEmailAndPassword(email: String, password: String, home: () -> Unit) =
        viewModelScope.launch { //para que se ejecute en segundo plano
            try {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("MyLogin", "signInWithEmailAndPassword logueado!!!!")
                            home()
                        } else {
                            Log.d(
                                "MyLogin",
                                "signInWithEmailAndPassword: ${task.result.toString()}"
                            )
                        }
                    }

            } catch (ex: Exception) {
                Log.d("MyLogin", "signInWithEmailAndPassword: ${ex.message}")
            }
        }


    fun createUserWithEmailAndPassword(
        year: Int,
        month: Int,
        day: Int,
        nombre: String,
        email: String,
        password: String,
        home: () -> Unit
    ) {
        if (email.isBlank() || password.isBlank()) {
            Log.e("SignUp", "Email or password is empty.")
            return
        }

        if (_loading.value == false) {
            _loading.value = true
            Log.d("SignUp", "Starting user creation...")

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("SignUp", "User created successfully: ${auth.currentUser?.uid}")
                        val username = nombre.split(" ").getOrNull(0) ?: ""
                        val edad = calcularEdad(year, month, day)
                        createUser(username, edad, email, password, tipo = true)
                        home()
                    } else {
                        Log.e("SignUp", "Error creating user: ${task.exception?.message}")
                    }
                    _loading.value = false
                }
        } else {
            Log.d("SignUp", "User creation is already in progress.")
        }
    }


    private fun createUser(nombre: String, edad: Int, correo: String, password: String, tipo: Boolean) {
        val userId = auth.currentUser?.uid

        val user = User(
            userId = userId.toString(),
            nombre = nombre,
            edad = edad,
            correo = correo,
            password = password,
            id = null,
            tipoEntrenador = false
        ).toMap()

        FirebaseFirestore.getInstance()
            .collection("users") // con esto referenciamos la coleccion que creamos cloud Firestore
            .add(user)
            .addOnSuccessListener {
                Log.d("MyLogin", "Creado ${it.id}")
            }
            .addOnFailureListener {
                Log.d("MyLogin", "Ocurrió Error: ${it}")
            }


    }
    fun calcularEdad(year: Int, month: Int, dayOfMonth: Int): Int {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        var edad = currentYear - year


        if (currentMonth < month - 1 || (currentMonth == month - 1 && currentDay < dayOfMonth)) {
            edad -= 1
        }

        return edad
    }

}