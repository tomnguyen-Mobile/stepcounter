package edu.sdgku.stepcounter.ui.dashboard

import android.R.attr.layoutLabel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CompactDashboard(
    stepsGoal: Int,
    sendStatus: String,
    layoutLabel: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSendToWatch: () -> Unit,
    onSaveToFirebase: () -> Unit,
    ){
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        ){
        Text (
            text = "Layout: $layoutLabel",
            style = MaterialTheme.typography.labelLarge
            )

        GoalStepper(
            stepsGoal = stepsGoal,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            largeNumber = false
            )

        SyncActions(
            sendStatus = sendStatus,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSaveToFirebase,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            )



    }
}