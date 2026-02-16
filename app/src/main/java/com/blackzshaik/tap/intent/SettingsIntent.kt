package com.blackzshaik.tap.intent

import com.blackzshaik.tap.utils.CommentsDepth

sealed interface SettingsIntent {
    data class OnUpdateCommentsDepth(val newCommentsDepth: CommentsDepth) : SettingsIntent
    data object SaveSettings : SettingsIntent
    object ResetFeedback: SettingsIntent
    data object GetAllData: SettingsIntent
}