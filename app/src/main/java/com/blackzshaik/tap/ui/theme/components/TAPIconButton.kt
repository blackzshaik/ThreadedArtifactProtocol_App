package com.blackzshaik.tap.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.innerShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun TAPIconButton(modifier: Modifier, onClick: () -> Unit,icon: ImageVector) {
    Box(
        modifier
            .padding(start = 8.dp)
            .size(56.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .innerShadow(
                CircleShape, Shadow(
                    8.dp,
                    spread = 1.dp
                )
            ),
        contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center) {
            Icon(
                icon,
                "",
                modifier = Modifier,
                tint = MaterialTheme.colorScheme.onTertiaryContainer

            )
        }
    }
}

@Composable
fun TAPIconButton(modifier: Modifier, onClick: () -> Unit,icon: @Composable () -> Unit) {
    Box(
        modifier
            .padding(start = 8.dp)
            .size(56.dp)
            .clip(CircleShape)
            .clickable {
                onClick()
            }
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .innerShadow(
                CircleShape, Shadow(
                    8.dp,
                    spread = 1.dp
                )
            ),
        contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center) {
            icon()
        }
    }
}