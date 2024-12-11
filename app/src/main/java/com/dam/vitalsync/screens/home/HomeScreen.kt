package com.dam.vitalsync.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.dam.vitalsync.R
import com.dam.vitalsync.model.DataViewModel
import kotlinx.coroutines.delay
import com.google.accompanist.pager.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dam.vitalsync.model.Datos
import com.dam.vitalsync.navigation.Screens
import com.dam.vitalsync.ui.theme.BlueStarted
import com.dam.vitalsync.ui.theme.CaribbeanBlue
import com.dam.vitalsync.ui.theme.Moraito
import com.dam.vitalsync.ui.theme.Pink40
import com.dam.vitalsync.ui.theme.Purple40
import com.dam.vitalsync.ui.theme.PurpleGrey40
import com.dam.vitalsync.ui.theme.PurpleGrey80
import com.dam.vitalsync.ui.theme.Rosipu
import com.github.tehras.charts.line.LineChart
import com.github.tehras.charts.line.LineChartData
import com.github.tehras.charts.line.renderer.line.SolidLineDrawer
import com.github.tehras.charts.line.renderer.xaxis.SimpleXAxisDrawer

@Composable
fun Home(navController: NavController, viewModel: DataViewModel = viewModel()) {
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val sliderValue = rememberSaveable { mutableStateOf(100f) }

    val dieta by viewModel.dieta.observeAsState(emptyMap())
    val showPopup = remember { mutableStateOf(false) }
    val viewModel: DataViewModel = viewModel()
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF181848)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            //modifier = Modifier.padding(32.dp)
        ) {

            ImageCarousel()


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButtonWithLabel(
                    icon = Icons.Default.Fastfood,
                    label = "Diet",
                    onClick = { showPopup.value = true }
                )
                if (showPopup.value) {
                    DietPlanPopup(
                        onDismiss = { showPopup.value = false },
                        onSave = { dietPlan ->
                            viewModel.saveDietPlanToFirestore(dietPlan)
                            showPopup.value = false
                        }
                    )
                }
                IconButtonWithLabel(
                    icon = Icons.Default.Bloodtype,
                    label = "Glucose",
                    onClick = { showDialog.value = true }
                )
            }


            Content(viewModel)

            BottomNavigationBar(navController)

        }


        if (showDialog.value) {
            GlucoseInputDialog(
                sliderValue = sliderValue,
                onConfirm = {
                    viewModel.addGlucoseLevel(sliderValue.value.toInt())
                    showDialog.value = false
                },
                onCancel = {
                    showDialog.value = false
                }
            )
        }

    }
}
@Composable
fun BottomNavigationBar(navController: NavController) {

        BottomNavigation(
            backgroundColor = PurpleGrey40,

            ) {
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Home",
                        tint = Color.White
                    )
                },

                selected = true,
                onClick = { navController.navigate(Screens.HomeScreen.name) }
            )
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = "Calendar",
                        tint = Color.White
                    )
                },

                selected = false,
                onClick = { navController.navigate(Screens.AgendaScreen.name) }
            )
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Default.Chat,
                        contentDescription = "Chat",
                        tint = Color.White
                    )
                },

                selected = false,
                onClick = { navController.navigate(Screens.ChatScreen.name) }
            )
            BottomNavigationItem(
                icon = {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White
                    )
                },

                selected = false,
                onClick = { navController.navigate(Screens.ProfileScreen.name) }
            )

    }
}
@Composable
fun Content(viewModel: DataViewModel) { //funcion contenedor para mostrar bien el grafico
    val glucoseLevels by viewModel.glucoseLevels.observeAsState(emptyList())

    Box(
        modifier = Modifier
            .size(255.dp)
            .background(Color.White)//Color(0XFF304878)
            //.clip(RoundedCornerShape(64.dp))
        ,

    ) {
        if (glucoseLevels.isNotEmpty()) {

            // el grafico recibe la lista de niveles de glucosa y la muestra
            Lineas(glucoseLevels = glucoseLevels)
        } else {
            Text(
                text = "No hay datos de glucosa disponibles",
                color = Color.Gray
            )
        }
    }
}

//grafica de glucosa
@Composable
fun Lineas(glucoseLevels: List<Pair<String, Int>>) {
    if (glucoseLevels.isEmpty()) {
        Text(
            text = "No hay datos de glucosa disponibles",
            color = Rosipu,
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val spacing = (glucoseLevels.size / 10).coerceAtLeast(1) // Muestra máximo 10 etiquetas
    val puntos = glucoseLevels.map { (_, level) ->
        LineChartData.Point(
            value = level.toFloat(),
            label = "",

        )
    }

    val lineas = listOf(
        LineChartData(
            points = puntos,
            lineDrawer = SolidLineDrawer(
                color = Moraito,
                thickness = 4.dp // Grosor
            ),
        )
    )

    LineChart(
        linesChartData = lineas,
        modifier = Modifier
            .padding(horizontal = 15.dp, vertical = 50.dp)
            .height(150.dp)
    )

}



@Composable
fun GlucoseInputDialog(
    sliderValue: MutableState<Float>,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(// dialog = pop up con el slider de la glucosa
        onDismissRequest = { onCancel() },
        title = { Text(text = "Enter Glucose Level", color = Color.White) },
        text = {
            Column {
                Text(text = "Adjust your glucose level:", color = Color.Gray)
                Slider(
                    value = sliderValue.value,
                    onValueChange = { sliderValue.value = it },
                    valueRange = 50f..300f, //rango
                    colors = SliderDefaults.colors(
                        thumbColor = BlueStarted,
                        activeTrackColor = CaribbeanBlue
                    )
                )
                Text(
                    text = "Selected: ${sliderValue.value.toInt()} mg/dL",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = "Save", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onCancel) {
                Text(text = "Cancel", color = Color.White)
            }
        },
        //backgroundColor = Color.DarkGray
    )
}




@Composable
fun ImageCarousel() {
    val images = listOf(
        R.drawable.post1,
        R.drawable.posteo,
        R.drawable.postin
    )
    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // Cambia cada 5 segundos
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % images.size)
        }
    }

    HorizontalPager(
        state = pagerState,
        count = images.size,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .width(150.dp)
    ) { page ->
        Image(
            painter = painterResource(images[page]),
            contentDescription = "Image $page",
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .shadow(8.dp, RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun IconButtonWithLabel(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(color = Purple40, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = label, tint = Color.White)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = Color.White, fontSize = 12.sp)
    }
}



@Composable
fun DietPlanPopup(
    onDismiss: () -> Unit,
    onSave: (Map<String, String>) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Agregar Plan de Dieta",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp),
                    color = Pink40
                )


                Box(modifier = Modifier.fillMaxSize()) {

                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    ) {

                        DietPlanForm(
                            onSubmit = { dietPlan ->
                                onSave(dietPlan)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))


                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}

@Composable
fun DietPlanForm(
    onSubmit: (Map<String, String>) -> Unit
) {

    val dietPlan = rememberSaveable {
        mutableStateOf(
            mapOf(
                "Monday" to "",
                "Tuesday" to "",
                "Wednesday" to "",
                "Thursday" to "",
                "Friday" to "",
                "Saturday" to "",
                "Sunday" to ""
            )
        )
    }



    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {

        dietPlan.value.forEach { (day, currentValue) ->
            TextInput(
                label = day,
                value = currentValue,
                onValueChange = { newValue ->
                    dietPlan.value = dietPlan.value + (day to newValue)
                },

            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))


        SubmitButton(
            textId = "Guardar Plan",
            inputValido = dietPlan.value.values.none { it.isBlank() }
        ) {
            onSubmit(dietPlan.value)
        }
    }
}

@Composable
fun TextInput(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = PurpleGrey80) },
        modifier = Modifier.fillMaxWidth(),

    )
}

@Composable
fun SubmitButton(
    textId: String,
    inputValido: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = inputValido,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = textId)
    }
}




