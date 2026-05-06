package com.example.habbitapp.view.ui.customElement

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CustomComponent(
    canvasSize: Dp = 300.dp,
    indicatorValue: Int,
    maxIndicatorValue: Int = 100,
    backgroundIndicatorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
    backgroundIndicatorStrokeWidth: Float = 100f,
    foregroundIndicatorColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    foregroundIndicatorStrokeWidth: Float = 100f,
    indicatorStrokeCap: StrokeCap = StrokeCap.Round,
    bigTextFontSize: TextUnit = MaterialTheme.typography.titleLarge.fontSize,
    bigTextColor: Color = MaterialTheme.colorScheme.onSurface,
    bigTextSuffix: String = "%",
    smallText: String = "",
    smallTextFontSize: TextUnit = MaterialTheme.typography.bodyLarge.fontSize,
    smallTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
    modifier: Modifier = Modifier

) {

    var allowedIndicatorValue by remember { mutableStateOf(maxIndicatorValue) }

    allowedIndicatorValue = if (indicatorValue <= maxIndicatorValue) {
        indicatorValue
    } else {
        maxIndicatorValue
    }

    var animatedIndicatorValue by remember { mutableStateOf(0f) }

    LaunchedEffect(key1 = allowedIndicatorValue) {
        animatedIndicatorValue = allowedIndicatorValue.toFloat()
    }

    val percentage = (animatedIndicatorValue / maxIndicatorValue) * 100

    val sweepAndgle by animateFloatAsState(
        targetValue = (2.4 * percentage).toFloat(),
        animationSpec = tween(1000)
    )

    val receivedValue by animateIntAsState(
        targetValue = allowedIndicatorValue,
        animationSpec = tween(1000)
    )

    val animatedBiggerText by animateColorAsState(
        targetValue = if (allowedIndicatorValue == 0) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f) else bigTextColor,
        animationSpec = tween(1000)
    )

    Column(
        modifier = Modifier
            .size(canvasSize)
            .drawBehind {
                val componentsize = size / 1.25f
                backgroundIndicator(
                    componentsize = componentsize,
                    indicatorColor = backgroundIndicatorColor,
                    indicatorStrokeWidth = backgroundIndicatorStrokeWidth,
                    indicatorStrokeCap = indicatorStrokeCap
                )
                foregroundIndicator(
                    sweepAndgle = sweepAndgle,
                    componentsize = componentsize,
                    indicatorColor = foregroundIndicatorColor,
                    indicatorStrokeWidth = foregroundIndicatorStrokeWidth,
                    indicatorStrokeCap = indicatorStrokeCap
                )
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmbeddedElements(
            bigText = receivedValue,
            bigTextFontSize = bigTextFontSize,
            bigTextColor = animatedBiggerText,
            bigTextSuffix = bigTextSuffix,
            smallText = smallText,
            smallTextFontSize = smallTextFontSize,
            smallTextColor = smallTextColor
        )
    }
}

fun DrawScope.backgroundIndicator(
    componentsize: Size,
    indicatorColor: androidx.compose.ui.graphics.Color,
    indicatorStrokeWidth: Float,
    indicatorStrokeCap: StrokeCap

) {
    drawArc(
        size = componentsize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = 240f,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStrokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentsize.width) / 2f,
            y = (size.height - componentsize.height) / 2f
        )
    )
}

fun DrawScope.foregroundIndicator(
    sweepAndgle: Float,
    componentsize: Size,
    indicatorColor: androidx.compose.ui.graphics.Color,
    indicatorStrokeWidth: Float,
    indicatorStrokeCap: StrokeCap

) {
    drawArc(
        size = componentsize,
        color = indicatorColor,
        startAngle = 150f,
        sweepAngle = sweepAndgle,
        useCenter = false,
        style = Stroke(
            width = indicatorStrokeWidth,
            cap = indicatorStrokeCap
        ),
        topLeft = Offset(
            x = (size.width - componentsize.width) / 2f,
            y = (size.height - componentsize.height) / 2f
        )
    )
}

@Composable
fun EmbeddedElements(
    bigText: Int,
    bigTextFontSize: TextUnit,
    bigTextColor: Color,
    bigTextSuffix: String,
    smallText: String,
    smallTextFontSize: TextUnit,
    smallTextColor: Color

) {

    Text(
        text = smallText,
        color = smallTextColor,
        fontSize = smallTextFontSize,
        textAlign = TextAlign.Center
    )

    Text(
        text = "$bigText ${bigTextSuffix.take(2)}",
        color = bigTextColor,
        fontSize = bigTextFontSize,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )

}

@Composable
@Preview(showBackground = true)
fun CustomComponentPreview() {
    CustomComponent(indicatorValue = 10)
}