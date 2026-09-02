package edu.sdgku.stepcounter.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoalEditorPane(
    stepsGoal: Int,
    sendStatus: String,
    showStatusHere: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSendToWatch: () -> Unit,
    onSaveToFirebase: () -> Unit,
    modifier: Modifier = Modifier,
    extraContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GoalStepper(
            stepsGoal = stepsGoal,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            largeNumber = !showStatusHere
        )

        extraContent()

        SyncActions(
            sendStatus = sendStatus,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSaveToFirebase,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        )

        if (!showStatusHere) {
            Text(text="")
        }
    } // end of column
} // end of function