package edu.sdgku.stepcounter.ui.dashboard

import android.R.attr.layoutLabel
import android.util.Log.e
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
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Layout: $layoutLabel",
            modifier = Modifier.padding(start = 24.dp, top = 16.dp),
            style = MaterialTheme.typography.labelLarge
        )

        GoalEditorPane(
            stepsGoal = stepsGoal,
            sendStatus = sendStatus,
            showStatusHere = true,
            onDecrease = onDecrease,
            onIncrease = onIncrease,
            onSendToWatch = onSendToWatch,
            onSaveToFirebase = onSaveToFirebase,

        )
    }
}