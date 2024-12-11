package com.dam.vitalsync.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dam.vitalsync.screens.agenda.Agenda
import com.dam.vitalsync.screens.chat.Chat
//import com.dam.vitalsync.screens.home.DietaScreen
import com.dam.vitalsync.screens.home.Home
import com.dam.vitalsync.screens.session.LoginScreen
import com.dam.vitalsync.screens.profile.Profile
import com.dam.vitalsync.screens.session.SignUp
import com.dam.vitalsync.screens.splash.Splash
import com.dam.vitalsync.screens.start.Start
import com.google.firebase.auth.FirebaseAuth

@Composable
fun Navigation() {
    val auth = FirebaseAuth.getInstance()
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screens.StartScreen.name
    ) {

        composable("splash") {
            Splash {
                val currentUser = auth.currentUser
                if (currentUser != null) {
                    navController.navigate("inicio") {
                        popUpTo("splash") { inclusive = true }
                    }
                } else {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            }
        }
        composable(Screens.StartScreen.name){
            Start(navController = navController)
        }
        composable(Screens.LoginScreen.name){
            LoginScreen(navController = navController)
        }
        composable(Screens.SignUpScreen.name){
            SignUp(navController = navController)
        }
        composable(Screens.HomeScreen.name){
            Home(navController = navController)
        }
        composable(Screens.AgendaScreen.name){
            Agenda(navController = navController)
        }
        composable(Screens.ChatScreen.name){
            Chat(navController = navController)
        }
        composable(Screens.ProfileScreen.name){
            Profile(navController = navController)
        }



    }


}