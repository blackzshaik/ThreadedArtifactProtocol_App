package com.blackzshaik.tap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackzshaik.tap.domain.AssistantNamePreferenceUseCase
import com.blackzshaik.tap.domain.CommentsDepthPreferenceUseCase
import com.blackzshaik.tap.domain.UserNamePreferenceUseCase
import com.blackzshaik.tap.intent.SettingsIntent
import com.blackzshaik.tap.utils.CommentsDepth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userNamePreferenceUseCase: UserNamePreferenceUseCase,
    private val assistantNamePreferenceUseCase: AssistantNamePreferenceUseCase,
    private val commentsDepthPreferenceUseCase: CommentsDepthPreferenceUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun handleIntent(intent: SettingsIntent){
        when(intent){
            SettingsIntent.GetAllData -> {
                viewModelScope.launch (Dispatchers.IO){
                    val userName = userNamePreferenceUseCase()
                    val assistantName = assistantNamePreferenceUseCase()
                    val commentsDepth = commentsDepthPreferenceUseCase()

                    _uiState.update {
                        it.copy(
                            userName = userName,
                            assistantName = assistantName,
                            commentsDepths = commentsDepth
                        )
                    }
                }
            }
            SettingsIntent.ResetFeedback -> {
                _uiState.update {
                    it.copy(
                        showUpdateSuccess = false
                    )
                }
            }
            is SettingsIntent.OnUpdateCommentsDepth -> {
                _uiState.update {
                    it.copy(
                        commentsDepths = intent.newCommentsDepth
                    )
                }
            }

            SettingsIntent.SaveSettings -> {
                viewModelScope.launch (Dispatchers.IO){
                    userNamePreferenceUseCase(_uiState.value.userName)
                    assistantNamePreferenceUseCase(_uiState.value.assistantName)
                    commentsDepthPreferenceUseCase(_uiState.value.commentsDepths)
                    _uiState.update {
                        it.copy(showUpdateSuccess = true)
                    }
                }
            }
        }
    }

}

data class SettingsUiState(
    val userName:String = "",
    val assistantName:String = "",
    val commentsDepths: CommentsDepth = CommentsDepth.MINIMUM,
    val showUpdateSuccess:Boolean = false
)