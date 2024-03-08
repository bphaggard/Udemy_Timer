package com.example.udemyjettimer

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.udemyjettimer.MainViewModel.Companion.totalTime
import com.example.udemyjettimer.ui.theme.blue200
import com.example.udemyjettimer.ui.theme.blue400
import com.example.udemyjettimer.ui.theme.blue500
import com.example.udemyjettimer.ui.theme.cord

const val TIMER_RADIUS = 300f

@Composable
fun Timer(
    currentTime: Long,
    isRunning: Boolean,
    onStart:() -> Unit,
    onRestart:() -> Unit
) {
    val transition = updateTransition(targetState = currentTime, label = null)
    val tran by transition.animateFloat(
        transitionSpec = { tween(1000, easing = FastOutLinearInEasing) },
        label = ""
    ) {timeleft ->
        if (timeleft < 0) {
            360f
        } else {
            360f-((360f / totalTime) * (totalTime - timeleft))
        }
    }
    
    val progress by animateFloatAsState(targetValue = if (isRunning) tran else 0f, label = "")

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            Button(
                onClick = onStart
            ) {
                Text(
                    text = "START"
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            Button(
                onClick = onRestart
            ) {
                Text(
                    text = "RESTART"
                )
            }
        }
        JetTimerProgressIndicator(
            progress = progress,
            currentTime = currentTime
        )
    }
}

@Composable
fun JetTimerProgressIndicator(
    progress: Float,
    currentTime: Long
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
        ) {
        CircularIndicator(progress = progress)
        AnimatedContent(
            label = "",
            targetState = currentTime,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInVertically { fullHeight -> fullHeight } + fadeIn()).togetherWith(
                        slideOutVertically { fullHeight -> fullHeight } + fadeOut())
                }else {
                    (slideInVertically { fullHeight -> fullHeight } + fadeIn()).togetherWith(
                        slideOutVertically { fullHeight -> fullHeight } + fadeOut())
                }.using(
                    sizeTransform = SizeTransform(clip = false)
                )
            }
        ) {time ->
            Text(
                text = getFormattedTime(time),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun CircularIndicator(progress: Float) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        val stroke = with(LocalDensity.current) {
            Stroke(
                width = 30.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            inset(
                horizontal = (size.width / 2) - TIMER_RADIUS,
                vertical = (size.height / 2) - TIMER_RADIUS
            ) {
                val gradient = Brush.linearGradient(
                    listOf(blue500, blue200, blue400)
                )
                drawBackGround(cord)
                drawProgressIndicator(
                    brush = gradient,
                    progress, stroke
                )
            }
        }
    }
}

fun DrawScope.drawBackGround(color: Color) {
    drawCircle(
        color = color,
        radius = TIMER_RADIUS,
        center = center
    )
}

fun DrawScope.drawProgressIndicator(
    brush: Brush,
    progress: Float,
    stroke : Stroke
) {
    val innerRadius = (size.minDimension - stroke.width) / 2
    val halfSize = size / 2.0f
    val topLeft = Offset(
        x = halfSize.width - innerRadius,
        y = halfSize.height - innerRadius
    )
    val size = Size(innerRadius * 2, innerRadius * 2)
    drawArc(
        brush = brush,
        startAngle = 270f,
        sweepAngle = progress,
        useCenter = false,
        topLeft = topLeft,
        size = size,
        style = stroke,
        blendMode = BlendMode.SrcIn
    )
}