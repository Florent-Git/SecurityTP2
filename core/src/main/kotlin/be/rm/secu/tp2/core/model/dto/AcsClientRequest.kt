package be.rm.secu.tp2.core.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class AcsClientRequest(
    val cardCode: String,
    val unixTimestamp: Long = System.currentTimeMillis() / 1000
)
