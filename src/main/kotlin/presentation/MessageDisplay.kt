package org.hexasilith.presentation

import org.hexasilith.model.Message
import org.hexasilith.model.Role
import java.time.LocalDateTime
import java.util.UUID

/**
 * Wrapper para formatação de mensagens na apresentação
 * Mantém os dados originais inalterados e aplica formatação para exibição
 */
data class MessageDisplay(
    private val originalMessage: Message
) {
    val id: UUID get() = originalMessage.id
    val conversationId: UUID get() = originalMessage.conversationId
    val role: Role get() = originalMessage.role
    val createdAt: LocalDateTime get() = originalMessage.createdAt

    /**
     * Conteúdo original da mensagem (sem formatação)
     */
    val originalContent: String get() = originalMessage.content

    /**
     * Conteúdo formatado para exibição no console (máximo 60 caracteres por linha)
     */
    val formattedContent: String by lazy {
        formatTextForDisplay(originalMessage.content, MAX_LINE_LENGTH)
    }

    companion object {
        private const val MAX_LINE_LENGTH = 60

        /**
         * Quebra o texto em linhas de no máximo maxLength caracteres
         * Preserva palavras inteiras quando possível
         */
        private fun formatTextForDisplay(text: String, maxLength: Int): String {
            if (text.length <= maxLength) return text

            val words = text.split(" ")
            val lines = mutableListOf<String>()
            var currentLine = ""

            for (word in words) {
                // Se a palavra sozinha é maior que o limite, quebra a palavra
                if (word.length > maxLength) {
                    // Adiciona a linha atual se não estiver vazia
                    if (currentLine.isNotEmpty()) {
                        lines.add(currentLine.trim())
                        currentLine = ""
                    }

                    // Quebra a palavra em pedaços
                    var remainingWord = word
                    while (remainingWord.length > maxLength) {
                        lines.add(remainingWord.substring(0, maxLength))
                        remainingWord = remainingWord.substring(maxLength)
                    }
                    if (remainingWord.isNotEmpty()) {
                        currentLine = remainingWord
                    }
                } else {
                    // Verifica se a palavra cabe na linha atual
                    val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"

                    if (testLine.length <= maxLength) {
                        currentLine = testLine
                    } else {
                        // Adiciona a linha atual e inicia nova linha com a palavra
                        lines.add(currentLine.trim())
                        currentLine = word
                    }
                }
            }

            // Adiciona a última linha se não estiver vazia
            if (currentLine.isNotEmpty()) {
                lines.add(currentLine.trim())
            }

            return lines.joinToString("\n")
        }

        /**
         * Cria um MessageDisplay a partir de uma Message
         */
        fun from(message: Message): MessageDisplay {
            return MessageDisplay(message)
        }
    }
}
