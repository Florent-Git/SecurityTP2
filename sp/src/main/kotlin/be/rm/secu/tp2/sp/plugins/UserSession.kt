package be.rm.secu.tp2.sp.plugins

import be.rm.secu.tp2.core.model.BasicUser
import be.rm.secu.tp2.core.model.BasicUsers
import com.mitchellbosecke.pebble.loader.ClasspathLoader
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.pebble.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*


@Serializable
data class BasicUserSession(val name: String, val count: Int) : Principal

fun Application.configureAuthentication() {
    install(Sessions) {
        cookie<BasicUserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 60
            transform(SessionTransportTransformerEncrypt("1234567891234567".toByteArray(), "1234567891234567".toByteArray()))
        }
    }
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                val dbUser = transaction { BasicUser.find{BasicUsers.username eq credentials.name}.firstOrNull() }
                if (dbUser == null)
                    null
                else{
                    val calculatedHash = MessageDigest.getInstance("SHA256").digest(
                        credentials.password.toByteArray() + dbUser.salt)
                    if (Arrays.equals(calculatedHash, dbUser.password)){
                        UserIdPrincipal(dbUser.firstname + " " + dbUser.lastname)
                        UserIdPrincipal(dbUser.firstname + " " + dbUser.lastname)

                    }
                    else
                        null
                }
            }
        }
        session<BasicUserSession>("auth-session") {
            validate { session ->
                session
            }
            challenge {
                call.respondRedirect("/login")
            }
        }
    }

    routing {
        authenticate("auth-form") {
            post("/login") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(BasicUserSession(name = userName, count = 1))
                call.respondRedirect("/pay")
            }
        }

        authenticate("auth-session") {
            get("/pay") {
                val basicUserSession = call.principal<BasicUserSession>()
                call.sessions.set(basicUserSession?.copy(count = basicUserSession.count + 1))
                call.respond(PebbleContent("/pay.html", mapOf("user" to basicUserSession!!)))
            }
        }
    }
}

