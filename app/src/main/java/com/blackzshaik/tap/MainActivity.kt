package com.blackzshaik.tap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.blackzshaik.tap.ui.theme.TAPTheme
import com.blackzshaik.tap.view.ArtifactHistoryScreen
import com.blackzshaik.tap.view.ArtifactScreen
import com.blackzshaik.tap.view.HomeScreen
import com.blackzshaik.tap.view.navigation.ArtifactHistoryNav
import com.blackzshaik.tap.view.navigation.ArtifactNav
import com.blackzshaik.tap.view.navigation.HomeNav
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TAPTheme {
                TAPScaffold()
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Preview
    @Composable
    fun TopBar(showTitle: Boolean = true,onClickBack: () -> Unit = {}){
        TopAppBar({
            if(showTitle){
                Text("Threaded Artifact Protocol")
            }
        }, navigationIcon = {
            Box(
                Modifier
                    .padding(start = 8.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable{
                        if (!showTitle){
                            onClickBack()
                        }
                    }
                    .background(MaterialTheme.colorScheme.onSecondaryContainer),
                contentAlignment = Alignment.Center) {
                if (showTitle){
                    Icon(
                        painterResource(R.drawable.ic_launcher_foreground),
                        "",
                        modifier =Modifier,
                        tint =  MaterialTheme.colorScheme.secondary
                    )
                }else{
                    Icon(
                        Icons.AutoMirrored.Default.ArrowBack,
                        "",
                        modifier =Modifier,
                        tint =  MaterialTheme.colorScheme.secondary
                    )
                }

            }
        },
            colors = TopAppBarDefaults.topAppBarColors(
                titleContentColor  = MaterialTheme.colorScheme.onPrimaryContainer,
                containerColor = MaterialTheme.colorScheme.primaryContainer))
    }


    @Composable
    private fun TAPScaffold(
        snackBarHostState: SnackbarHostState = remember { SnackbarHostState() },
        coroutineScope: CoroutineScope = rememberCoroutineScope()
    ) {
        val backStack = remember { mutableStateListOf<Any>(HomeNav) }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(showTitle = backStack.last() == HomeNav){
                    backStack.removeLastOrNull()
                }
            },
            snackbarHost = {
                SnackbarHost(snackBarHostState)
            }
        ) { innerPadding ->
            AppNavDisplay(backStack, innerPadding, showErrorSnackBar = {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(it)
                }
            })
        }
    }

    @Composable
    fun AppNavDisplay(
        backStack: SnapshotStateList<Any>,
        innerPadding: PaddingValues,
        showErrorSnackBar: (String) -> Unit = {}
    ) {
        NavDisplay(
            backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            modifier = Modifier.padding(innerPadding),
            onBack = {
                backStack.removeLastOrNull()
            },
            entryProvider = {
                when (it) {
                    is HomeNav -> NavEntry(it) {
                        HomeScreen(onClickArtifact = { data ->
                            backStack.add(ArtifactNav(data))
                        }, showErrorSnackBar = { msg ->
                            showErrorSnackBar(msg)
                        })
                    }

                    is ArtifactNav -> NavEntry(it) { entryData ->
                        ArtifactScreen(data = (entryData as ArtifactNav).data) {
                            backStack.add(ArtifactHistoryNav(entryData.data))
                        }
                    }

                    is ArtifactHistoryNav -> NavEntry(it) { entryData ->
                        (entryData as ArtifactHistoryNav).data?.let { it ->
                            ArtifactHistoryScreen(data = it)
                        } ?: Text("404 Not Found")

                    }

                    else -> NavEntry(it) {
                        Text("404 Not Found")
                    }
                }
            })
    }
}