package org.hexasilith.test

import org.hexasilith.presentation.component.MarkdownParser
import org.hexasilith.presentation.component.MarkdownElement
import org.hexasilith.presentation.component.InlineElement

fun main() {
    val parser = MarkdownParser()
    val testText = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"

    println("Testing: $testText")
    println("=".repeat(50))

    // Testar regex diretamente
    val boldRegex = "\\*\\*([^*]+)\\*\\*".toRegex()
    val matches = boldRegex.findAll(testText)

    println("Regex matches found:")
    matches.forEach { match ->
        println("  Match: '${match.value}' -> '${match.groupValues[1]}'")
    }

    println("\nParser results:")
    val elements = parser.parseMarkdown(testText)

    elements.forEach { element ->
        when (element) {
            is MarkdownElement.Paragraph -> {
                println("Paragraph with ${element.content.size} inline elements:")
                element.content.forEachIndexed { index, inlineElement ->
                    when (inlineElement) {
                        is InlineElement.PlainText -> println("  [$index] PlainText: '${inlineElement.text}'")
                        is InlineElement.BoldText -> println("  [$index] BoldText: '${inlineElement.text}'")
                        is InlineElement.ItalicText -> println("  [$index] ItalicText: '${inlineElement.text}'")
                        else -> println("  [$index] Other: $inlineElement")
                    }
                }
            }
            else -> println("Other element: $element")
        }
    }
}
