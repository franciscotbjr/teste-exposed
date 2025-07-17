package org.hexasilith.presentation

import org.hexasilith.model.Conversation
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

/**
 * Wrapper para formatação de conversas na apresentação
 * Mantém os dados originais inalterados e aplica formatação para exibição
 */
data class ConversationDisplay(
    private val originalConversation: Conversation
) {
    val id: UUID get() = originalConversation.id
    val createdAt: LocalDateTime get() = originalConversation.createdAt
    val updatedAt: LocalDateTime get() = originalConversation.updatedAt

    /**
     * Título original da conversa (sem formatação)
     */
    val originalTitle: String get() = originalConversation.title

    /**
     * Título formatado para exibição no console (máximo 60 caracteres por linha)
     */
    val formattedTitle: String by lazy {
        formatTextForDisplay(originalConversation.title, MAX_TITLE_LENGTH)
    }

    /**
     * Data formatada para exibição
     */
    val formattedDate: String by lazy {
        updatedAt.format(dateFormatter)
    }

    companion object {
        private const val MAX_TITLE_LENGTH = 60
        private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        /**
         * Quebra o texto em linhas de no máximo maxLength caracteres
         */
        private fun formatTextForDisplay(text: String, maxLength: Int): String {
            if (text.length <= maxLength) return text

            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = ""

            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"

                if (testLine.length <= maxLength) {
                    currentLine = testLine
                } else {
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine.trim())
                        currentLine = word
                    } else {
                        // Palavra muito longa, quebra forçadamente
                        lines.add(word.substring(0, maxLength))
                        currentLine = word.substring(maxLength)
                    }
                }
            }

            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.trim())
            }

            return lines.joinToString("\n")
        }

        /**
         * Cria um ConversationDisplay a partir de uma Conversation
         */
        fun from(conversation: Conversation): ConversationDisplay {
            return ConversationDisplay(conversation)
        }
    }
}
