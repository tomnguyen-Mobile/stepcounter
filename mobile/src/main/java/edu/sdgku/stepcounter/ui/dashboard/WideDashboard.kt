package edu.sdgku.stepcounter.ui.dashboard


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
fun WideDashboard(
    stepsGoal: Int,
    sendStatus: String,
    layoutLabel: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSendToWatch: () -> Unit,
    onSaveToFirebase: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp, vertical = 32.dp)
        ) {
            Text(
                text = "Layout: $layoutLabel",
                style = MaterialTheme.typography.labelLarge
                )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                ) {
                GoalStepper(
                    stepsGoal = stepsGoal,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    largeNumber = true,
                    modifier = Modifier.weight(1f)
                    )

                SyncActions(
                    sendStatus = sendStatus,
                    onSendToWatch = onSendToWatch,
                    onSaveToFirebase = onSaveToFirebase,
                    modifier = Modifier.weight(1f).fillMaxWidth()
                    )

            }
        }
    }
