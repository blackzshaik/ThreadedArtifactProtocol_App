package com.blackzshaik.tap.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TAPTextField(
    modifier: Modifier = Modifier,
    comment: String,
    onCommentChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 4
) {
    Box(
        modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(4.dp)
    ) {
        TextField(
            comment, onCommentChange,
            modifier = Modifier

                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.tertiaryContainer,
                    RoundedCornerShape(16.dp)
                )
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            placeholder = {
                Text(placeholder)
            },
            shape = RoundedCornerShape(16.dp),
            minLines = minLines,
            maxLines = maxLines,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                disabledTextColor = MaterialTheme.colorScheme.onTertiaryContainer.copy(
                    alpha = 0.5f
                ),
                focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                unfocusedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(
                    alpha = 0.5f
                ),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}
