package edu.sdgku.stepcounter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.google.android.gms.wearable.Wearable
import com.google.firebase.firestore.ListenerRegistration
import edu.sdgku.stepcounter.Theme.StepCounterTheme
import edu.sdgku.stepcounter.shared.data.FirebaseRepository



class MainActivity : ComponentActivity() {
    private var heartRate by mutableIntStateOf(72)
    private var stepsGoal by mutableIntStateOf(10000)
    private lateinit var heartRateSensorManager: HeartRateSensorManager
    private lateinit var wearDataListener: WearDataListener
    private lateinit var repository: FirebaseRepository
    private var firebaseListener: ListenerRegistration? = null
    private val heartRatePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                heartRateSensorManager.startListening()
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val repository = FirebaseRepository()
        repository = FirebaseRepository()
        repository.listenToFitnessData(
            onDataChange = { fitnessData ->
                Log.d(
                    "SharedFirebase",
                    "Goal received: ${fitnessData.dailyGoal}",

                )
            },
            onError = { exception ->
                Log.d(
                    "SharedFirebased",
                    "Cloud not update goal",
                    exception,
                ) // end of log.d
            },
        )
        createNotificationChannel(this)
        heartRateSensorManager = HeartRateSensorManager(context = this, onHeartRateChanged = {
            newHeartRate -> heartRate = newHeartRate
            })
        wearDataListener = WearDataListener(onStepsGoalChange = { newGoal->
            runOnUiThread { stepsGoal=newGoal }
            })
        if (heartRateSensorManager.hasHeartRateSensor && !heartRateSensorManager.hasPermission()){
            heartRatePermissionLauncher.launch(heartRateSensorManager.requiredPermission)
            }
        enableEdgeToEdge()
        setContent {
            StepCounterTheme {
                WearFitnessApp(
                    heartRateSensorValue = heartRate,
                    hasHeartRateSensor = heartRateSensorManager.hasHeartRateSensor,
//                            hasHeartRateSensor = false,
                    stepsGoalFromPhone = stepsGoal,
                )
//                StepCounterScreen()
            }
        }
    }

    override fun onResume(){ // stop the sensor from getting constant information to save battery
        super.onResume()
        // checking if heartRateSensorManager is initialized and start listening
        if (::heartRateSensorManager.isInitialized) { // check if this variable is initialized because of late init
            heartRateSensorManager.startListening()
        }
        // checking if wearDataListener is initialized and start listening
        if (::wearDataListener.isInitialized) {
            Wearable.getDataClient(this).addListener(wearDataListener)
        }
        // checking if repository is initialized and if it is start the listener
        if (::repository.isInitialized){
            startFirebaseListener()
        }
    }


    private fun startFirebaseListener(){
        if (firebaseListener != null){
            return
        }
        firebaseListener = repository.listenToFitnessData(
            onDataChange = {fitnessData ->
                runOnUiThread {
                    stepsGoal = fitnessData.dailyGoal.toInt()
                }
            },

            onError = { exception ->
                Log.e(
                    "Shared firebase wear",
                    "Firebase listener error",
                    exception
                )
            },
        )
    }


    private fun stopFirebaseListener(){
        firebaseListener?.remove()
        firebaseListener = null
    }


    override fun onPause(){
        super.onPause()
        if (::heartRateSensorManager.isInitialized){
            heartRateSensorManager.stopListener()
            }
        if (::wearDataListener.isInitialized){
            Wearable.getDataClient(this).removeListener(wearDataListener)
            }
    }

}

@Composable
fun StepCounterScreen(){
    var steps by remember { mutableIntStateOf(0)}
    var calories by remember { mutableDoubleStateOf(0.0)}

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
            text = "${"%.2f".format(calories)} kcal",
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
            onClick={ steps++;calories+=0.05 },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black
            ),
        ) {
            Text("Add Step")
        }
    } // end of column
}

