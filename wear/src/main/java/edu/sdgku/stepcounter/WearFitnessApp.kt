package edu.sdgku.stepcounter

import android.Manifest
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
import androidx.compose.runtime.mutableDoubleStateOf
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
    stepsGoalFromPhone: Int,
) {
    val navController = rememberNavController() // the navigation will remember the states
    val context = LocalContext.current

    var steps by remember { mutableIntStateOf(0) }
//    var stepsGoal by remember { mutableIntStateOf(100) }
    var calories by remember { mutableDoubleStateOf(0.0) }
    var caloriesGoal by remember { mutableIntStateOf(5) }
    var manualHeartRate by remember { mutableIntStateOf(72) }

    val displayedHeartRate = if (hasHeartRateSensor) { // conditionally assigned
        heartRateSensorValue
    } else {
        manualHeartRate
    }
    var displayedStepsGoal:Int by remember { mutableIntStateOf(stepsGoalFromPhone) }
    var heartRateNotificationSent by remember { mutableStateOf(false) }
    var notificationPermissionGranted by remember {
        mutableStateOf(
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PERMISSION_GRANTED
        )
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        notificationPermissionGranted = isGranted
    }

    LaunchedEffect(stepsGoalFromPhone) {
        displayedStepsGoal = stepsGoalFromPhone
    }

    LaunchedEffect(Unit) {
        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU &&
            !notificationPermissionGranted
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(
        displayedHeartRate,
        notificationPermissionGranted
    ) {
        if (
            displayedHeartRate >= 100 &&
            !heartRateNotificationSent &&
            notificationPermissionGranted
        ) {
            showNotification(
                context = context,
                notificationId = HEART_RATE_NOTIFICATION_ID,
                title = "High Heart Rate Detected",
                message = "Your Heart Rate reached $displayedHeartRate BPM"
            )
            heartRateNotificationSent = true
        }
        if (displayedHeartRate < 100) {
            heartRateNotificationSent = false
        }
    }
    LaunchedEffect(
        steps,
        notificationPermissionGranted
    ) {
        if (
            steps >= displayedStepsGoal &&
            !heartRateNotificationSent &&
            notificationPermissionGranted
        ) {
            showNotification(
                context = context,
                notificationId = STEP_COUNT_NOTIFICATION_ID,
                title = "Step Count Achieved!",
                message = "You reached your $displayedStepsGoal steps!"
            )
            heartRateNotificationSent = true
        }
        if (steps < displayedStepsGoal) {
            heartRateNotificationSent = false
        }
    }

    SwipeNavigationController(
        navController = navController
    ) {
        NavHost(
            navController = navController,
            startDestination = "progress",
        ) {
            composable("progress") {
                DailyProgressScreen(
                    steps = steps,
                    calories = calories,
                    stepsGoal = displayedStepsGoal,
                    caloriesGoal = caloriesGoal,
                    onAddStep = {
                        steps++
                        calories += 0.05
                    }
                )
            }
            composable("heart") {
                HeartRateScreen(
                    heartRate = displayedHeartRate,
                    hasHeartRateSensor = hasHeartRateSensor,
                    onDecreaseHeartRate = { manualHeartRate-- },
                    onIncreaseHeartRate = { manualHeartRate++ }
                )
            }
            composable("goals") {
                ModifyGoalScreen(
                    stepsGoal = displayedStepsGoal,
                    caloriesGoal = caloriesGoal,
                    onDecreaseStepsGoal = { displayedStepsGoal -= 100 },
                    onIncreaseStepsGoal = { displayedStepsGoal += 100 },
                    onDecreaseCaloriesGoal = { caloriesGoal -= 5 },
                    onIncreaseCaloriesGoal = { caloriesGoal += 5 },
                )
            }
        }
    }
}

@Composable
fun SwipeNavigationController(
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val routes = listOf("progress", "heart", "goals") // the order here is the order of the screen
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "progress"
    val currentIndex = routes.indexOf(currentRoute)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .pointerInput(currentRoute) {
                var totalDrag = 0f
                detectHorizontalDragGestures(
                    onDragStart = { totalDrag = 0f },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                    },
                    onDragEnd = {
                        if (totalDrag < -60 && currentIndex < routes.lastIndex) {
                            navController.navigate(routes[currentIndex + 1]) {
                                launchSingleTop = true
                            }
                        }
                        if (totalDrag > 60 && currentIndex > 0) {
                            navController.navigate(routes[currentIndex - 1]) {
                                launchSingleTop = true
                            }
                        }
                    }
                ) // end of detect horizontal drag gestures
            },
        contentAlignment = Alignment.Center
    ) {// end of button arguments
        content()
    }
}

@Composable
fun DailyProgressScreen(
    steps: Int,
    stepsGoal: Int,
    calories: Double,
    caloriesGoal: Int,
    onAddStep: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        Text(
//            text = "Daily Progress",
//            color = Color.White,
//            style = MaterialTheme.typography.titleMedium
//        )
        Spacer(modifier = Modifier.height(12.dp))
//        Text(
//            text = "Steps",
//            color = Color.White,
//        )
//        Text(
//            text = "$steps / $stepsGoal",
//            color = Color.White,
//            style = MaterialTheme.typography.labelMedium
//        )
        Text(text="Step Count: $stepsGoal", color=Color.White)
        Spacer(modifier = Modifier.height(8.dp))
//        Text(
//            text = "${"%.2f".format(calories)} / $caloriesGoal",
//            color = Color.White,
//            style = MaterialTheme.typography.labelMedium
//            )
        Spacer(modifier = Modifier.height(12.dp))
//        Button(onClick = onAddStep) { Text("Add Step") }
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


