package edu.sdgku.stepcounter.ui.dashboard

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.Firebase
import edu.sdgku.stepcounter.sendStepsGoalToWatch
import edu.sdgku.stepcounter.shared.data.FirebaseRepository
import edu.sdgku.stepcounter.shared.model.FitnessData

@Composable
fun PhoneCompanionApp(
    repository: FirebaseRepository,
    widthSizeClass: WindowWidthSizeClass,
    ){
    val context = LocalContext.current
    var stepsGoal by remember { mutableIntStateOf(100000) }
    var sendStatus by remember { mutableStateOf("Not sent") }
    var cloudData by remember { mutableStateOf<FitnessData?>(null) }

    DisposableEffect( repository){
        val listenerRegistration = repository.listenToFitnessData(
            onDataChanged = {
                fitnessData ->
                cloudData = fitnessData
                stepsGoal = fitnessData.dailyGoal.toInt()
                sendStatus = "Goal received from Firebase: $stepsGoal"
            },
            onError = {
                exception ->
                sendStatus = "Firebiase listeneder error: " + (exception.message ?: "Unknown error")
                }
            )
        onDispose { listenerRegistration.remove() }
    }

    val onDecrease = {
        if (stepsGoal > 500)
            stepsGoal -= 500
        }
    val onIncrease =  { stepsGoal += 500}
    val onGoalDropped: (Int) -> Unit = {dropped -> stepsGoal = dropped.coerceAtLeast(500)
    sendStatus = " Dropped preset $dropped (not saved yet)"}
    val onSendToWatch = {
        sendStatus = "Sending..."
        sendStepsGoalToWatch(
            context = context,
            stepsGoal = stepsGoal,
            onSuccess = { sendStatus = "sent $stepsGoal to the watch"},
            onError = { errorMessage -> sendStatus = "error: $errorMessage" })
    }
    val onSaveToFirebase = {
        sendStatus = "saving to firebase..."
        repository.updateDailyGoal(
            dailyGoal = stepsGoal.toLong(),
            onSuccess = { sendStatus = "saved $stepsGoal in firebase"},
            onError = {
                exception ->
                sendStatus = "Firebase error: " + (exception.message ?: "unknown error")            }        )
    }
    val layoutLabel = widthSizeClass.label()
    if (widthSizeClass.isWide()){
        WideDashboard(
            stepsGoal = stepsGoal,
            sendStatus = sendStatus,
            layoutLabel = layoutLabel,
            cloudData = cloudData,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSaveToFirebase,
            extraEditorContent = {GoalPresetRow()},
            onGoalDropped = onGoalDropped )
    } else {
        CompactDashboard(
            stepsGoal = stepsGoal,
            sendStatus = sendStatus,
            layoutLabel = layoutLabel,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSaveToFirebase)
    }
}
