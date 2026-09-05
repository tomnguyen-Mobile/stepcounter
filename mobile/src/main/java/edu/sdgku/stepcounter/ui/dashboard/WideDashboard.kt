package edu.sdgku.stepcounter.ui.dashboard


import android.R.attr.layoutLabel
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.sdgku.stepcounter.shared.model.FitnessData

@Composable
fun WideDashboard(
    stepsGoal: Int,
    sendStatus: String,
    layoutLabel: String,
    cloudData: FitnessData?,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onSendToWatch: () -> Unit,
    onSaveToFirebase: () -> Unit,
    extraEditorContent: @Composable () -> Unit = {},
    onGoalDropped: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
        ) {
            Text(
                text = "Layout: $layoutLabel",
                style = MaterialTheme.typography.labelLarge
                )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                ) {
                GoalEditorPane(
                    stepsGoal = stepsGoal,
                    sendStatus = sendStatus,
                    showStatusHere = false,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    onSendToWatch = onSendToWatch,
                    onSaveToFirebase = onSaveToFirebase,
                    modifier = Modifier.weight(.55f),
                    extraContent = extraEditorContent,
                    onGoalDropped = onGoalDropped
                    )
                VerticalDivider()
                DashboardInfoPane(
                    sendStatus = sendStatus,
                    cloudData = cloudData,
                    modifier = Modifier.weight(0.45f)
                    )

            }
        }
    }
