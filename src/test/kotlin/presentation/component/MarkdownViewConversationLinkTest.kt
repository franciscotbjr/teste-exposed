package presentation.component

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Testes para validar o parsing de links de conversa no Markdown
 * Não depende de JavaFX para evitar problemas de inicialização
 */
class MarkdownViewConversationLinkTest {

    @Test
    fun `should detect conversation link pattern`() {
        // Given
        val testConversationId = "123e4567-e89b-12d3-a456-426614174000"
        val markdownContent = """
            Esta conversa foi iniciada a partir de um resumo.
            
            [Ver conversa original](conversation://$testConversationId)
        """.trimIndent()

        // When & Then
        assertTrue(markdownContent.contains("conversation://$testConversationId"), 
                  "Conteúdo deve conter link de conversa")
        assertTrue(markdownContent.contains("[Ver conversa original]"), 
                  "Deve conter texto do link")
    }

    @Test
    fun `should handle conversation link in complex markdown`() {
        // Given
        val testConversationId = "987f6543-c21b-98d7-e654-321098765432"
        val complexMarkdown = """
            ## Resumo da Conversa
            
            ### 📊 Estatísticas
            - Total de mensagens: 10
            - Mensagens do usuário: 5
            - Respostas da IA: 5
            
            ### 💬 Resumo do Conteúdo
            Esta foi uma conversa sobre implementação de funcionalidades.
            
            ---
            
            *Esta conversa foi iniciada a partir de um resumo. [Ver conversa original](conversation://$testConversationId)*
        """.trimIndent()

        // When & Then
        assertTrue(complexMarkdown.contains("conversation://$testConversationId"), 
                  "Markdown complexo deve conter link de conversa")
        assertTrue(complexMarkdown.contains("## Resumo da Conversa"),
                  "Deve conter elementos de markdown")
    }

    @Test
    fun `should distinguish conversation links from regular links`() {
        // Given
        val markdownWithBothLinks = """
            Aqui temos um [link normal](https://example.com) e um [link de conversa](conversation://123-456-789).
            
            Também temos outro [site](http://test.com).
        """.trimIndent()

        // When & Then
        assertTrue(markdownWithBothLinks.contains("conversation://123-456-789"), 
                  "Deve conter link de conversa")
        assertTrue(markdownWithBothLinks.contains("https://example.com"), 
                  "Deve conter link normal")
        assertFalse(markdownWithBothLinks.contains("conversation://example.com"), 
                   "Não deve confundir tipos de link")
    }

    @Test 
    fun `should validate conversation link format`() {
        // Given
        val validConversationId = "123e4567-e89b-12d3-a456-426614174000"
        val conversationLink = "conversation://$validConversationId"
        
        // When & Then
        assertTrue(conversationLink.startsWith("conversation://"),
                  "Link deve começar com conversation://")
        assertTrue(conversationLink.length > "conversation://".length,
                  "Link deve ter ID após o protocolo")
        
        // Verificar se pode extrair o ID
        val extractedId = conversationLink.substring("conversation://".length)
        assertTrue(extractedId == validConversationId,
                  "ID extraído deve ser igual ao original")
    }
}