package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerResponse <T: Response>(val body:T?, val error:Response.ErrorResponse?)