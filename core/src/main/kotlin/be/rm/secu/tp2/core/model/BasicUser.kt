package be.rm.secu.tp2.core.model

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.util.*

class BasicUser(private val uuid:EntityID<UUID>) : UUIDEntity(uuid) {
    companion object:UUIDEntityClass<BasicUser>(BasicUsers)
    var username by BasicUsers.username
    var firstname by BasicUsers.firstname
    var lastname by BasicUsers.lastname
    var password by BasicUsers.password
    var salt by BasicUsers.salt
}