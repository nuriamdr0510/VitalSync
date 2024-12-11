package com.dam.vitalsync.screens.session

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.IconButton
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dam.vitalsync.R
import com.dam.vitalsync.navigation.Screens
import com.dam.vitalsync.ui.theme.BlueStarted
import java.util.Calendar


@SuppressLint("SuspiciousIndentation")
@Composable

fun SignUp(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        val username = rememberSaveable { mutableStateOf("") }
        val birthday = rememberSaveable { mutableStateOf("") }
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        val confirmPassword = rememberSaveable { mutableStateOf("") }

        Image(
            painter = painterResource(id = R.drawable.nightcity),
            contentDescription = "Background Image",
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.4f),  // opacidad
            contentScale = ContentScale.Crop
        )

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        //imageVector = Icons.Default.ArrowBack,
                        painter = painterResource(R.drawable.rebobinar),
                        contentDescription = "Back",
                        tint = Color.Unspecified,
                        modifier = Modifier.clickable { navController.popBackStack() }.size(32.dp)
                    )
                }

                Text(
                    text = "Create an",
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    style = TextStyle(
                        fontSize = 30.sp,
                        letterSpacing = 0.5.sp,
                        lineHeight = 40.sp,

                        )
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Account",
                    fontFamily = FontFamily.SansSerif,
                    color = BlueStarted,
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        letterSpacing = 0.5.sp,
                        lineHeight = 40.sp,

                        )
                )


                Spacer(modifier = Modifier.height(32.dp))


                InputField(
                    value = username,
                    placeholder = "Username",
                    leadingIcon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(16.dp))

                BirthdayPickerButton(birthday)
                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = email,
                    placeholder = "Email Address",
                    leadingIcon = Icons.Default.Email
                )
                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = password,
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                InputField(
                    value = confirmPassword,
                    placeholder = "Confirm Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )
                Spacer(modifier = Modifier.height(32.dp))


                Button(
                    onClick = {
                        val parts = birthday.value.split("-")
                        if (username.value.isBlank() || email.value.isBlank() || password.value.isBlank()) {
                            Log.e("SignUp", "All fields must be filled.")
                        } else if (password.value != confirmPassword.value) {
                            Log.e("SignUp", "Passwords do not match.")
                        } else if (parts.size != 3) {
                            Log.e("SignUp", "Invalid birthday format.")
                        } else {
                            Log.d("SignUp", "Attempting to create user...")
                            viewModel.createUserWithEmailAndPassword(
                                year = parts[0].toInt(),
                                month = parts[1].toInt(),
                                day = parts[2].toInt(),
                                nombre = username.value,
                                email = email.value,
                                password = password.value
                            ) {
                                navController.navigate(Screens.HomeScreen.name)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF880E4F)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "SIGN UP", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account?",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = " Sign In",
                        color = BlueStarted,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {

                            navController.navigate(Screens.LoginScreen.name)
                        }
                    )
                }
            }
        }
    }

@Composable
fun BirthdayPickerButton(birthdayState: MutableState<String>) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    Button(
        onClick = {
            DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->

                    birthdayState.value = "$year-${month + 1}-$dayOfMonth"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
    ) {
        Text(
            text = if (birthdayState.value.isEmpty()) "Select Birthday" else birthdayState.value,
            color = Color.White
        )
    }
}

@Composable
fun InputField(
    placeholder: String,
    leadingIcon: ImageVector,
    value: MutableState<String>,
    isPassword: Boolean = false,
    isDateField: Boolean = false
) {
    val input = rememberSaveable { mutableStateOf("") }
    val isPasswordVisible = rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value.value,
        onValueChange = { value.value = it },
        /*value = input.value,
        onValueChange = { input.value = it },*/
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = placeholder,
                tint = Color.Gray
            )
        },
        visualTransformation = if (isPassword && !isPasswordVisible.value) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = {
                    isPasswordVisible.value = !isPasswordVisible.value
                }) {
                    Icon(
                        imageVector = if (isPasswordVisible.value) {
                            Icons.Default.Visibility
                        } else {
                            Icons.Default.VisibilityOff
                        },
                        contentDescription = "Toggle Password Visibility",
                        tint = Color.Gray
                    )
                }
            }
        } else null,
        readOnly = isDateField,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color(0xFF1C1C1C),
                shape = RoundedCornerShape(8.dp)
            ),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = BlueStarted, //BORDE DE LOS INPUT
            unfocusedBorderColor = Color.Gray,
            textColor = Color.White,
            cursorColor = Color.White,
            placeholderColor = Color.Gray
        )
    )
}

