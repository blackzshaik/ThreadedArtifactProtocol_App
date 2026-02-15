package com.blackzshaik.tap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.blackzshaik.tap.R
import com.blackzshaik.tap.ai.Role
import com.blackzshaik.tap.intent.ArtifactIntent
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.utils.toMarkDown
import com.blackzshaik.tap.viewmodel.ArtifactUiState
import com.blackzshaik.tap.viewmodel.ArtifactViewModel
import java.text.SimpleDateFormat
import kotlin.math.roundToInt

@Composable
fun ArtifactScreen(
    viewModel: ArtifactViewModel = hiltViewModel(),
    data: Artifact?,
    onShowFullScreen: (Artifact) -> Unit = {}
) {
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ArtifactIntent.OnShowArtifact(data!!))
    }

    val uiState = viewModel.uiState.collectAsState()
    ArtifactScreenContent(uiState.value, viewModel::handleIntent)
    LaunchedEffect(uiState.value.artifactObj) {
        if (uiState.value.artifactObj != null) {
            onShowFullScreen(uiState.value.artifactObj!!)
        }
    }
}

@Composable
fun ArtifactScreenContent(uiState: ArtifactUiState, handleIntent: (ArtifactIntent) -> Unit = {}) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    var totalHeight by remember { mutableStateOf(0) }
    var topHeight by remember { mutableStateOf(0) }
    var bottomHeight by remember { mutableStateOf(0) }
    var showSectionDivider by remember { mutableStateOf(false) }

    LaunchedEffect(totalHeight) {
        topHeight = totalHeight / 2
        bottomHeight = totalHeight - topHeight
    }

    Column(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                totalHeight = it.size.height
            }) {
        ArtifactSection(topHeight, uiState, handleIntent)

        CommentSection(bottomHeight, uiState, handleIntent)
    }


    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        showSectionDivider = true
                    }, onDragEnd = {
                        showSectionDivider = false
                    }, onDragCancel = {
                        showSectionDivider = false
                    }) { change, dragAmount ->
                        change.consume()
                        offsetY += dragAmount.y
                        topHeight = (totalHeight / 2) + offsetY.roundToInt()
                        bottomHeight = totalHeight + topHeight
                    }
                }
        ) {
            if (showSectionDivider) {
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .align(Alignment.Center)
                        .background(MaterialTheme.colorScheme.surfaceDim)
                )
            }

            Icon(
                Icons.Default.MoreVert, "",
                Modifier
                    .size(width = 48.dp, height = 24.dp)
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.onSecondaryContainer.let {
                        if (showSectionDivider) it else it.copy(
                            alpha = 0.5f
                        )
                    }, CircleShape)
                    .rotate(90f),
                tint = MaterialTheme.colorScheme.secondaryContainer.let {
                    if (showSectionDivider) it else it.copy(
                        alpha = 0.5f
                    )
                }
            )
        }

    }
}

@Composable
private fun CommentSection(
    sectionHeight: Int,
    uiState: ArtifactUiState,
    handleIntent: (ArtifactIntent) -> Unit,
) {
    val (comment, onCommentChange) = remember { mutableStateOf("") }

    val sdf = remember { SimpleDateFormat("hh:mm a") }
    val lazyListState = rememberLazyListState()
    val focusManager = LocalFocusManager.current

    val gradient = Brush.verticalGradient(
        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.secondaryContainer),
        startY = 0f
    )
    Box(
        Modifier
            .height(with(LocalDensity.current) { sectionHeight.toDp() })
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        val gradientTop = Brush.verticalGradient(
            colors = listOf(
                MaterialTheme.colorScheme.secondaryContainer,
                MaterialTheme.colorScheme.secondaryContainer,
                Color.Transparent),
            startY = 0f
        )
        Text("Comments", Modifier.fillMaxWidth().background(gradientTop).padding(8.dp).zIndex(1f), color = MaterialTheme.colorScheme.onSecondaryContainer)
        LazyColumn(
            Modifier.fillMaxWidth(),
            lazyListState,
            contentPadding = PaddingValues(top = 40.dp,bottom = 72.dp)
        ) {
            items(uiState.commentList, key = { item -> item.id }) { item ->
                CommentItem(item, uiState, sdf, handleIntent)
            }
        }

        Spacer(
            Modifier
                .fillMaxWidth()
                .height(16.dp)
                .background(gradientTop)
        )


        Column(
            Modifier
                .padding(top = 8.dp)
                .align(Alignment.BottomCenter)
                .background(gradient)
        ) {
            Spacer(Modifier.height(16.dp))
            if (uiState.isGeneratingResponse) {
                Text(
                    "Generating response... Please wait....",
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }
            if (uiState.replyAssistantComment != null && !uiState.isGeneratingResponse) {
                Row(
                    Modifier
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Column(
                        Modifier
                            .weight(1f)
                            .padding(bottom = 8.dp)
                    ) {
                        Text(
                            "Reply to",
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            uiState.replyAssistantComment.content,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(
                        {
                            handleIntent(ArtifactIntent.OnClickClearReply)
                        },

                        ) {
                        Icon(
                            Icons.Default.Close, contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                        )
                    }
                }
            }

            Row(
                Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TAPTextField(comment, onCommentChange, "Add a comment")
                Box(
                    Modifier
                        .padding(start = 8.dp)
                        .size(48.dp)
                        .clickable {
                            if (!uiState.isGeneratingResponse && comment.isNotBlank()) {
                                handleIntent(ArtifactIntent.AddComment(comment))
                                onCommentChange("")
                                focusManager.clearFocus()
                            }
                        }
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSecondaryContainer),
                    contentAlignment = Alignment.Center) {
                    Icon(
                        painterResource(R.drawable.round_send_24),
                        "",
                        modifier = Modifier.rotate(-25f),
                        tint = if (!uiState.isGeneratingResponse && comment.isNotBlank()) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }

    LaunchedEffect(lazyListState.layoutInfo.totalItemsCount) {
        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
    }
}

@Composable
fun RowScope.TAPTextField(
    comment: String,
    onCommentChange: (String) -> Unit,
    placeholder: String,
    minLines: Int = 1,
    maxLines: Int = 4
) {
    Box(
        Modifier
            .weight(1f)
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

@Composable
private fun CommentItem(
    item: Comment,
    uiState: ArtifactUiState,
    sdf: SimpleDateFormat,
    handleIntent: (ArtifactIntent) -> Unit
) {
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .padding(start = if (item.parentId == uiState.artifactId) 0.dp else 32.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
            .padding(4.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (item.role == "user") "User" else "Assistant",
                Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                "${sdf.format(item.time)}",
                Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer

            )
        }
        item.repliedToComment?.let { repliedToComment ->

            Row(
                Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.round_reply_24),
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    "Reply to:",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    AnnotatedString.fromHtml(repliedToComment),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(Modifier.padding(horizontal = 8.dp)) {
            Text(
                AnnotatedString.fromHtml(item.content.toMarkDown()),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))
        if (item.role == Role.ASSISTANT.value) {
            TextButton(
                {
                    handleIntent(ArtifactIntent.OnClickReplyComment(item))
                },
                colors = ButtonDefaults.textButtonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(painterResource(R.drawable.round_reply_24), "")
                Text("Reply")
            }
        }
    }
}

@Composable
private fun ArtifactSection(
    sectionHeight: Int,
    uiState: ArtifactUiState,
    handleIntent: (ArtifactIntent) -> Unit
) {
    Column(
        Modifier
            .height(with(LocalDensity.current) { sectionHeight.toDp() })
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(
                uiState.prompt,
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 1
            )
            Box(
                Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxHeight()
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Text(
                    AnnotatedString.fromHtml(uiState.artifact.toMarkDown()),
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(), color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Box(
                    Modifier
                        .padding(8.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                        .clickable {
                            handleIntent(ArtifactIntent.OnClickShowFullScreen)
                        }
                        .align(Alignment.BottomEnd),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.round_fullscreen_24),
                        "Show full screen button",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Preview(
    showSystemUi = false, showBackground = false,
)
@Composable
fun ArtifactScreenPreview() {
    TAPTheme() {
        Box(Modifier.background(MaterialTheme.colorScheme.surface)) {
            val replyAssistantComment = Comment(
                role = "assistant",
                content = "I have updated the original artifact with proper tone. Can you check and let me know if there are any issues?"
            )
            ArtifactScreenContent(
                ArtifactUiState(
                    artifactId = "artifactId",
                    prompt = "Prompt",
                    artifact = "Artifact",
                    commentList = listOf(
                        Comment(
                            id = "parent1",
                            role = "user",
                            content = "update this with proper tone first!!!",
                            parentId = "artifactId"
                        ),
                        Comment(
                            id = "parent2",
                            role = "assistant",
                            content = "I have updated the original artifact with proper tone. Can you check and let me know if there are any issues?",
                            parentId = "parent1"
                        ),
                        Comment(
                            id = "parent3",
                            role = "user",
                            content = "update this with proper tone",
                            parentId = "parent2"
                        ),
                        Comment(
                            id = "parent4",
                            role = "assistant",
                            content = "I have updated the original artifact with proper tone. Can you check and let me know if there are any issues?",
                            parentId = "parent3",
                            repliedToComment = "I have updated the original artifact with proper tone. Can you I have updated the original artifact with proper tone. Can you"
                        ),
                    ),
                    isGeneratingResponse = false,
                    replyAssistantComment = null,
                    
                )
            )
        }
    }
}

@Preview
@Composable
fun ArtifacScreenPreviewLight() {
//    ArtifactScreenPreview()
}