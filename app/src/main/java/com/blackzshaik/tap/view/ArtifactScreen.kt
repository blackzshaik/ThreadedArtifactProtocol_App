package com.blackzshaik.tap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import androidx.compose.ui.unit.Dp
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
import com.blackzshaik.tap.ui.theme.components.HourGlassLoading
import com.blackzshaik.tap.ui.theme.components.TAPIconButton
import com.blackzshaik.tap.ui.theme.components.TAPTextField
import com.blackzshaik.tap.ui.theme.components.ThoughtsDialog
import com.blackzshaik.tap.utils.toMarkDown
import com.blackzshaik.tap.viewmodel.ArtifactUiState
import com.blackzshaik.tap.viewmodel.ArtifactViewModel
import kotlinx.coroutines.delay
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
                MaterialTheme.colorScheme.tertiaryContainer,
                MaterialTheme.colorScheme.tertiaryContainer,
                Color.Transparent
            ),
            startY = 0f
        )
        Box(
            Modifier
                .fillMaxWidth()
                .background(gradientTop)
                .zIndex(1f)
        ) {

            Text(
                "Comments",
                Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .padding(bottom = 16.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
        LazyColumn(
            Modifier.fillMaxWidth(),
            lazyListState,
            contentPadding = PaddingValues(top = 40.dp, bottom = 72.dp)
        ) {
            @Composable
            fun Reply(replyComment: Comment?, startPadding: Dp = 0.dp) {

                if (replyComment != null) {
                    CommentItem(
                        replyComment,
                        uiState,
                        sdf,
                        handleIntent,
                        startPadding = startPadding.plus(8.dp)
                    )
                    Reply(replyComment.replyComment, startPadding.plus(8.dp))
                }

            }
            uiState.commentList.forEach { comment ->
                item {
                    CommentItem(comment, uiState, sdf, handleIntent)
                }
                item {
                    Spacer(Modifier.height(8.dp))
                }

                item {
                    Reply(comment.replyComment)
                }


            }
//            items(uiState.commentList, key = { item -> item.id }) { item ->
//                CommentItem(item, uiState, sdf, handleIntent)
//            }
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
                TAPTextField(Modifier.weight(1f), comment, onCommentChange, "Add a comment")
                Box(Modifier.size(64.dp), contentAlignment = Alignment.Center) {
                    if (uiState.isGeneratingResponse) {
                        HourGlassLoading()
                    } else {
                        TAPIconButton(Modifier, {
                            if (!uiState.isGeneratingResponse && comment.isNotBlank()) {
                                handleIntent(ArtifactIntent.AddComment(comment))
                                onCommentChange("")
                                focusManager.clearFocus()
                            }
                        }, {
                            Icon(
                                painterResource(R.drawable.round_send_24),
                                "",
                                modifier = Modifier.rotate(-25f),
                                tint = if (!uiState.isGeneratingResponse && comment.isNotBlank()) MaterialTheme.colorScheme.onTertiaryContainer
                                else MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.5f)
                            )
                        })
                    }
                }
            }
        }
    }

    LaunchedEffect(lazyListState.layoutInfo.totalItemsCount) {
        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount)
    }
}


@Composable
private fun CommentItem(
    item: Comment,
    uiState: ArtifactUiState,
    sdf: SimpleDateFormat,
    handleIntent: (ArtifactIntent) -> Unit,
    startPadding: Dp = 0.dp
) {
    Column(
        Modifier
            .padding(horizontal = 8.dp)
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .padding(start = startPadding)
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
                if (item.role == "user") uiState.userName else uiState.assistantName,
                Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                style = MaterialTheme.typography.titleSmall,
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
        val starIdx = item.content.indexOf("<think>")
        val endIdx = item.content.indexOf("</think>")
        val think = if (starIdx > -1 && endIdx > -1)
            item.content.substring(starIdx, endIdx)
        else null
        var showThoughtsDialog by remember { mutableStateOf(false) }

        think?.let { thoughts ->

            Row(
                Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .clickable {
                        showThoughtsDialog = true
                    }.alpha(0.5f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(R.drawable.rounded_network_intelligence_24),
                    "",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    "thoughts:",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    AnnotatedString.fromHtml(thoughts),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (showThoughtsDialog) {
                ThoughtsDialog(thoughts, {
                    showThoughtsDialog = false
                })
            }
        }

        Row(Modifier.padding(horizontal = 8.dp)) {
            Text(
                AnnotatedString.fromHtml(item.content.substring(endIdx.takeIf { it > -1 } ?: 0)
                    .toMarkDown()),
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyMedium
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
    var showFullScreenButton by remember { mutableStateOf(false) }
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
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        showFullScreenButton = !showFullScreenButton
                    }
                    .padding(bottom = 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shadow(4.dp, RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                val starIdx = uiState.artifact.indexOf("<think>")
                val endIdx = uiState.artifact.indexOf("</think>")
                val think = if (starIdx > -1 && endIdx > -1)
                    uiState.artifact.substring(starIdx, endIdx)
                else null
                Text(
                    AnnotatedString.fromHtml(uiState.artifact.substring(endIdx.takeIf { it > -1 }
                        ?: 0).toMarkDown()),
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(), color = MaterialTheme.colorScheme.onSecondaryContainer
                )

                if (think != null) {
                    var showThoughtsDialog by remember { mutableStateOf(false) }
                    Box(
                        Modifier
                            .padding(8.dp)
                            .align(Alignment.BottomStart),
                        contentAlignment = Alignment.Center
                    ) {
                        TAPIconButton(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .alpha(0.75f),
                            {
                                showThoughtsDialog = true
                            },
                            {
                                Icon(
                                    painterResource(R.drawable.rounded_network_intelligence_24),
                                    "Show full screen button",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                                )
                            }
                        )
                    }
                    if (showThoughtsDialog) {
                        ThoughtsDialog(think, {
                            showThoughtsDialog = false
                        })
                    }
                }

                if (showFullScreenButton) {
                    Box(
                        Modifier
                            .padding(8.dp)
                            .align(Alignment.BottomEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        TAPIconButton(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .alpha(0.75f),
                            {
                                handleIntent(ArtifactIntent.OnClickShowFullScreen)
                            },
                            {
                                Icon(
                                    painterResource(R.drawable.round_fullscreen_24),
                                    "Show full screen button",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(showFullScreenButton) {
        if (showFullScreenButton) {
            delay(3000)
            showFullScreenButton = false
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
                            parentId = "artifactId",
                            replyComment = Comment(
                                id = "parent2",
                                role = "assistant",
                                content = "<think>Some thoughts</think>Reply ONE:::: I have updated the original artifact with proper tone. Can you check and let me know if there are any issues?",
                                parentId = "parent1",
                                replyComment = Comment(
                                    id = "parent3",
                                    role = "user",
                                    content = "update this with proper tone",
                                    parentId = "parent2"
                                )
                            )
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