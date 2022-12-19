package be.rm.secu.tp2.core.model.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.*

@Serializable
data class Header(
    val alg: String,
)
