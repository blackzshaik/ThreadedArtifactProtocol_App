package com.blackzshaik.tap.ai.model

sealed interface Response{
    data class ErrorResponse(val error:String): Response
}