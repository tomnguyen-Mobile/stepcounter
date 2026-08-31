package edu.sdgku.stepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import edu.sdgku.stepcounter.shared.data.FirebaseRepository
import edu.sdgku.stepcounter.ui.dashboard.PhoneCompanionApp

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi:: class)
    override fun onCreate(
        savedInstanceState: Bundle?
        ) {
        super.onCreate(savedInstanceState)
        val repository: FirebaseRepository = FirebaseRepository()
//        repository.updateDailyGoal(
//            dailyGoal = 55555,
//            onSuccess = {
//                Log.d(
//                    "sharedFirebase",
//                    "Goal updated from mobile")
//                },
//            onError = { exception ->
//                Log.d(
//                    "SharedFirebased",
//                    "Cloud not update goal",
//                    exception,
//                    ) // end of log.d
//                },
//            ) // end of repository.updatedailygoal
        setContent {
            val widthSizeClass = calculateWindowSizeClass(this).widthSizeClass
            MaterialTheme {
                PhoneCompanionApp(
                    repository = repository,
                    widthSizeClass =  widthSizeClass
                )
            }
        }
    }
}

//@Composable
//fun PhoneCompanionApp(repository: FirebaseRepository) {
//    val context = LocalContext.current
//    var stepsGoal by remember { mutableIntStateOf(10000) }
//    var sendStatus by remember { mutableStateOf("Not Sent") }
//
//
//    DisposableEffect(repository) {
//        val listenerRegistration = repository.listenToFitnessData(
//            onDataChange = { fitnessData ->
//                stepsGoal = fitnessData.dailyGoal.toInt()
//                sendStatus = "Goal received from Firebase: $stepsGoal"
//            },
//            onError = { exception ->
//                sendStatus = "Firebase listener error: " + (exception.message ?: "unknown error")
//            },
//        )
//
//        onDispose {
//            listenerRegistration.remove()
//        }
//    }
//
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(24.dp),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//        Text(text = "Wear Fitness", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(24.dp))
//        Text(text = "Steps Goal", style = MaterialTheme.typography.titleMedium)
//        Spacer(modifier = Modifier.height(12.dp))
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.Center
//        ) {
//            Button(
//                onClick = {
//                    if (stepsGoal > 500) {
//                        stepsGoal -= 500
//                        }
//                    }) { // end of button arguments signature
//                Text("-")
//                } // end of button
//            Spacer(modifier = Modifier.width(16.dp))
//            Text(text = stepsGoal.toString(), style = MaterialTheme.typography.headlineSmall)
//            Spacer(modifier = Modifier.width(16.dp))
//            Button(
//                onClick = {
//                    stepsGoal += 500
//                    }) {
//                    Text("+")
//                } // end of onClick
//            } // end of row
//        Spacer(modifier = Modifier.height(24.dp))
//        Button(
//            onClick = {
//                sendStatus = "Sending..."
//                sendStepsGoalToWatch(context = context, stepsGoal = stepsGoal, onSuccess = {
//                    sendStatus = "Sent $stepsGoal to the watch"
//                }, onError = { errorMessage ->
//                    sendStatus = "Error: $errorMessage"
//                })
//            }) {
//            Text("Send to Watch ")
//        }
//
//        // start of button for saving to firebase
//        Button(
//            onClick = {
//                sendStatus = "Saving to Firebase..."
//                repository.updateDailyGoal(
//                    dailyGoal = stepsGoal.toLong(),
//                    onSuccess = { sendStatus = "Saved $stepsGoal in Firebase"},
//                    onError = { exception -> sendStatus = "Firebase error: " + (exception.message ?: "unknown error") }
//                )
//            },
//        ){
//            Text("Save to Firebase")
//        }
//
//        // end of button for saving to firebase
//        Spacer(modifier = Modifier.height(24.dp))
//        Text(text = "Status: $sendStatus")
//    }
//}