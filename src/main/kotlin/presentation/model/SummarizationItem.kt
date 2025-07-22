package org.hexasilith.presentation.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SummarizationItem(
    val id: String,
    val originConversationId: String,
    val originConversationTitle: String,
    val summary: String,
    val tokensUsed: Int,
    val summaryMethod: String,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) {
    fun getDisplayTitle(): String {
        // Título baseado na conversa original com data
        val dateStr = createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        return "Resumo de \"$originConversationTitle\" - $dateStr"
    }

    fun getDisplaySummary(): String {
        // Primeira linha ou primeiras 100 characters como preview
        return summary.lines().firstOrNull()?.take(100)?.let {
            if (it.length < summary.length) "$it..." else it
        } ?: ""
    }

    fun getFormattedTokens(): String {
        return "$tokensUsed tokens"
    }

    fun getFormattedDate(): String {
        return createdAt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }

    fun getFormattedTime(): String {
        return createdAt.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    fun getStatusText(): String {
        return if (isActive) "Ativo" else "Inativo"
    }
}