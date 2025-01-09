package com.example

import io.ktor.server.application.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import picocli.CommandLine
import java.util.concurrent.Callable

fun main(args: Array<String>) {
    CommandLine(AppCLI(args)).execute(*args)
}

/**
 * Command interface line.
 * @see <a href="https://picocli.info/">Picocli</a>
 */
@CommandLine.Command(
    name = "telegram-bot",
    version = ["0.0.1"],
    mixinStandardHelpOptions = true,
    sortOptions = false,
)
class AppCLI(private val args: Array<String>) : Callable<Int> {
    @CommandLine.Option(
        names = ["--notification"],
        description = ["Execute the mailing."]
    )
    var notification: Boolean = false

    override fun call(): Int {
        try {
            if (notification) {
                val result = Runtime.getRuntime().exec(
                    listOf("curl", "-X", "GET", "http://0.0.0.0:8080/api/notification")
                        .toTypedArray()
                )
                return result.exitValue()
            } else {
                io.ktor.server.netty.EngineMain.main(args)
            }
        } catch (ex: Exception) {
            return 1
        }
        return 0
    }
}

/**
 * Init Ktor server.
 */
fun Application.module() {
    // get from config params
    val isDevelopment = environment.config.property("ktor.development")
        .getString().toBoolean()
    val botToken = environment.config.property("telegram.token")
        .getString()
    // Init Telegram Bot
    val bot = AppBot(botToken).apply {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(this)
    }
    // Start Koin
    startKoin {
        printLogger(if (isDevelopment) Level.DEBUG else Level.NONE)
        modules(module {
            single { bot }
        })
    }
    // Init DB
    configureDatabases()
    // Init Routing
    configureRouting()
}
