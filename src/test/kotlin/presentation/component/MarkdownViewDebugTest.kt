package org.hexasilith.presentation.component

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

/**
 * Teste simples para depurar o problema do parsing
 */
class MarkdownViewDebugTest {

    @Test
    fun debugBoldParsing() {
        val markdownView = MarkdownView()
        val markdown = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"

        // Fazendo o parsing diretamente (agora o método é público)
        val elements = markdownView.parseMarkdown(markdown)

        println("Número de elementos: ${elements.size}")
        elements.forEachIndexed { index, element ->
            println("Elemento $index: $element")
            if (element is MarkdownElement.Paragraph) {
                println("  Conteúdo do parágrafo:")
                element.content.forEachIndexed { i, content ->
                    println("    $i: $content")
                }
            }
        }

        // Teste básico
        assertTrue(elements.isNotEmpty())
    }

    @Test
    fun debugNestedFormatting() {
        val markdownView = MarkdownView()
        val markdown = "Texto com **negrito e *itálico* juntos**"

        println("Testing nested formatting: $markdown")
        val elements = markdownView.parseMarkdown(markdown)

        println("Número de elementos: ${elements.size}")
        elements.forEachIndexed { index, element ->
            println("Elemento $index: $element")
            if (element is MarkdownElement.Paragraph) {
                println("  Conteúdo do parágrafo (${element.content.size} elementos):")
                element.content.forEachIndexed { i, content ->
                    println("    $i: $content")
                }
            }
        }

        // Teste básico
        assertTrue(elements.isNotEmpty())
    }
}
