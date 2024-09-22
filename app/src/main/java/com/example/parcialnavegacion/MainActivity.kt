package com.example.parcialnavegacion

import android.os.Bundle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import java.text.SimpleDateFormat
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
          val navController = rememberNavController()
            NavHost(navController = navController, startDestination =  "PersonalData") {
                composable("PersonalData") { PersonalData(navController) }
                composable("Reservation/{name}/{number}")
                {
                    backStatEntry -> Reservation(
                        navController = navController,
                        name = backStatEntry.arguments?.getString("name") ?: "",
                        number = backStatEntry.arguments?.getString("number") ?: ""
                    )
                }
                composable("ReservationDetails/{name}/{number}/{date}/{time}")
                {
                    backStatEntry -> ReservationDetails(
                        name = backStatEntry.arguments?.getString("name") ?: "",
                        number = backStatEntry.arguments?.getString("number") ?: "",
                        date = backStatEntry.arguments?.getString("date") ?: "",
                        time = backStatEntry.arguments?.getString("time") ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun PersonalData (navController: NavController) {
    var name by remember { mutableStateOf("") }
    var number by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
            OutlinedTextField(value = number, onValueChange = {
                if (it.length <= 10) {
                    number = it
                }
            }, label = { Text("Number") })

            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                if (name.isNotEmpty() && number.length == 10) {
                    navController.navigate("Reservation/$name/$number")
                }
            }) {
                Text(text = "Continuar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Reservation(navController: NavController, name: String, number: String) {
    val dateState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)
    val timeState = rememberTimePickerState()

    // Revisamos si se seleccionó una fecha
    val selectedDate = dateState.selectedDateMillis?.let {
        Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
    } ?: ""

    // Formateamos la fecha
    val date = if (selectedDate is LocalDate) {
        selectedDate.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")).toString()
    } else {
        selectedDate.toString()
    }

    val time = "${timeState.hour}:${timeState.minute}"

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DatePicker(
                state = dateState,
            )

            Spacer(modifier = Modifier.padding(8.dp))

            TimePicker(state = timeState)

            Spacer(modifier = Modifier.padding(8.dp))

            Button(onClick = {
                if (date.isNotEmpty() && time.isNotEmpty()) {
                    navController.navigate("ReservationDetails/$name/$number/$date/$time")
                }
            }) {
                Text(text = "Confirmar Reserva")
            }
        }
    }
}

@Composable
fun ReservationDetails(name: String, number: String, date: String, time: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            OutlinedTextField(value = name, onValueChange = {}, readOnly = true, label = { Text("Nombrea") })
            OutlinedTextField(value = number, onValueChange = {}, readOnly = true, label = { Text("Número") })
            OutlinedTextField(value = date, onValueChange = {}, readOnly = true, label = { Text("Fecha") })
            OutlinedTextField(value = time, onValueChange = {}, readOnly = true, label = { Text("Hora") })
        }
    }
}