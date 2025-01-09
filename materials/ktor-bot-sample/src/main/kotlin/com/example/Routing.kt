package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.coroutines.runBlocking
import kotlinx.html.*
import org.koin.java.KoinJavaComponent.inject

/**
 * Base init routing.
 */
fun Application.configureRouting() {
    routing {
        main()
        route("/api") {
            notification()
        }
    }
}

/**
 * Main page HTML.
 * @see <a href="https://github.com/Kotlin/kotlinx.html">kotlinx.html</a>
 */
private fun Route.main() {
    get("/") {
        call.respondHtml {
            style = "width: 100%; height: 100%; background: black;"
            body {
                style = "width: 100%; height: 100%; margin: 0;"
                table {
                    style = "width: 100%; height: 100%;"
                    tr {
                        td {
                            h1 {
                                style = "color: white; text-align: center;"
                                +"REST API for Telegram Bot!"
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Method REST API for notification.
 */
private fun Route.notification() {
    get("/notification") {
        // service
        val userService: UserService by inject(UserService::class.java)
        val appBot: AppBot by inject(AppBot::class.java)
        // act
        runBlocking {
            userService.getAll().forEach {
                try {
                    appBot.sendMessage(it.idTg, "Notification!")
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    println("Failed to send message to this user: ${it.idTg}")
                }
            }
        }
        // response
        call.response.status(
            HttpStatusCode(
                HttpStatusCode.OK.value,
                "Notification completed successfully!"
            )
        )
    }
}