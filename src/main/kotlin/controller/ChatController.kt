package org.hexasilith.controller

import org.hexasilith.service.ConversationService
import org.hexasilith.util.ConsolePrinter
import org.hexasilith.util.InputReader
import org.hexasilith.util.LoadingIndicator
import java.util.UUID
import kotlin.system.exitProcess

private const val NEW_CONVERSATION = "New Conversation"

class ChatController (
    private val conversationService: ConversationService,
    private val consolePrinter: ConsolePrinter,
    private val inputReader: InputReader
) {

    private var currentConversationId: UUID? = null
    private val loadingIndicator = LoadingIndicator()

    suspend fun start() {
        consolePrinter.printWelcome()

        while (true) {
            consolePrinter.printPrompt()
            val input = inputReader.readInput().trim()

            when {
                input.equals("/exit", ignoreCase = true) -> exitProcess(0)
                input.equals("/new", ignoreCase = true) -> startNewConversation()
                input.equals("/list", ignoreCase = true) -> listConversations()
                input.startsWith("/load ") -> loadConversation(input.substringAfter("/load "))
                input.startsWith("/delete ") -> deleteConversation(input.substringAfter("/delete "))
                else -> processUserInput(input)
            }
        }
    }

    private suspend fun processUserInput(input: String) {
        if (currentConversationId != null) {
            val conversationId = currentConversationId!!

            // Iniciar indicador de carregamento
            loadingIndicator.start("Enviando mensagem para a IA")

            try {
                val response = conversationService.sendMessage(conversationId, input)
                val conversation = conversationService.getConversation(conversationId)
                if(conversation?.title == NEW_CONVERSATION) {
                    conversationService.updateConversationTitle(conversationId, input)
                }

                // Parar indicador de carregamento
                loadingIndicator.stop()

                consolePrinter.printResponse(response)
            } catch (e: Exception) {
                // Parar indicador de carregamento em caso de erro
                loadingIndicator.stop()
                consolePrinter.printError("Erro ao enviar mensagem: ${e.message}")
            }
        } else {
            consolePrinter.printUsage()
        }
    }

    private fun startNewConversation() {
        currentConversationId = conversationService.createConversation(NEW_CONVERSATION).id
        consolePrinter.printInfo("Started new conversation")
    }

    private fun listConversations() {
        val conversations = conversationService.listConversations()
        if (conversations.isEmpty()) {
            consolePrinter.printInfo("No conversations found")
        } else {
            consolePrinter.printConversationList(conversations)
        }
    }

    private suspend fun loadConversation(id: String) {
        try {
            val uuid = UUID.fromString(id)
            val conversation = conversationService.getConversation(uuid)

            if (conversation != null) {
                currentConversationId = uuid
                val messages = conversationService.getMessages(uuid)
                if(conversation.title == NEW_CONVERSATION
                    && messages.isNotEmpty()) {
                    conversationService.updateConversationTitle(uuid, messages.first().content)
                }
                consolePrinter.printConversationHistory(conversation, messages)

                // Verifica se a última mensagem é do USER (sem resposta da IA)
                if (conversationService.hasPendingUserMessage(uuid)) {
                    val lastUserMessage = messages.lastOrNull()?.content ?: ""
                    consolePrinter.printPendingMessageWarning(lastUserMessage)

                    val userResponse = inputReader.readInput().trim()
                    if (userResponse.equals("s", ignoreCase = true) ||
                        userResponse.equals("sim", ignoreCase = true) ||
                        userResponse.equals("y", ignoreCase = true) ||
                        userResponse.equals("yes", ignoreCase = true)) {

                        // Iniciar indicador de carregamento para reenvio
                        loadingIndicator.start("Reenviando mensagem para a IA")

                        try {
                            val aiResponse = conversationService.resendLastUserMessage(uuid)

                            // Parar indicador de carregamento
                            loadingIndicator.stop()

                            if (aiResponse != null) {
                                consolePrinter.printResponse(aiResponse)
                                consolePrinter.printInfo("Resposta recebida e salva com sucesso!")
                            } else {
                                consolePrinter.printError("Erro: Não foi possível reenviar a mensagem")
                            }
                        } catch (e: Exception) {
                            // Parar indicador de carregamento em caso de erro
                            loadingIndicator.stop()
                            consolePrinter.printError("Erro ao reenviar mensagem: ${e.message}")
                        }
                    } else {
                        consolePrinter.printInfo("Mensagem não reenviada. Você pode continuar a conversa normalmente.")
                    }
                }
            } else {
                consolePrinter.printError("Conversation not found")
            }
        } catch (e: IllegalArgumentException) {
            consolePrinter.printError("Invalid conversation ID")
        }
    }

    private fun deleteConversation(id: String) {
//        try {
//            val uuid = UUID.fromString(id)
//            conversationService.deleteConversation(uuid)
//
//            if (currentConversationId == uuid) {
//                currentConversationId = null
//            }
//
//            consolePrinter.printInfo("Conversation deleted")
//        } catch (e: IllegalArgumentException) {
//            consolePrinter.printError("Invalid conversation ID")
//        }
    }

}