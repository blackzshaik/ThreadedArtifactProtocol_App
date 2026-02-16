package com.blackzshaik.tap.ai.model

import kotlinx.serialization.Serializable

@Serializable
data class UpdateArtifact(val orgStr: String? = null, val repStr: String)