package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable

sealed interface Response{
    @Serializable
    data class ErrorResponse(val error:String): Response
}