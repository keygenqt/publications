package com.example

import kotlinx.coroutines.runBlocking
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.i
import kotlinx.html.stream.createHTML
import org.koin.java.KoinJavaComponent.inject
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

class AppBot(token: String) : TelegramLongPollingBot(token) {

    private val userService: UserService by inject(UserService::class.java)

    /**
     * Name bot for user.
     */
    override fun getBotUsername(): String {
        return "Telegram Bot"
    }

    /**
     * The main function for receiving messages from the bot.
     */
    override fun onUpdateReceived(update: Update) {
        // Get data
        val message = update.message.text
        val user = update.message.from
        // Start message
        if (message == "/start") {
            runBlocking {
                if (userService.isHash(user.id)) {
                    sendMessage(user.id, "You are already registered.")
                } else {
                    userService.insert(
                        idTg = update.message.from.id,
                        name = update.message.from.firstName,
                    )
                    sendMessage(user.id, "Welcome!")
                }
            }
            return
        }
        if (message == "/about") {
            sendMessage(
                user.id,
                createHTML().div {
                    b { +"Telegram Bot\n" }
                    i { +"v0.0.1" }
                }.substringAfter("<div>").substringBefore("</div>")
            )
            return
        }
        sendMessage(user.id, "I don't understand you :(")
    }

    /**
     * Send message in HTML format.
     * @see <a href="https://core.telegram.org/bots/api#formatting-options">Formatting options</a>.
     */
    fun sendMessage(idTg: Long, text: String) {
        execute(
            SendMessage.builder()
                .chatId(idTg.toString())
                .parseMode("html")
                .text(text)
                .build()
        )
    }
}