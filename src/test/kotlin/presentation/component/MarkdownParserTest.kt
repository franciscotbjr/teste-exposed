package org.hexasilith.presentation.component

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested

/**
 * Testes unitários para o MarkdownParser
 * Testa apenas a lógica de parsing separadamente da renderização
 */
class MarkdownParserTest {

    private lateinit var parser: MarkdownParser

    @BeforeEach
    fun setUp() {
        parser = MarkdownParser()
    }

    @Nested
    @DisplayName("Testes de Elementos Inline")
    inner class InlineElementTests {

        @Test
        @DisplayName("Deve parsear texto em negrito com **")
        fun shouldParseBoldTextWithDoubleAsterisks() {
            val markdown = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"
            val elements = parser.parseMarkdown(markdown)

            assertTrue(elements.isNotEmpty())
            val paragraph = elements.first() as MarkdownElement.Paragraph

            // Debug: mostrar conteúdo parseado
            println("Elementos do parágrafo: ${paragraph.content.size}")
            paragraph.content.forEachIndexed { i, elem ->
                println("  $i: $elem")
            }

            // Deve encontrar elementos em negrito
            val boldElements = paragraph.content.filterIsInstance<InlineElement.BoldText>()
            assertTrue(boldElements.isNotEmpty(), "Deveria encontrar elementos em negrito")

            val boldTexts = boldElements.map { it.text }
            assertTrue(boldTexts.contains("Charles M. Schulz"), "Deveria conter 'Charles M. Schulz'")
            assertTrue(boldTexts.contains("26 de novembro de 1922"), "Deveria conter '26 de novembro de 1922'")
        }

        @Test
        @DisplayName("Deve parsear texto em itálico com *")
        fun shouldParseItalicTextWithSingleAsterisks() {
            val markdown = "deixando um legado imenso com *Peanuts*, uma das tiras"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph

            // Deve encontrar o elemento itálico
            val italicElement = paragraph.content.find { it is InlineElement.ItalicText }
            assertNotNull(italicElement)
            assertEquals("Peanuts", (italicElement as InlineElement.ItalicText).text)
        }

        @Test
        @DisplayName("Deve parsear texto em negrito com __")
        fun shouldParseBoldTextWithDoubleUnderscores() {
            val markdown = "Ele faleceu em __12 de fevereiro de 2000__"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph
            val boldElement = paragraph.content.find { it is InlineElement.BoldText }
            assertNotNull(boldElement)
            assertEquals("12 de fevereiro de 2000", (boldElement as InlineElement.BoldText).text)
        }

        @Test
        @DisplayName("Deve parsear código inline com `")
        fun shouldParseInlineCode() {
            val markdown = "Use o comando `println()` para imprimir"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph
            val codeElement = paragraph.content.find { it is InlineElement.InlineCode }
            assertNotNull(codeElement)
            assertEquals("println()", (codeElement as InlineElement.InlineCode).code)
        }

        @Test
        @DisplayName("Deve parsear links com [texto](url)")
        fun shouldParseLinks() {
            val markdown = "Visite o [GitHub](https://github.com) para mais informações"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph
            val linkElement = paragraph.content.find { it is InlineElement.Link }
            assertNotNull(linkElement)
            assertEquals("GitHub", (linkElement as InlineElement.Link).text)
            assertEquals("https://github.com", linkElement.url)
        }

        @Test
        @DisplayName("Deve parsear texto tachado com ~~")
        fun shouldParseStrikethrough() {
            val markdown = "Este texto está ~~riscado~~ para teste"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph
            val strikeElement = paragraph.content.find { it is InlineElement.StrikeThrough }
            assertNotNull(strikeElement)
            assertEquals("riscado", (strikeElement as InlineElement.StrikeThrough).text)
        }

        @Test
        @DisplayName("Deve parsear texto em negrito e itálico com ***")
        fun shouldParseBoldItalicText() {
            val markdown = "Este texto é ***muito importante*** para destacar"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements.first() as MarkdownElement.Paragraph
            val boldItalicElement = paragraph.content.find { it is InlineElement.BoldItalicText }
            assertNotNull(boldItalicElement)
            assertEquals("muito importante", (boldItalicElement as InlineElement.BoldItalicText).text)
        }

        @Test
        @DisplayName("Deve parsear formatação aninhada corretamente")
        fun shouldParseNestedFormatting() {
            val markdown = "Texto com **negrito e *itálico* juntos**"
            val elements = parser.parseMarkdown(markdown)

            val paragraph = elements[0] as MarkdownElement.Paragraph

            // Debug para entender o comportamento atual
            println("=== DEBUG FORMATAÇÃO ANINHADA ===")
            println("Markdown: $markdown")
            println("Elementos parseados: ${paragraph.content.size}")
            paragraph.content.forEachIndexed { i, elem ->
                println("  $i: ${elem::class.simpleName} -> $elem")
            }

            // Verificar se há pelo menos 2 elementos
            assertTrue(paragraph.content.size >= 2, "Deveria ter pelo menos 2 elementos")

            // Verificar se há texto simples no início
            val firstElement = paragraph.content.first()
            assertTrue(firstElement is InlineElement.PlainText, "Primeiro elemento deve ser texto simples")
            assertEquals("Texto com ", (firstElement as InlineElement.PlainText).text)

            // Verificar se há algum elemento formatado
            val hasFormattedElements = paragraph.content.any {
                it is InlineElement.BoldText || it is InlineElement.ItalicText || it is InlineElement.BoldItalicText
            }
            assertTrue(hasFormattedElements, "Deveria encontrar pelo menos um elemento formatado")
        }
    }

    @Nested
    @DisplayName("Testes de Elementos de Bloco")
    inner class BlockElementTests {

        @Test
        @DisplayName("Deve parsear cabeçalhos com #")
        fun shouldParseHeaders() {
            val markdown = """
                # Título Principal
                ## Subtítulo
                ### Título Menor
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(3, elements.size)

            val h1 = elements[0] as MarkdownElement.Header
            assertEquals("Título Principal", h1.text)
            assertEquals(1, h1.level)

            val h2 = elements[1] as MarkdownElement.Header
            assertEquals("Subtítulo", h2.text)
            assertEquals(2, h2.level)

            val h3 = elements[2] as MarkdownElement.Header
            assertEquals("Título Menor", h3.text)
            assertEquals(3, h3.level)
        }

        @Test
        @DisplayName("Deve parsear listas não ordenadas")
        fun shouldParseUnorderedLists() {
            val markdown = """
                - Item 1
                - Item 2
                - Item 3
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            val list = elements[0] as MarkdownElement.UnorderedList
            assertEquals(3, list.items.size)
            assertEquals("Item 1", list.items[0])
            assertEquals("Item 2", list.items[1])
            assertEquals("Item 3", list.items[2])
        }

        @Test
        @DisplayName("Deve parsear listas ordenadas")
        fun shouldParseOrderedLists() {
            val markdown = """
                1. Primeiro item
                2. Segundo item
                3. Terceiro item
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            val list = elements[0] as MarkdownElement.OrderedList
            assertEquals(3, list.items.size)
            assertEquals("Primeiro item", list.items[0])
            assertEquals("Segundo item", list.items[1])
            assertEquals("Terceiro item", list.items[2])
        }

        @Test
        @DisplayName("Deve parsear blocos de código")
        fun shouldParseCodeBlocks() {
            val markdown = """
                ```kotlin
                fun hello() {
                    println("Hello, World!")
                }
                ```
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            val codeBlock = elements[0] as MarkdownElement.CodeBlock
            assertEquals("kotlin", codeBlock.language)
            assertTrue(codeBlock.code.contains("fun hello()"))
            assertTrue(codeBlock.code.contains("println(\"Hello, World!\")"))
        }

        @Test
        @DisplayName("Deve parsear citações")
        fun shouldParseBlockquotes() {
            val markdown = """
                > Esta é uma citação
                > que pode ter múltiplas linhas
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            val blockquote = elements[0] as MarkdownElement.Blockquote
            assertEquals("Esta é uma citação que pode ter múltiplas linhas", blockquote.content)
        }

        @Test
        @DisplayName("Deve parsear linhas horizontais")
        fun shouldParseHorizontalRules() {
            val markdown = """
                Texto antes
                ---
                Texto depois
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            assertEquals(3, elements.size)
            assertTrue(elements[0] is MarkdownElement.Paragraph)
            assertTrue(elements[1] is MarkdownElement.HorizontalRule)
            assertTrue(elements[2] is MarkdownElement.Paragraph)
        }
    }

    @Nested
    @DisplayName("Testes de Casos Especiais")
    inner class SpecialCasesTests {

        @Test
        @DisplayName("Deve lidar com texto vazio")
        fun shouldHandleEmptyText() {
            val markdown = ""
            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            assertTrue(elements[0] is MarkdownElement.EmptyLine)
        }

        @Test
        @DisplayName("Deve lidar com texto sem formatação")
        fun shouldHandlePlainText() {
            val markdown = "Este é um texto simples sem formatação"
            val elements = parser.parseMarkdown(markdown)

            assertEquals(1, elements.size)
            val paragraph = elements[0] as MarkdownElement.Paragraph
            assertEquals(1, paragraph.content.size)
            assertTrue(paragraph.content[0] is InlineElement.PlainText)
            assertEquals("Este é um texto simples sem formatação", (paragraph.content[0] as InlineElement.PlainText).text)
        }

        @Test
        @DisplayName("Deve lidar com múltiplas linhas")
        fun shouldHandleMultipleLines() {
            val markdown = """
                Primeira linha
                
                Segunda linha
                
                Terceira linha
            """.trimIndent()

            val elements = parser.parseMarkdown(markdown)

            // Deve ter pelo menos 3 elementos (parágrafos + linhas vazias)
            assertTrue(elements.size >= 3)

            // Deve conter parágrafos
            val paragraphs = elements.filterIsInstance<MarkdownElement.Paragraph>()
            assertEquals(3, paragraphs.size)
        }
    }

    @Test
    @DisplayName("Teste completo com exemplo do Charles M. Schulz")
    fun shouldParseCompleteSchulzExample() {
        val markdown = """
            **Charles M. Schulz** nasceu no dia **26 de novembro de 1922**, em Minneapolis, Minnesota, nos Estados Unidos.

            Ele faleceu em **12 de fevereiro de 2000**, deixando um legado imenso com *Peanuts*, uma das tiras de quadrinhos mais adoradas de todos os tempos.

            Se quiser saber mais sobre sua vida ou obra, é só perguntar! 😊
        """.trimIndent()

        val elements = parser.parseMarkdown(markdown)

        // Deve ter elementos (parágrafos + linhas vazias)
        assertTrue(elements.isNotEmpty())

        // Pegar todos os parágrafos
        val paragraphs = elements.filterIsInstance<MarkdownElement.Paragraph>()
        assertTrue(paragraphs.isNotEmpty())

        // Verificar se há elementos em negrito
        val allBoldElements = paragraphs.flatMap { it.content.filterIsInstance<InlineElement.BoldText>() }
        val boldTexts = allBoldElements.map { it.text }

        assertTrue(boldTexts.contains("Charles M. Schulz"))
        assertTrue(boldTexts.contains("26 de novembro de 1922"))
        assertTrue(boldTexts.contains("12 de fevereiro de 2000"))

        // Verificar se há elementos em itálico
        val allItalicElements = paragraphs.flatMap { it.content.filterIsInstance<InlineElement.ItalicText>() }
        val italicTexts = allItalicElements.map { it.text }

        assertTrue(italicTexts.contains("Peanuts"))
    }

    @Test
    @DisplayName("Teste específico para debug do negrito")
    fun shouldDebugBoldParsing() {
        val markdown = "Texto **simples** com negrito"
        val elements = parser.parseMarkdown(markdown)

        println("=== DEBUG NEGRITO SIMPLES ===")
        println("Markdown: $markdown")
        println("Elementos parseados: ${elements.size}")

        elements.forEachIndexed { index, element ->
            println("Elemento $index: ${element::class.simpleName}")
            if (element is MarkdownElement.Paragraph) {
                println("  Conteúdo do parágrafo (${element.content.size} elementos):")
                element.content.forEachIndexed { i, content ->
                    println("    $i: ${content::class.simpleName} -> $content")
                }
            }
        }

        // Verificar se há um parágrafo
        assertTrue(elements.isNotEmpty())
        val paragraph = elements[0] as MarkdownElement.Paragraph

        // Verificar se há pelo menos 3 elementos: texto + negrito + texto
        assertTrue(paragraph.content.size >= 3, "Deveria ter pelo menos 3 elementos")

        // Verificar se há elemento em negrito
        val boldElements = paragraph.content.filterIsInstance<InlineElement.BoldText>()
        assertTrue(boldElements.isNotEmpty(), "Deveria encontrar elemento em negrito")
        assertEquals("simples", boldElements[0].text, "Texto em negrito deve ser 'simples'")
    }
}
