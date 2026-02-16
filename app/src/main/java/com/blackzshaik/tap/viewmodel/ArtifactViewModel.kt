package com.blackzshaik.tap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackzshaik.tap.domain.AddCommentUseCase
import com.blackzshaik.tap.domain.AssistantNamePreferenceUseCase
import com.blackzshaik.tap.domain.GetAllCommentsForArtifact
import com.blackzshaik.tap.domain.GetArtifactByIdUseCase
import com.blackzshaik.tap.domain.UserNamePreferenceUseCase
import com.blackzshaik.tap.intent.ArtifactIntent
import com.blackzshaik.tap.model.Artifact
import com.blackzshaik.tap.model.Comment
import com.blackzshaik.tap.model.datastore.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class ArtifactViewModel @Inject constructor(
    private val getArtifactByIdUseCase: GetArtifactByIdUseCase,
    private val getAllCommentsForArtifact: GetAllCommentsForArtifact,
    private val addCommentUseCase: AddCommentUseCase,
    private val userNamePreferenceUseCase: UserNamePreferenceUseCase,
    private val assistantNamePreferenceUseCase: AssistantNamePreferenceUseCase,
) : ViewModel() {
    private var _uiState: MutableStateFlow<ArtifactUiState> = MutableStateFlow(ArtifactUiState())
    val uiState: StateFlow<ArtifactUiState> = _uiState.asStateFlow()

    private var artifact: Artifact? = null

    fun handleIntent(intent: ArtifactIntent) {
        when (intent) {
            is ArtifactIntent.OnShowArtifact -> {
                viewModelScope.launch(Dispatchers.IO) {
                    getArtifactByIdUseCase(intent.data._id).let { artifactById ->
                        _uiState.value = ArtifactUiState(
                            artifactId = artifactById._id,
                            prompt = artifactById.prompt,
                            artifact = artifactById.artifact,
                            userName = userNamePreferenceUseCase(),
                            assistantName = assistantNamePreferenceUseCase()
                        )
                        artifact = artifactById
                    }


                    getAllCommentsForArtifact(intent.data._id).collect { comments ->
                        _uiState.update {
                            it.copy(
                                commentList = comments
                            )
                        }
                    }
                }
            }

            is ArtifactIntent.AddComment -> {
                _uiState.update {
                    it.copy(isGeneratingResponse = true)
                }
                viewModelScope.launch(Dispatchers.IO) {
                    val updatedArtifact = addCommentUseCase(
                        _uiState.value.artifactId,
                        intent.newComment,
                        _uiState.value.replyAssistantComment,
                        _uiState.value.commentList
                    )

                    _uiState.update {
                        it.copy(
                            artifact = updatedArtifact.artifact,
                            isGeneratingResponse = false,
                            replyAssistantComment = null
                        )
                    }
                }
            }

            is ArtifactIntent.OnClickReplyComment -> {
                _uiState.update {
                    it.copy(
                        replyAssistantComment = intent.assistantComment
                    )
                }
            }

            is ArtifactIntent.OnClickClearReply -> {
                _uiState.update {
                    it.copy(
                        replyUserComment = null,
                        replyAssistantComment = null
                    )
                }
            }

            is ArtifactIntent.OnClickShowFullScreen -> {
                _uiState.update {
                    it.copy(
                        artifactObj = artifact
                    )
                }
            }

            is ArtifactIntent.ShowFullScreen -> {

            }
        }
    }
}


data class ArtifactUiState(
    val artifactId: String = "",
    val prompt: String = "",
    val artifact: String = "",
    val commentList: List<Comment> = emptyList(),
    val isGeneratingResponse: Boolean = false,
    val replyUserComment: Comment? = null,
    val replyAssistantComment: Comment? = null,
    val artifactObj: Artifact? = null,
    val userName:String = "User",
    val assistantName:String = "Assistant"
)