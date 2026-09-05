package edu.sdgku.stepcounter.ui.dashboard

import android.content.ClipDescription
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.mimeTypes
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.unit.dp

@Composable
fun GoalDropTarget(
    onGoalDropped: (Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var isOver by remember { mutableStateOf(false) }
    val target = remember(onGoalDropped) {
        object : DragAndDropTarget {
            override fun onStarted(event: DragAndDropEvent) {
                isOver = false
            }

            override fun onEntered(event: DragAndDropEvent) {
                isOver = true
            }

            override fun onExited(event: DragAndDropEvent) {
                isOver = false
            }

            override fun onEnded(event: DragAndDropEvent) {
                isOver = false
            }

            override fun onDrop(event: DragAndDropEvent): Boolean {
                isOver = false
                val text: String? =
                    event.toAndroidDragEvent().clipData?.getItemAt(0)?.text?.toString()
                val value: Int = text?.toIntOrNull() ?: return false
                onGoalDropped(value)
                return true
            }
        }
    }
    Box(
        modifier = modifier
            .border(
                width = if (isOver) 3.dp else 1.dp, color = if (isOver) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.outline
                }, shape = RoundedCornerShape(12.dp)
            )
            .background(
                color = if (isOver) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                }, shape = RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
            .dragAndDropTarget(
                shouldStartDragAndDrop = { event ->
                    event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
                },
                target = target,
            )
    ) {
        content()
    }
}