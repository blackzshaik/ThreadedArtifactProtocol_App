package com.blackzshaik.tap.domain

import com.blackzshaik.tap.model.datastore.COMMENTS_DEPTH
import com.blackzshaik.tap.model.datastore.PreferencesRepository
import com.blackzshaik.tap.utils.CommentsDepth
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CommentsDepthPreferenceUseCase @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    suspend operator fun invoke(commentsDepth: CommentsDepth) {
        preferencesRepository.update(COMMENTS_DEPTH, commentsDepth.name)
    }

    suspend operator fun invoke(): CommentsDepth {
        return CommentsDepth.valueOf(preferencesRepository.commentsDepth.first())
    }

}