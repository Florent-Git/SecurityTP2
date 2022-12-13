package be.rm.secu.tp2.sp.plugins

import be.rm.secu.tp2.core.Db
import be.rm.secu.tp2.core.model.BasicUser
import be.rm.secu.tp2.core.model.BasicUsers
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import kotlinx.html.*
import kotlinx.serialization.Serializable
import java.security.MessageDigest
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SizedIterable
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.Transaction
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
                    if (Arrays.equals(calculatedHash, dbUser.password))
                        UserIdPrincipal(credentials.name)
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
        get("/login") {
            call.respondHtml {
                body {
                    form(action = "/login", encType = FormEncType.applicationXWwwFormUrlEncoded, method = FormMethod.post) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Password:"
                            passwordInput(name = "password")
                        }
                        p {
                            submitInput() { value = "Login" }
                        }
                    }
                }
            }
        }

        authenticate("auth-form") {
            post("/login") {
                val userName = call.principal<UserIdPrincipal>()?.name.toString()
                call.sessions.set(BasicUserSession(name = userName, count = 1))
                call.respondRedirect("/hello")
            }
        }

        authenticate("auth-session") {
            get("/hello") {
                val basicUserSession = call.principal<BasicUserSession>()
                call.sessions.set(basicUserSession?.copy(count = basicUserSession.count + 1))
                call.respondText("Hello, ${basicUserSession?.name}! Visit count is ${basicUserSession?.count}.")
            }
        }

        get("/logout") {
            call.sessions.clear<BasicUserSession>()
            call.respondRedirect("/login")
        }
    }
}

