package com.dam.vitalsync.screens.start

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dam.vitalsync.R
import com.dam.vitalsync.navigation.Screens
import com.dam.vitalsync.screens.session.LoginScreenViewModel
import com.dam.vitalsync.ui.theme.BlueStarted
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider


@Composable
fun Start(navController: NavController,
          viewModel: LoginScreenViewModel = viewModel()
          ){
    LaunchedEffect(Unit) {
        // verifica la autenticación
        viewModel.initCheck()
    }


    val isAuthenticated by viewModel.isAuthenticated.observeAsState(null)


    if (isAuthenticated == null) {
        // pantalla de carga
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BlueStarted)
        }
    } else {

        if (isAuthenticated as Boolean) {
            LaunchedEffect(isAuthenticated) {
                navController.navigate("HomeScreen") {
                    popUpTo("StartScreen") { inclusive = true }
                }
            }
        } else {
            val token = "19636702755-8qp9htb0v7us543beuqdbnmsq3s49cck.apps.googleusercontent.com"
            val context = LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts
                    .StartActivityForResult()
            ) {
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(it.data)

                try {
                    val account = task.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    viewModel.signInWithGoogleCredential(credential) {
                        navController.navigate(Screens.HomeScreen.name)
                    }
                } catch (ex: Exception) {
                    Log.d("My Login", "GoogleSignIn falló")
                }
            }
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 30.dp),
                color = Color.Black
            ) {


                Image(
                    painter = painterResource(id = R.drawable.nightcity),
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(0.4f),  // opacidad
                    contentScale = ContentScale.Crop
                )

                Column(

                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(25.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 40.dp)
                    ){
                        Icon(
                            painter = painterResource(R.drawable.vitalsynclogo),
                            contentDescription = "logo",
                            modifier = Modifier
                                .size(50.dp)
                                .clickable {  }
                            ,
                            tint = Color.Unspecified,
                        )

                        Text(
                            text = "VitalSync",
                            fontFamily = FontFamily.SansSerif,
                            color = Color.White,
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp,
                                letterSpacing = 0.5.sp,
                                lineHeight = 40.sp,

                                )
                        )

                    }
                    Spacer(modifier = Modifier.height(45.dp))
                    Text(
                        text = "Let´s Get",
                        fontFamily = FontFamily.SansSerif,
                        color = Color.White,
                        style = TextStyle(
                            fontSize = 40.sp,
                            letterSpacing = 0.5.sp,
                            lineHeight = 40.sp,

                            )
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "Started!",
                        fontFamily = FontFamily.SansSerif,
                        color = BlueStarted,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 45.sp,
                            letterSpacing = 0.5.sp,
                            lineHeight = 40.sp,

                            )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(onClick = {navController.navigate(Screens.LoginScreen.name)},
                        modifier = Modifier.border(2.dp, Color.Black).width(200.dp),
                        shape = CutCornerShape(5.dp)
                    ){
                        Text("SIGN IN")
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(text = "OR SIGN IN WITH", color = Color.White, style = TextStyle(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(10.dp))
                    Icon(
                        painter = painterResource(R.drawable.buscar),
                        contentDescription = "Google",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {

                                val opciones = GoogleSignInOptions
                                    .Builder(
                                        GoogleSignInOptions.DEFAULT_SIGN_IN
                                    )
                                    .requestIdToken(token)
                                    .requestEmail()
                                    .build()

                                val googleSingInCliente = GoogleSignIn.getClient(context, opciones)
                                launcher.launch(googleSingInCliente.signInIntent)
                            },

                        tint = Color.Unspecified,

                        )
                    Divider(modifier = Modifier.fillMaxWidth().padding(32.dp), color = Color(0xFFf4f4f4))

                    Column (horizontalAlignment = Alignment.CenterHorizontally){



                        Text(text = "DIDN´T HAVE AN ACCOUNT?", color = Color.White, style = TextStyle(fontWeight = FontWeight.Bold))
                        TextButton(
                            onClick = {

                                navController.navigate(Screens.SignUpScreen.name)


                            }
                        ) {

                            Text(text = "SIGN UP NOW", color = BlueStarted)
                        }


                    }
                }
            }
        }

        }
    }





