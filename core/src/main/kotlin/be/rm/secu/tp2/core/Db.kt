package be.rm.secu.tp2.core

import be.rm.secu.tp2.core.model.BasicUser
import be.rm.secu.tp2.core.model.BasicUsers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.security.MessageDigest
import kotlin.random.Random

object Db {
    fun configureDatabase(url: String? = javaClass.classLoader.getResource("/data/db.sqlite")?.path) {
        val fullPath = "jdbc:sqlite:${url}"
        Database.connect(fullPath, driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                BasicUsers
            )
        }
    }

    fun initDatabase(){
        configureDatabase()
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
}