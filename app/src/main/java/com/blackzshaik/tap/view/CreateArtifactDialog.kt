package com.blackzshaik.tap.view

import android.content.res.Configuration
import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.blackzshaik.tap.R
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.ui.theme.components.HourGlassLoading
import com.blackzshaik.tap.ui.theme.components.TAPTextField
import kotlinx.coroutines.delay

@Preview(showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun CreateArtifactDialog(
    showLoading: Boolean = false,
    onClickCreate: (String) -> Unit = {},
    onClickClose: () -> Unit = {}
) {
    var (prompt, setPrompt) = remember { mutableStateOf("") }
    Dialog({}) {
        Box(
            Modifier
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(4.dp)
        ) {
            if (showLoading) {
                Column(
                    Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val red = remember { Animatable(Color.Transparent) }
                    val green = remember { Animatable(Color.Transparent) }
                    var animateHourGlass by remember { mutableStateOf(false) }
                    val gradientAnimation = animateColorAsState(
                        if (animateHourGlass) Color.Green else Color.Transparent,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2500, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "gradient animation"

                    )
                    val offsetAnimation = animateFloatAsState(
                        if (animateHourGlass) 1000.0f else 0.0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "gradient animation"

                    )
                    val textGradientColor = listOf(MaterialTheme.colorScheme.onPrimary,MaterialTheme.colorScheme.secondary,MaterialTheme.colorScheme.secondary,MaterialTheme.colorScheme.onPrimary)
                    val gradientBrush = Brush.linearGradient(
                        colors = textGradientColor,
                        start = Offset(200.0f,offsetAnimation.value),
//                        end = Offset(offsetAnimation.value,0.0f),
//                        tileMode = TileMode.Mirror
                    )
                    Text(
                        "Creating artifact...",
                        style = MaterialTheme.typography.titleLarge.copy(
                            brush = gradientBrush
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    LaunchedEffect(Unit) {
                        red.animateTo(
                            Color.Gray, animationSpec =
                                infiniteRepeatable(
                                    animation = tween(1000, easing = LinearEasing),
                                    repeatMode = RepeatMode.Reverse
                                )
                        )
                    }

                    LaunchedEffect(Unit) {

                        delay(1000)
                        green.animateTo(
                            Color.Gray, animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            )
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    HourGlassLoading()
                }
            } else {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .innerShadow(MaterialTheme.shapes.medium, Shadow(radius = 8.dp,spread =1.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        "Create Artifact",
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                        TAPTextField(Modifier,
                            prompt, {
                                setPrompt(it)
                            },

                            "Enter your prompt here...", minLines = 4
                        )

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton({
                            onClickClose()
                        }) {
                            Icon(
                                Icons.Default.Close,
                                "",
                                tint = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                "Close",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        TextButton(
                            {
                                onClickCreate(prompt)
                            },
                            colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                        ) {
                            Icon(Icons.Default.Done, "")
                            Text("Create")
                        }
                    }

                }
            }
        }


    }
}

@Preview(showSystemUi = false, showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun CreateArtifactDialogPreview() {
    TAPTheme() {
        Box(Modifier.fillMaxSize()) {
            CreateArtifactDialog(showLoading = false) { }
        }
    }
}