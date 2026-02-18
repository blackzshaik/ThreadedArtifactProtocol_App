package com.blackzshaik.tap.ui.theme.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import com.blackzshaik.tap.R

@Composable
fun HourGlassLoading(){
    var animateHourGlass by remember { mutableStateOf(false) }

    val hourGlassAnimation = animateFloatAsState(
        if (animateHourGlass) 360f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hour glass animation"


    )

    Icon(
        painter = painterResource(R.drawable.round_hourglass_top_24),
        "",
        tint = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier
            .graphicsLayer {

                rotationZ = hourGlassAnimation.value
            }
            .scale(1.5f)
    )
    LaunchedEffect(animateHourGlass) {
        if (!animateHourGlass) {
            animateHourGlass = true
        }
    }
}