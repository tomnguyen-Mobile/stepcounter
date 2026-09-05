package edu.sdgku.stepcounter.ui.dashboard

import android.content.ClipData
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.unit.dp

@Composable
fun GoalPresetChip(
    value: Int){
    Text(
        text = value.toString(),
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.dragAndDropSource{
            _ -> DragAndDropTransferData(
                clipData = ClipData.newPlainText(                    "steps Goals",                    value.toString()),
                flags = View.DRAG_FLAG_GLOBAL)
        }
        .background(
            color = MaterialTheme.colorScheme.secondaryContainer,
            shape = RoundedCornerShape(8.dp)
        )
        .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}
//    AssistChip(
//        onClick = {},
//        label = {Text(value.toString())},
//        modifier = Modifier.dragAndDropSource {
//            _ ->
//            DragAndDropTransferData(
//                clipData = ClipData.newPlainText(
//                    "steps Goals",
//                    value.toString()),
//                flags = View.DRAG_FLAG_GLOBAL)
//        },
//    )
//}