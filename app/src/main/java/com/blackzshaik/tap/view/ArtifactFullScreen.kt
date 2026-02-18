package com.blackzshaik.tap.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.fromHtml
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.blackzshaik.tap.R
import com.blackzshaik.tap.intent.ArtifactHistoryIntent
import com.blackzshaik.tap.intent.ArtifactIntent
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.utils.toMarkDown
import com.blackzshaik.tap.viewmodel.ArtifactFullScreenViewModel
import com.blackzshaik.tap.viewmodel.ArtifactHistoryUiState

@Composable
fun ArtifactHistoryScreen(viewModel: ArtifactFullScreenViewModel = hiltViewModel(), data: Artifact){
    val uiState = viewModel.uiState.collectAsState()
    ArtifactFullScreenContent(uiState.value, viewModel::handleIntent)
    LaunchedEffect(Unit) {
        viewModel.handleIntent(ArtifactHistoryIntent.GetById(data._id, data.prompt))
    }
}

@Composable
fun ArtifactFullScreenContent(
    uiState: ArtifactHistoryUiState,
    handleIntent: (ArtifactHistoryIntent) -> Unit = {}
) {
    
    val str = buildAnnotatedString {
        if (uiState.artifact.isEmpty() || uiState.updatedArtifactStr.isEmpty())
            append(uiState.artifact)
        else{
            val artifactMarkDown = uiState.artifact
            val updatedArtifactStrMarkDown = uiState.updatedArtifactStr

            val starPost = artifactMarkDown.indexOf(updatedArtifactStrMarkDown).takeIf { it != -1 } ?: 0
            val start = artifactMarkDown.take(starPost)
            val change = if (starPost ==0 ){
                artifactMarkDown.substring(starPost, updatedArtifactStrMarkDown.length)
            }else{
                artifactMarkDown.substring(starPost, starPost+ updatedArtifactStrMarkDown.length)
            }
            val end = if(starPost + updatedArtifactStrMarkDown.length == artifactMarkDown.length)
                ""
            else
                artifactMarkDown.substring(updatedArtifactStrMarkDown.length)
            append(start)
            withStyle(SpanStyle(color = Color.White, background = Color.Green.copy(alpha = 0.5f))) {
                append("{{change}}"+change+"{{/change}}")
            }
            append(end)
        }

    }
    Column(
        Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            Modifier
                .weight(1f)
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
                    AnnotatedString.fromHtml(str.text.toMarkDown().replace("{{change}}","<span style=\"background-color: #444748;\">").replace("{{/change}}","</span>")),
                    Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 8.dp, horizontal = 8.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(), color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
        VersionNavigator(handleIntent, uiState)
    }

}

@Composable
private fun VersionNavigator(
    handleIntent: (ArtifactHistoryIntent) -> Unit,
    uiState: ArtifactHistoryUiState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .clickable {
                    handleIntent(ArtifactHistoryIntent.Previous)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowBack, "",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }

        Text(
            "Version ${uiState.currentVersion}",
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Box(
            Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .clickable {
                    handleIntent(ArtifactHistoryIntent.Next)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Default.ArrowForward, "",
                tint = MaterialTheme.colorScheme.onTertiaryContainer,
            )
        }
    }
}

@Preview
@Composable
fun ArtifactHistoryScreenPreview() {
    TAPTheme() {
        val uiState = ArtifactHistoryUiState(
            prompt = "what is the capital of france",
            artifact = "The capital of France is **Paris**. The national language spoken in France is French. It’s a beautiful city with many things to offer, including regional dialects and minority languages.",
            originalArtifactStr = "capital of France is **Paris**",
            updatedArtifactStr = "including regional dialects and minority languages."
        )
        ArtifactFullScreenContent(uiState)
    }
}