package com.mdi2.stepcounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.mdi2.stepcounter.Theme.StepCounterTheme


class MainActivity : ComponentActivity() {
    private var heartRate by  mutableIntStateOf(72)
    private lateinit var heartRateSensorManager: HeartRateSensorManager
    private val heartRatePermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isGranted -> if(isGranted){
                heartRateSensorManager.startListening()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        heartRateSensorManager = HeartRateSensorManager(context = this, onHeartRateChanged = {
            newHeartRate -> heartRate = newHeartRate
            })
        if (heartRateSensorManager.hasHeartRateSensor && !heartRateSensorManager.hasPermission()){
            heartRatePermissionLauncher.launch(heartRateSensorManager.requiredPermission)
            }
        enableEdgeToEdge()
        setContent {
            StepCounterTheme {
                WearFitnessApp(
                    heartRateSensorValue = heartRate,
                    hasHeartRateSensor = heartRateSensorManager.hasHeartRateSensor
                    )
            }
        }
    }

    override fun onResume(){ // stop the sensor from getting constant information to save battery
        super.onResume()
        if (::heartRateSensorManager.isInitialized){ // check if this variable is initialized because of late init
            heartRateSensorManager.startListening()
        }
    }

    override fun onPause(){
        super.onPause()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.stopListener()
        }
    }

}
