package be.rm.secu.tp2.core.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class TotpToken(
    val password: String,
    val startEpoch: Long,
    val endEpoch: Long
)
