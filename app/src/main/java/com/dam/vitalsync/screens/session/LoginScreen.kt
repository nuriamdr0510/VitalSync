package com.dam.vitalsync.screens.session

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.text.input.KeyboardType
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

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginScreenViewModel = viewModel()
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        val email = rememberSaveable { mutableStateOf("") }
        val password = rememberSaveable { mutableStateOf("") }
        // Fondo de la pantalla
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

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Welcome",
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
                    text = "Back!",
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


                LoginInputField(
                    placeholder = "Email Address or Username",
                    leadingIcon = Icons.Default.Email,
                    inputState = email
                )
                Spacer(modifier = Modifier.height(16.dp))

                LoginInputField(
                    placeholder = "Password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    inputState = password
                )
                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val rememberChecked = rememberSaveable { mutableStateOf(false) }
                    Checkbox(
                        checked = rememberChecked.value,
                        onCheckedChange = { rememberChecked.value = it },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = Color.Magenta,
                            uncheckedColor = Color.Gray
                        )
                    )
                    Text(
                        text = "Remember",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))


                Button(
                    onClick = {
                        viewModel.signInWithEmailAndPassword(
                            email = email.value,
                            password = password.value
                        ) {
                            navController.navigate("HomeScreen")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF880E4F)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "LOG IN", color = Color.White, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Forgot Password?",
                    color = BlueStarted,
                    fontSize = 14.sp,
                    modifier = Modifier.clickable {
                        // recuperación de contraseña
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))


                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Don’t have an account?",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = " Sign up",
                        color = BlueStarted,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable {
                            
                            navController.navigate(Screens.SignUpScreen.name)
                        }
                    )
                }
            }
        }
    }


@Composable
fun LoginInputField(
    placeholder: String,
    leadingIcon: ImageVector,
    inputState: MutableState<String>,
    isPassword: Boolean = false
) {
    val input = rememberSaveable { mutableStateOf("") }
    val isPasswordVisible = rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = inputState.value,
        onValueChange = { inputState.value = it },
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
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
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = Color(0xFF1C1C1C),
                shape = RoundedCornerShape(8.dp)
            ),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Magenta,
            unfocusedBorderColor = Color.Gray,
            textColor = Color.White,
            cursorColor = Color.White,
            placeholderColor = Color.Gray
        )
    )
}
