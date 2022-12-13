package be.rm.secu.tp2.core.model

import org.jetbrains.exposed.dao.id.UUIDTable

object BasicUsers : UUIDTable(){
    val username = varchar("username", 25)
    val firstname = varchar("firstname", 50)
    val lastname = varchar("lastname", 50)
    val password = binary("password", 64)
    val salt = binary("salt", 8)

}