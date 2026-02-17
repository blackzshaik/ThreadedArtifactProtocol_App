package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable


@Serializable
data class ChatCompletion(
    val id: String,
    val `object`: String = "chat.completion", // Use a default value for clarity
    val created: Long,
    val model: String,
    val choices: List<Choice>,
    val usage: Usage,
    val stats: Map<String, String> = emptyMap(), // Default to an empty map if no stats are present
    val system_fingerprint: String
):Response

@Serializable
data class Choice(
    val index: Int,
    val logprobs: String? = null, // Allow null for logprobs
    val finish_reason: String,
    val message: ChatMessage,
)
@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

@Serializable
data class ChatMessage(val role:String,
                       val content:String,
                       val tool_calls: Array<ToolCall>?= null,
                       val tool_call_id:String = ""
)

@Serializable
data class ChatRequest(val model:String,
                       val instruction:String,
                       val messages : Array<ChatMessage>,
                       val tools:Array<ToolUse>,
                       val temperature: Float =  0.6f
)