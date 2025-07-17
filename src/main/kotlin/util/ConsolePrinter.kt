package org.hexasilith.util

import kotlinx.serialization.json.Json
import java.time.format.DateTimeFormatter
import org.fusesource.jansi.Ansi
import org.fusesource.jansi.Ansi.ansi
import org.hexasilith.model.Conversation
import org.hexasilith.model.Message
import org.hexasilith.model.Role
import org.hexasilith.presentation.ConversationDisplay
import org.hexasilith.presentation.MessageDisplay

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
        val messageDisplay = MessageDisplay.from(
            org.hexasilith.model.Message(
                conversationId = java.util.UUID.randomUUID(),
                role = Role.ASSISTANT,
                content = response
            )
        )

        val formattedLines = messageDisplay.formattedContent.split("\n")
        formattedLines.forEach { line ->
            printCommand(Ansi.Color.BLUE, line)
        }
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
            val conversationDisplay = ConversationDisplay.from(conv)
            println("${conversationDisplay.id} - ${conversationDisplay.formattedTitle} (${conversationDisplay.formattedDate})")
        }
    }

    fun printConversationHistory(conversation: Conversation, messages: List<Message>) {
        val conversationDisplay = ConversationDisplay.from(conversation)
        printCommand(Ansi.Color.CYAN, "=== ${conversationDisplay.formattedTitle} ===")

        messages.forEach { msg ->
            val messageDisplay = MessageDisplay.from(msg)
            val (prefix, contentColor) = when (messageDisplay.role) {
                Role.USER -> ansi().fgBright(Ansi.Color.GREEN).a("You: ").reset() to Ansi.Color.GREEN
                Role.ASSISTANT -> ansi().fgBright(Ansi.Color.BLUE).a("AI: ").reset() to Ansi.Color.BLUE
                Role.SYSTEM -> ansi().fgBright(Ansi.Color.YELLOW).a("System: ").reset() to Ansi.Color.YELLOW
            }

            // Exibe cada linha da mensagem formatada
            val formattedLines = messageDisplay.formattedContent.split("\n")
            formattedLines.forEachIndexed { index, line ->
                if (index == 0) {
                    // Primeira linha: prefixo colorido + conteúdo colorido
                    print(prefix)
                    println(ansi().fg(contentColor).a(line).reset())
                } else {
                    // Para linhas subsequentes, adiciona espaçamento para alinhar com o prefixo
                    val spacing = " ".repeat(when (messageDisplay.role) {
                        Role.USER -> 5  // "You: ".length
                        Role.ASSISTANT -> 4  // "AI: ".length
                        Role.SYSTEM -> 8  // "System: ".length
                    })
                    print(spacing)
                    println(ansi().fg(contentColor).a(line).reset())
                }
            }
        }
    }

    fun printPendingMessageWarning(lastUserMessage: String) {
        val messageDisplay = MessageDisplay.from(
            org.hexasilith.model.Message(
                conversationId = java.util.UUID.randomUUID(),
                role = Role.USER,
                content = lastUserMessage
            )
        )

        printCommand(Ansi.Color.YELLOW, "⚠️  A última mensagem não teve resposta da IA:")
        printCommand(Ansi.Color.WHITE, "\"${messageDisplay.formattedContent}\"")
        printCommand(Ansi.Color.CYAN, "Deseja reenviar esta mensagem para obter uma resposta? (s/n): ")
    }

    fun showApiLoadingIndicator() {
        print(ansi()
            .fgBright(Ansi.Color.YELLOW)
            .a("🤖 Processando sua mensagem")
            .reset()
        )
    }

    fun hideApiLoadingIndicator() {
        // Move cursor to beginning of line and clear it
        print(ansi()
            .cursorToColumn(0)
            .eraseLine()
            .reset()
        )
    }

}