package be.rm.secu.tp2.sp

import be.rm.secu.tp2.core.Db
import be.rm.secu.tp2.sp.plugins.configureAuthentication
import be.rm.secu.tp2.sp.plugins.configureSerialization
import be.rm.secu.tp2.sp.plugins.configureTemplating
import be.rm.secu.tp2.sp.routes.home
import be.rm.secu.tp2.sp.routes.loginRoutes
import be.rm.secu.tp2.sp.routes.payRoutes
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    Db.configureDatabase()
    configureTemplating()
    configureSerialization()
    configureAuthentication()

    home()
    loginRoutes()
    payRoutes()
}
