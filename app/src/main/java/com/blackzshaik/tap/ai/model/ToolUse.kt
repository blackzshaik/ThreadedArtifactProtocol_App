package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class ToolUse(val type:String, val function:Function)

@Serializable
data class Function(
    val name:String,
    val description:String,
    val parameters:Params
)
@Serializable
data class Params(
    val type: String,
    val properties:Map<String,ParamProperties>,
    val required: Array<String>
)
@Serializable
data class ParamProperties(
    val type: String,
    val description:String
)