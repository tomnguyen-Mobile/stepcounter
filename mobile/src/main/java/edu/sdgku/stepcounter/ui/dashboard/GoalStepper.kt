package edu.sdgku.stepcounter.ui.dashboard

import android.R.attr.onClick
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoalStepper(
    stepsGoal: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    largeNumber: Boolean = false,
    onGoalDropped: ((Int) -> Unit)? = null,
    ){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Text(
            text = "Wear Fitness",
            style = MaterialTheme.typography.headlineMedium,
        )
        Spacer( modifier = Modifier.height(16.dp))

        Text(
            text = "Steps Goal",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer( modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            ){
            Button(onClick = onDecrease) { Text("-") }
            Spacer( modifier = Modifier.height(16.dp))

            val numberText = @Composable{
                Text(
                    text = stepsGoal.toString(),
                    style = if (largeNumber){
                        MaterialTheme.typography.displaySmall
                    } else { MaterialTheme.typography.headlineSmall }
                    )
            }
            if (onGoalDropped != null) {
                GoalDropTarget(onGoalDropped = onGoalDropped){
                    numberText()
                }
            } else {
                numberText()
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onIncrease) {Text("+")}
        }
    }
}