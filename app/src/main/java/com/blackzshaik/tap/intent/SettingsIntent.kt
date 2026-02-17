package com.blackzshaik.tap.intent

import com.blackzshaik.tap.utils.CommentsDepth

sealed interface SettingsIntent {
    data class OnUpdateCommentsDepth(val newCommentsDepth: CommentsDepth) : SettingsIntent
    data class SaveSettings(val name:String, val assistantName:String, val commentsDepth: CommentsDepth, val serverUrl:String) : SettingsIntent
    object ResetFeedback: SettingsIntent
    data object GetAllData: SettingsIntent
}