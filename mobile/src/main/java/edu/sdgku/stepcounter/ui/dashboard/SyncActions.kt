package edu.sdgku.stepcounter.ui.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.pipeline.Expression.Companion.mod

@Composable
fun SyncActions(
    sendStatus: String,
    onSendToWatch: () -> Unit,
    onSaveToFirebase: () -> Unit,
    modifier: Modifier = Modifier
    ){
    Column(
        modifier= modifier,
        horizontalAlignment = Alignment.CenterHorizontally
        ){

        Button(
            onClick = onSendToWatch,
            modifier = Modifier.fillMaxWidth()
            ){
            Text("Send to Watch")
        }

        Spacer (modifier = Modifier.height(12.dp))

        Button(
            onClick = onSaveToFirebase,
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Send to Firebase")
        }

        Spacer (modifier = Modifier.height(16.dp))

        Text(
            text = "Status: $sendStatus",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}