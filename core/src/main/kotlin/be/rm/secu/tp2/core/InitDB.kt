package be.rm.secu.tp2.core

import be.rm.secu.tp2.core.model.BasicUser
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import kotlin.random.Random

fun main(args: Array<String>) {
    Db.configureDatabase(args[0])
    transaction {
        BasicUser.new {
            username = "mgx"
            firstname = "Florent"
            lastname = "Raeymaeckers"
            val newSalt = Random.nextBytes(8)
            salt = newSalt.clone()
            password = MessageDigest.getInstance("SHA256").digest("hepl".toByteArray() + newSalt.clone())
        }
        BasicUser.new {
            username = "rafuryc"
            firstname = "Timothy"
            lastname = "Masset"
            val newSalt = Random.nextBytes(8)
            salt = newSalt.clone()
            password = MessageDigest.getInstance("SHA256").digest("helmo".toByteArray() + newSalt.clone())
        }
    }
}