package com.blackzshaik.tap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackzshaik.tap.domain.CreateArtifactUseCase
import com.blackzshaik.tap.domain.GetAllArtifactUseCase
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.intent.HomeIntent
import com.blackzshaik.tap.utils.coroutineExceptionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val createArtifactUseCase: CreateArtifactUseCase,
    val getAllArtifactUseCase: GetAllArtifactUseCase
): ViewModel() {
    private var _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: HomeIntent){
        when(intent){
            HomeIntent.ShowCreateArtifactDialog -> {
                _uiState.update {
                    it.copy(
                        showCreateArtifactDialog = true
                    )
                }
            }

            HomeIntent.HideCreateArtifactDialog -> {
                _uiState.update {
                    it.copy(
                        showCreateArtifactDialog = false
                    )
                }
            }

            is HomeIntent.CreateArtifact -> {
                createArtifact(intent)
            }

            is HomeIntent.GetAllArtifacts -> {
                viewModelScope.launch(Dispatchers.IO) {
                    getAllArtifactUseCase().collect { artifacts ->
                        _uiState.update {
                            it.copy(
                                artifactList = artifacts
                            )
                        }
                    }
                }
            }

            is HomeIntent.OnClickArtifact -> {
                _uiState.update {
                    it.copy(
                        openArtifact = intent.artifact
                    )
                }
            }
            is HomeIntent.ResetErrorMessage -> {
                _uiState.update {
                    it.copy(
                        showErrorWithMessage = null
                    )
                }
            }
        }
    }

    private fun createArtifact(intent: HomeIntent.CreateArtifact) {
        _uiState.update {
            it.copy(
                showArtifactDialogLoading = true
            )
        }
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler {

            _uiState.update {
                it.copy(
                    showArtifactDialogLoading = false,
                    showCreateArtifactDialog = false,
                    showErrorWithMessage = "Unable to create artifact"
                )
            }
        }) {
            createArtifactUseCase(intent.prompt)
            _uiState.update {
                it.copy(
                    showArtifactDialogLoading = false,
                    showCreateArtifactDialog = false
                )
            }
        }
    }
}


data class HomeUiState(
    val showArtifactDialogLoading: Boolean = false,
    val showCreateArtifactDialog: Boolean = false,
    val artifactList:List<Artifact> = emptyList(),
    val openArtifact: Artifact? = null,
    val showErrorWithMessage:String? = null
)