package be.rm.secu.tp2.sp

import be.rm.secu.tp2.core.Db
import io.ktor.server.application.*
import be.rm.secu.tp2.sp.plugins.*
import be.rm.secu.tp2.sp.plugins.configureRouting
import be.rm.secu.tp2.sp.plugins.configureSecurity
import be.rm.secu.tp2.sp.plugins.configureSerialization
import be.rm.secu.tp2.sp.plugins.configureTemplating
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    //configureSecurity()
    Db.initDatabase()
    configureTemplating()
    configureSerialization()
    configureAuthentication()
    configureRouting()
}
