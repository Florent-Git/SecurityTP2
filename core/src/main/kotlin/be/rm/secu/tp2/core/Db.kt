package be.rm.secu.tp2.core

import be.rm.secu.tp2.core.model.BasicUsers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

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
}