package com.blackzshaik.tap.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.blackzshaik.tap.R
import com.blackzshaik.tap.intent.HomeIntent
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.utils.toMarkDown
import com.blackzshaik.tap.viewmodel.HomeViewModel
import com.blackzshaik.tap.viewmodel.HomeUiState
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.html.HtmlGenerator
import org.intellij.markdown.parser.MarkdownParser
import java.text.SimpleDateFormat


@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(),
               onClickArtifact: (Artifact) -> Unit,
               showErrorSnackBar: (String) -> Unit) {
    val uiState = viewModel.uiState.collectAsState()
    HomeScreenContent(uiState.value,viewModel::handleIntent)
    LaunchedEffect(uiState.value.openArtifact) {
        uiState.value.openArtifact?.let {
            onClickArtifact(it)
            viewModel.handleIntent(HomeIntent.OnClickArtifact(null))
        }
    }

    LaunchedEffect(uiState.value.showErrorWithMessage) {
        uiState.value.showErrorWithMessage?.let {
            showErrorSnackBar(it)
            viewModel.handleIntent(HomeIntent.ResetErrorMessage)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleIntent(HomeIntent.GetAllArtifacts)
    }
}

@Composable
fun HomeScreenContent(uiState: HomeUiState, handleIntent: (HomeIntent) -> Unit = {}) {
    val sdf = remember { SimpleDateFormat("hh:mm a") }

    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        LazyColumn() {
            items(uiState.artifactList){
                Column(
                    Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .clickable{
                            handleIntent(HomeIntent.OnClickArtifact(it))
                        }
                ) {
                    Column (Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp)){
                        Box (Modifier.fillMaxWidth()
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .padding(8.dp)){
                            Text(
                                it.prompt,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }


                        Spacer(Modifier.height(8.dp))
                        Text(
                            AnnotatedString.fromHtml(it.artifact.toMarkDown()),
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(6.dp))
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(Modifier.fillMaxWidth().alpha(0.75f).padding(horizontal = 16.dp).padding(bottom = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.weight(1f)) {
                            Icon(
                                painter = painterResource(R.drawable.round_comment_24),
                                "",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                it.commentCount.toString(),
                                modifier = Modifier.padding(start = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                        Text(
                            "Last updated ${sdf.format(it.time)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }

        FloatingActionButton({
            handleIntent(HomeIntent.ShowCreateArtifactDialog)
        }, Modifier
            .padding(16.dp)
            .align(Alignment.BottomEnd)) {
            Icon(painterResource(R.drawable.round_create_24), "Add Artifact Icon")
        }
    }

    if (uiState.showCreateArtifactDialog) {
        CreateArtifactDialog(uiState.showArtifactDialogLoading,{
            handleIntent(HomeIntent.CreateArtifact(it))
        }, onClickClose = {handleIntent(HomeIntent.HideCreateArtifactDialog)})
    }
}

@Preview(uiMode = Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun HomeScreenPreview() {
    TAPTheme() {
        Box(Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)) {
            HomeScreenContent(
                HomeUiState(false,
                    artifactList = listOf(
                        Artifact(prompt = "what is the capital of france",
                            artifact = "The capital of France is **Paris**. \uD83D\uDE0A \n\nIt’s a beautiful city with so much to offer! Would you like to know anything more about Paris, or perhaps another country's capital?",),
                        Artifact(prompt = "Prompt", artifact = "Artifact",)
                    )
                )
            )
        }
    }
}