package presentation.component

import org.hexasilith.presentation.component.InlineElement
import org.hexasilith.presentation.component.MarkdownElement
import org.hexasilith.presentation.component.MarkdownParser
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownParserLinkTest {

    private val parser = MarkdownParser()

    @Test
    fun `should parse conversation link correctly`() {
        // Given
        val conversationId = "123e4567-e89b-12d3-a456-426614174000"
        val markdownText = "[Ver conversa original](conversation://$conversationId)"

        // When
        val elements = parser.parseMarkdown(markdownText)

        // Then
        assertEquals(1, elements.size)
        val paragraph = elements[0] as MarkdownElement.Paragraph
        assertEquals(1, paragraph.content.size)
        val link = paragraph.content[0] as InlineElement.Link
        
        assertEquals("Ver conversa original", link.text)
        assertEquals("conversation://$conversationId", link.url)
    }

    @Test
    fun `should parse conversation link in complex text`() {
        // Given
        val conversationId = "987f6543-c21b-98d7-e654-321098765432"
        val markdownText = "*Esta conversa foi iniciada a partir de um resumo. [Ver conversa original](conversation://$conversationId)*"

        // When
        val elements = parser.parseMarkdown(markdownText)

        // Then
        assertEquals(1, elements.size)
        val paragraph = elements[0] as MarkdownElement.Paragraph
        assertTrue(paragraph.content.isNotEmpty(), "Paragraph should have content") 
        
        // Encontrar o link nos elementos
        val linkElement = paragraph.content.find { it is InlineElement.Link } as? InlineElement.Link
        assertTrue(linkElement != null, "Link element should be found")
        assertEquals("Ver conversa original", linkElement.text)
        assertEquals("conversation://$conversationId", linkElement.url)
    }

    @Test
    fun `should parse multiple links including conversation link`() {
        // Given
        val conversationId = "111e2222-a33b-44c5-d666-777788889999"
        val markdownText = "Veja [este site](https://example.com) e [conversa anterior](conversation://$conversationId)"

        // When
        val elements = parser.parseMarkdown(markdownText)

        // Then
        assertEquals(1, elements.size)
        val paragraph = elements[0] as MarkdownElement.Paragraph
        
        val links = paragraph.content.filterIsInstance<InlineElement.Link>()
        assertEquals(2, links.size)
        
        val normalLink = links.find { it.url.startsWith("https://") }
        val conversationLink = links.find { it.url.startsWith("conversation://") }
        
        assertTrue(normalLink != null, "Normal link should be found")
        assertEquals("este site", normalLink.text)
        assertEquals("https://example.com", normalLink.url)
        
        assertTrue(conversationLink != null, "Conversation link should be found")
        assertEquals("conversa anterior", conversationLink.text)
        assertEquals("conversation://$conversationId", conversationLink.url)
    }

    @Test
    fun `should parse conversation link with markdown special characters`() {
        // Given
        val conversationId = "aaa11111-bb22-cc33-dd44-eeeeeeeeeeee"
        val markdownText = "---\n\n*Esta conversa foi iniciada a partir de um resumo. [Ver conversa original](conversation://$conversationId)*"

        // When
        val elements = parser.parseMarkdown(markdownText)

        // Then
        assertTrue(elements.size >= 2) // Horizontal rule + paragraph
        
        val paragraph = elements.find { it is MarkdownElement.Paragraph } as? MarkdownElement.Paragraph
        assertTrue(paragraph != null, "Paragraph should be found")
        
        val linkElement = paragraph.content.find { it is InlineElement.Link } as? InlineElement.Link
        assertTrue(linkElement != null, "Link element should be found")
        assertEquals("Ver conversa original", linkElement.text)
        assertEquals("conversation://$conversationId", linkElement.url)
    }
}