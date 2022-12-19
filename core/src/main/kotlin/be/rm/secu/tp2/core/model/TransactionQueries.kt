package be.rm.secu.tp2.core.model

import org.jetbrains.exposed.dao.id.UUIDTable

object TransactionQueries : UUIDTable() {
    val date = datetime("date")
}