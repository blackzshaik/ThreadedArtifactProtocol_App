package com.blackzshaik.tap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.blackzshaik.tap.intent.SettingsIntent
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.ui.theme.components.TAPIconButton
import com.blackzshaik.tap.ui.theme.components.TAPTextField
import com.blackzshaik.tap.utils.CommentsDepth
import com.blackzshaik.tap.viewmodel.SettingsUiState
import com.blackzshaik.tap.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel(),showSnackbar:(String) -> Unit) {
    val uiState = viewModel.uiState.collectAsState().value
    SettingsScreenContent(uiState,viewModel::handleIntent)
    LaunchedEffect(uiState.showUpdateSuccess) {
        if (uiState.showUpdateSuccess){
            showSnackbar("Settings Updated")
            viewModel.handleIntent(SettingsIntent.ResetFeedback)
        }
    }
    LaunchedEffect(Unit) {
        viewModel.handleIntent(SettingsIntent.GetAllData)
    }
}

@Composable
fun SettingsScreenContent(uiState: SettingsUiState,handleIntent: (SettingsIntent) -> Unit = {}) {
    val (userName, onUserNameChange) = remember (uiState.userName){ mutableStateOf<String>(uiState.userName) }
    val (assistantName, onAssistantName) = remember (uiState.assistantName){ mutableStateOf<String>(uiState.assistantName) }
    val (serverUrl, onServerUrlChange) = remember (uiState.serverUrl){ mutableStateOf<String>(uiState.serverUrl) }

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column() {
            Column(
                Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    "Server URL",
                    Modifier.padding(start = 16.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                TAPTextField(Modifier.padding(2.dp),
                    serverUrl,
                    onServerUrlChange,
                    "eg: http://127.0.0.1:8080", maxLines = 1)
                Text("Endpoint will be added automatically :/v1/chat/completion",
                    modifier = Modifier.padding(start = 16.dp, bottom = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color =MaterialTheme.colorScheme.onPrimaryContainer )
            }
            Column(
                Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    "Your Name",
                    Modifier.padding(start = 16.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                TAPTextField(Modifier.padding(2.dp),
                    userName,
                    onUserNameChange,
                    "User", maxLines = 1)
            }
            Column(
                Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(
                    "AI Name",
                    Modifier.padding(start = 16.dp, top = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                TAPTextField(Modifier.padding(2.dp),
                    assistantName,
                    onAssistantName,
                    "Assistant",
                    maxLines = 1)
            }
            //TODO; implement this later
//            Column (Modifier
//                .padding(8.dp)
//                .clip(RoundedCornerShape(16.dp))
//                .background(MaterialTheme.colorScheme.primaryContainer)){
//                Text("Additional System Instruction", Modifier.padding(start = 16.dp, top = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer )
//                TAPTextField(Modifier.padding(2.dp),"",{},"Reply like a pirate. Example:", minLines = 4)
//            }

            Column(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(8.dp)
            ) {
                Text(
                    "Comments Depth",
                    Modifier.padding(start = 8.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CommentsDepth(Modifier.fillMaxWidth(0.25f), onClick = {
                        handleIntent(SettingsIntent.OnUpdateCommentsDepth(CommentsDepth.MINIMUM))
                    },CommentsDepth.MINIMUM, uiState.commentsDepths)
                    CommentsDepth(Modifier.weight(1f), onClick = {
                        handleIntent(SettingsIntent.OnUpdateCommentsDepth(CommentsDepth.OPTIMAL))

                    }
                        ,CommentsDepth.OPTIMAL, uiState.commentsDepths)
                }
                CommentsDepth(Modifier.fillMaxWidth(), onClick = {
                    handleIntent(SettingsIntent.OnUpdateCommentsDepth(CommentsDepth.FULL))

                },CommentsDepth.FULL, uiState.commentsDepths)

            }
        }

        Row(Modifier.align(Alignment.BottomCenter).fillMaxWidth(0.75f).padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween){
            TAPIconButton(Modifier, {}, Icons.Default.Close)
            TAPIconButton(Modifier, {
                handleIntent(SettingsIntent.SaveSettings(userName, assistantName, uiState.commentsDepths, serverUrl))
            }, Icons.Default.Done)
        }
    }
}


@Composable
private fun CommentsDepth(
    modifier:Modifier,
    onClick: (CommentsDepth) -> Unit,
    commentsDepth: CommentsDepth,
    selectedCommentsDepth: CommentsDepth,
) {
    val textColor: Color =
        if (commentsDepth == selectedCommentsDepth)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.onTertiaryContainer

    val buttonColor: Color = if (commentsDepth == selectedCommentsDepth)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.tertiaryContainer

    TextButton(
        {onClick(commentsDepth)}, modifier.fillMaxWidth(),
        colors = ButtonDefaults.textButtonColors(containerColor = buttonColor)
    ) {
        if (commentsDepth == selectedCommentsDepth) {
            Box(
                Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    Icons.Default.Done,
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(when(commentsDepth){
            CommentsDepth.MINIMUM -> "Low"
            CommentsDepth.OPTIMAL -> "Optimal"
            CommentsDepth.FULL -> "Full"
        }, color = textColor, maxLines = 1)
    }
}

@Composable
@Preview
fun SettingScreenPreview() {
    TAPTheme() {
        SettingsScreenContent(
            SettingsUiState(
                "Blackz Shaik",
                "Buddy",
                CommentsDepth.MINIMUM
            )
        )
    }
}