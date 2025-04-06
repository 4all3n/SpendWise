package com.fallen.spenwise.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    isAnimated: Boolean = true
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated && animationPlayed) 1f else 0f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "logoAnimation"
    )
    
    val rotationAngle by animateFloatAsState(
        targetValue = if (isAnimated && animationPlayed) 360f else 0f,
        animationSpec = tween(2000, easing = FastOutSlowInEasing),
        label = "rotationAnimation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val radius = size.width.coerceAtMost(size.height) * 0.4f

            // Draw outer circle with gradient
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8D5CF5),
                        Color(0xFFB06AB3),
                        Color(0xFFE96D71)
                    )
                ),
                radius = radius,
                center = Offset(centerX, centerY),
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Draw animated arc
            drawArc(
                color = Color.White,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        intervals = floatArrayOf(15f, 15f),
                        phase = rotationAngle
                    )
                ),
                size = Size(radius * 1.6f, radius * 1.6f),
                topLeft = Offset(
                    centerX - radius * 0.8f,
                    centerY - radius * 0.8f
                )
            )

            // Draw dollar sign
            val dollarPath = Path().apply {
                val symbolSize = radius * 0.5f
                moveTo(centerX - symbolSize * 0.2f, centerY - symbolSize * 0.6f)
                lineTo(centerX + symbolSize * 0.2f, centerY - symbolSize * 0.6f)
                moveTo(centerX, centerY - symbolSize * 0.8f)
                lineTo(centerX, centerY + symbolSize * 0.8f)
                moveTo(centerX - symbolSize * 0.2f, centerY + symbolSize * 0.6f)
                lineTo(centerX + symbolSize * 0.2f, centerY + symbolSize * 0.6f)
            }

            // Draw animated dollar sign
            drawPath(
                path = dollarPath,
                color = Color.White,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                ),
                alpha = animatedProgress
            )

            // Draw animated dots around the circle
            val numberOfDots = 8
            for (i in 0 until numberOfDots) {
                val angle = (i * (360f / numberOfDots) + rotationAngle) * (PI / 180f)
                val dotRadius = 4.dp.toPx()
                val x = centerX + cos(angle).toFloat() * (radius * 1.2f)
                val y = centerY + sin(angle).toFloat() * (radius * 1.2f)
                
                drawCircle(
                    color = Color(0xFF8D5CF5),
                    radius = dotRadius,
                    center = Offset(x, y),
                    alpha = animatedProgress
                )
            }
        }
    }
} 