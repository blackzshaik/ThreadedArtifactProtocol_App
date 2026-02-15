package com.blackzshaik.tap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackzshaik.tap.domain.GetHistoryForArtifact
import com.blackzshaik.tap.intent.ArtifactHistoryIntent
import com.blackzshaik.tap.model.ArtifactHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtifactFullScreenViewModel  @Inject constructor(private val getHistoryForArtifact: GetHistoryForArtifact):
    ViewModel(){
    private var artifactHistoryList = emptyList<ArtifactHistory>()
    private var _uiState: MutableStateFlow<ArtifactHistoryUiState> = MutableStateFlow(ArtifactHistoryUiState())
    val uiState = _uiState.asStateFlow()
    private var currentPostion = 0

    fun handleIntent(intent: ArtifactHistoryIntent){
        when(intent){
            is ArtifactHistoryIntent.ChangeToVersion -> TODO()
            is ArtifactHistoryIntent.GetById -> {
                currentPostion = 0
                viewModelScope.launch{
                    val list = getHistoryForArtifact(intent.artifactId)
                    artifactHistoryList = list
                    _uiState.value = _uiState.value.copy(
                        prompt = intent.prompt,
                        artifact = list[0].updatedArtifact,
                        originalArtifactStr = list[0].originalArtifactStr,
                        updatedArtifactStr = list[0].replaceArtifactStr,
                        versionList = list.map { it.version },
                        currentVersion = list[0].version
                    )
                }
            }
            ArtifactHistoryIntent.Next -> {
                if (currentPostion + 1 < artifactHistoryList.size){
                    _uiState.value = _uiState.value.copy(
                        artifact = artifactHistoryList[currentPostion+1].updatedArtifact,
                        originalArtifactStr = artifactHistoryList[currentPostion+1].originalArtifactStr,
                        updatedArtifactStr = artifactHistoryList[currentPostion+1].replaceArtifactStr,
                        currentVersion = artifactHistoryList[currentPostion+1].version
                    )
                    currentPostion += 1

                }
            }
            ArtifactHistoryIntent.Previous -> {

                if (currentPostion -1 >= 0){
                    _uiState.value = _uiState.value.copy(
                        artifact = artifactHistoryList[currentPostion-1].updatedArtifact,
                        originalArtifactStr = artifactHistoryList[currentPostion-1].originalArtifactStr,
                        updatedArtifactStr = artifactHistoryList[currentPostion-1].replaceArtifactStr,
                        currentVersion = artifactHistoryList[currentPostion-1].version
                    )
                    currentPostion -= 1
                }
            }
        }
    }
}

data class ArtifactHistoryUiState(
    val prompt:String = "",
    val artifact:String = "",
    val originalArtifactStr:String = "",
    val updatedArtifactStr:String = "",
    val versionList:List<Float> = emptyList(),
    val currentVersion:Float = 1.0f
)