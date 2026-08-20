package com.mdi2.stepcounter

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

@Composable
fun WearFitnessApp(
    heartRateSensorValue: Int,
    hasHeartRateSensor: Boolean,

    ){
    val  navController = rememberNavController() // the navigation will remember the states
    val context = LocalContext.current

    var steps by remember { mutableIntStateOf(0)}
    var stepsGoal by remember { mutableIntStateOf(10000) }
    var calories by remember { mutableIntStateOf(0) }
    var caloriesGoal by remember { mutableIntStateOf(500) }
    var manualHeartRate by remember { mutableIntStateOf(72) }

    val  displayedHeartRate = if (hasHeartRateSensor) { // conditionally assigned
            heartRateSensorValue
        } else {
            manualHeartRate
        }

    var heartRateNotificationSent by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember { mutableStateOf(
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PERMISSION_GRANTED
    )}

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ){
            isGranted -> notificationPermissionGranted = isGranted
    }

    LaunchedEffect(Unit){
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            !notificationPermissionGranted
        ){
            notificationPermissionLauncher.launch (android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(
        displayedHeartRate,
        notificationPermissionGranted
    ){
        if (
            displayedHeartRate >= 100 &&
            !heartRateNotificationSent &&
            notificationPermissionGranted
        ){
            showNotification(
                context = context,
                notificationId = HEART_RATE_NOTIFICATION_ID,
                title = "High Heart Rate Detected",
                message = "Your Heart Rate reached $displayedHeartRate BPM"
            )
            heartRateNotificationSent = true
        }
        if (displayedHeartRate < 100){
            heartRateNotificationSent = false
        }
    }

    SwipeNavigationController(
        navController = navController
    ) {
        NavHost(
            navController = navController,
            startDestination = "progress",
        ){
            composable("progress"){
                DailyProgressScreen(
                    steps = steps,
                    calories = calories,
                    stepsGoal = stepsGoal,
                    caloriesGoal = caloriesGoal,
                    onAddStep = {
                        steps++
                        calories++
                    }
                )
            }
            composable("heart"){
                HeartRateScreen(
                    heartRate = displayedHeartRate,
                    hasHeartRateSensor = hasHeartRateSensor,
                    onDecreaseHeartRate = { manualHeartRate-- },
                    onIncreaseHeartRate = { manualHeartRate++ }
                )
            }
            composable("goals"){
                ModifyGoalScreen(
                    stepsGoal = stepsGoal,
                    caloriesGoal = caloriesGoal,
                    onDecreaseStepsGoal = { stepsGoal -= 500 },
                    onIncreaseStepsGoal = { stepsGoal += 500 },
                    onDecreaseCaloriesGoal = { caloriesGoal -= 50 },
                    onIncreaseCaloriesGoal = { caloriesGoal += 50 },
                )
            }
        }
    }
}

@Composable
fun SwipeNavigationController(
    navController: NavHostController,
    content: @Composable () -> Unit,
){
    val routes = listOf("progress","heart","goals") // the order here is the order of the screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "progress"
    val currentIndex = routes.indexOf(currentRoute)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(currentRoute){
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = {totalDrag = 0f},
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalDrag < -60 && currentIndex < routes.lastIndex){
                            navController.navigate( routes[currentIndex+1] ){
                                launchSingleTop = true
                            }
                        }
                        if (totalDrag > 60 && currentIndex > 0){
                            navController.navigate( routes[currentIndex-1] ){
                                launchSingleTop = true
                            }
                        }
                    }
                ) // end of detect horizontal drag gestures
            },
        contentAlignment = Alignment.Center
    ){// end of button arguments
        content()
    }
}

@Composable
fun DailyProgressScreen(
    steps: Int,
    stepsGoal: Int,
    calories: Int,
    caloriesGoal: Int,
    onAddStep: () -> Unit,
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Daily Progress",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Steps",
            color = Color.White,
        )
        Text(
            text = "$steps / $stepsGoal",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$calories / $caloriesGoal",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick=onAddStep,
        ) {
            Text("Add Step")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Swipe ->",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun HeartRateScreen(
    heartRate: Int,
    hasHeartRateSensor: Boolean,
    onDecreaseHeartRate: () -> Unit,
    onIncreaseHeartRate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Heart Rate",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "$heartRate BPM",
            color = Color.White,
            style = MaterialTheme.typography.displayMedium
        )
        if (!hasHeartRateSensor) {
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = onDecreaseHeartRate) {
                    Text("-")
                }
                Spacer(modifier = Modifier.height(6.dp))

                Button(onClick = onIncreaseHeartRate) {
                    Text("+")
                }
            }
        }

        Text(
            text = "<- Swipe ->",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun ModifyGoalScreen(
    stepsGoal: Int,
    caloriesGoal: Int,
    onDecreaseStepsGoal: () -> Unit,
    onIncreaseStepsGoal: () -> Unit,
    onDecreaseCaloriesGoal: () -> Unit,
    onIncreaseCaloriesGoal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Modify Goals",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Steps", color = Color.White)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onDecreaseStepsGoal) {
                Text("-")
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stepsGoal.toString(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(onClick = onIncreaseStepsGoal) {
                Text("+")
            }
        }

        Text(text = "Calories", color = Color.White)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onDecreaseCaloriesGoal) {
                Text("-")
            }
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = caloriesGoal.toString(),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Button(onClick = onIncreaseCaloriesGoal) {
                Text("+")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "<- Swipe",
            color = Color.Gray,
            style = MaterialTheme.typography.bodySmall
        )
    }
}


@Composable
fun StepCounterScreen(){
    var steps by remember { mutableIntStateOf(0)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){ // end of column arguments body
        Text(
            text = "Daily Goal",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "10,000 steps / 500 cal",
            color = Color.White,
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Calories",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "25 kcal",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Steps Today",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = steps.toString(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick={ steps++ },
            colors = androidx.wear.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            ),
        ) {
            Text("Add Step")
        }
    } // end of column
}

