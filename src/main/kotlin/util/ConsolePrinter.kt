package org.hexasilith.util

import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.hexasilith.model.Conversation
import org.hexasilith.model.Message
import org.hexasilith.model.Role

class ConsolePrinter {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun printWelcome() {
        println(ansi()
            .fgBright(Ansi.Color.CYAN)
            .a("""
            ██   ██ ███████ ██   ██  █████  ███████ ██ ██      ██ ████████ ██   ██      ██████ ██   ██  █████  ████████ 
            ██   ██ ██       ██ ██  ██   ██ ██      ██ ██      ██    ██    ██   ██     ██      ██   ██ ██   ██    ██    
            ███████ █████     ███   ███████ ███████ ██ ██      ██    ██    ███████     ██      ███████ ███████    ██    
            ██   ██ ██       ██ ██  ██   ██      ██ ██ ██      ██    ██    ██   ██     ██      ██   ██ ██   ██    ██    
            ██   ██ ███████ ██   ██ ██   ██ ███████ ██ ███████ ██    ██    ██   ██      ██████ ██   ██ ██   ██    ██    
            """.trimMargin())
            .reset()
        )
        println("Type your message or use commands: /new, /list, /load <id>, /delete <id>, /exit")
    }

    fun printUsage() {
        println("Type your message or use commands: /new, /list, /load <id>, /delete <id>, /exit")
    }

    private fun printCommand(color : Ansi.Color, aValue: String) {
        val ansiString = ansi()
            .fgBright(color)
            .a(aValue)
            .reset()
            .toString()

        println(ansiString)
    }

    fun printPrompt() {
        printCommand(Ansi.Color.GREEN, "> ")
    }

    fun printResponse(response: String) {
        printCommand(Ansi.Color.BLUE, response)
    }

    fun printError(message: String) {
        printCommand(Ansi.Color.RED, "[ERROR] $message")
    }

    fun printInfo(message: String) {
        printCommand(Ansi.Color.YELLOW, "[INFO] $message")
    }

    fun printConversationList(conversations: List<Conversation>) {
        printCommand(Ansi.Color.CYAN, "=== Your Conversations ===")
        conversations.forEach { conv ->
            println("${conv.id} - ${conv.title} (${conv.updatedAt.format(dateFormatter)})")
        }
    }

    fun printConversationHistory(conversation: Conversation, messages: List<Message>) {
        printCommand(Ansi.Color.CYAN, "=== ${conversation.title} ===")

        messages.forEach { msg ->
            val prefix = when (msg.role) {
                Role.USER -> ansi().fgBright(Ansi.Color.GREEN).a("You: ").reset()
                Role.ASSISTANT -> ansi().fgBright(Ansi.Color.BLUE).a("AI: ").reset()
                Role.SYSTEM -> ansi().fgBright(Ansi.Color.YELLOW).a("System: ").reset()
            }
            println("$prefix${msg.content}")
        }
    }

    fun printPendingMessageWarning(lastUserMessage: String) {
        printCommand(Ansi.Color.YELLOW, "⚠️  A última mensagem não teve resposta da IA:")
        printCommand(Ansi.Color.WHITE, "\"${lastUserMessage.take(100)}${if (lastUserMessage.length > 100) "..." else ""}\"")
        printCommand(Ansi.Color.CYAN, "Deseja reenviar esta mensagem para obter uma resposta? (s/n): ")
    }

}