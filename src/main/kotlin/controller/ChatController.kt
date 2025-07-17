package org.hexasilith.controller

import org.hexasilith.service.ConversationService
import org.hexasilith.util.ConsolePrinter
import org.hexasilith.util.InputReader
import java.util.UUID
import kotlin.system.exitProcess

private const val NEW_CONVERSATION = "New Conversation"

class ChatController (
    private val conversationService: ConversationService,
    private val consolePrinter: ConsolePrinter,
    private val inputReader: InputReader
) {

    private var currentConversationId: UUID? = null

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
            val response = conversationService.sendMessage(conversationId, input)
            val conversation = conversationService.getConversation(conversationId)
            if(conversation?.title == NEW_CONVERSATION) {
                conversationService.updateConversationTitle(conversationId, input)
            }
            consolePrinter.printResponse(response)
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