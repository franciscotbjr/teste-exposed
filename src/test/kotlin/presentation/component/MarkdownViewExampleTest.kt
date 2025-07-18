package org.hexasilith.presentation.component

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName

/**
 * Teste usando o exemplo real do arquivo exemplo.markdown.md
 */
class MarkdownViewExampleTest {

    private lateinit var markdownView: MarkdownView

    @BeforeEach
    fun setUp() {
        markdownView = MarkdownView()
    }

    @Test
    @DisplayName("Teste com exemplo real do arquivo exemplo.markdown.md")
    fun testRealExample() {
        val markdown = """
            **Charles M. Schulz** nasceu no dia **26 de novembro de 1922**, em Minneapolis, Minnesota, nos Estados Unidos.

            Ele faleceu em **12 de fevereiro de 2000**, deixando um legado imenso com *Peanuts*, uma das tiras de quadrinhos mais adoradas de todos os tempos.

            Se quiser saber mais sobre sua vida ou obra, é só perguntar! 😊
        """.trimIndent()

        println("=== TESTE COM EXEMPLO REAL ===")
        println("Markdown original:")
        println(markdown)
        println()

        val elements = markdownView.parseMarkdown(markdown)

        println("Elementos parseados: ${elements.size}")
        elements.forEachIndexed { index, element ->
            println("Elemento $index: ${element::class.simpleName}")
            when (element) {
                is MarkdownElement.Paragraph -> {
                    println("  Conteúdo do parágrafo (${element.content.size} elementos):")
                    element.content.forEachIndexed { i, content ->
                        println("    $i: ${content::class.simpleName} -> $content")
                    }
                }
                is MarkdownElement.EmptyLine -> println("  Linha vazia")
                else -> println("  Conteúdo: $element")
            }
            println()
        }
    }

    @Test
    @DisplayName("Teste específico para formatação aninhada")
    fun testNestedFormattingSimple() {
        val markdown = "Texto com **negrito e *itálico* juntos**"

        println("=== TESTE FORMATAÇÃO ANINHADA ===")
        println("Markdown: $markdown")
        println()

        val elements = markdownView.parseMarkdown(markdown)

        println("Elementos parseados: ${elements.size}")
        elements.forEachIndexed { index, element ->
            println("Elemento $index: ${element::class.simpleName}")
            when (element) {
                is MarkdownElement.Paragraph -> {
                    println("  Conteúdo do parágrafo (${element.content.size} elementos):")
                    element.content.forEachIndexed { i, content ->
                        println("    $i: ${content::class.simpleName} -> $content")
                    }
                }
                else -> println("  Conteúdo: $element")
            }
            println()
        }
    }
}
