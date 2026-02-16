package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolCall(
    val type:String = "function",
    val id:String,
    val function: ToolFunction
)

@Serializable
data class ToolFunction(
    val name:String,
    val arguments:String
)