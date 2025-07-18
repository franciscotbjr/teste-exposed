package org.hexasilith.test

import org.hexasilith.presentation.component.*

fun main() {
    println("=== TESTE DO PARSER DE NEGRITO ===")

    val parser = MarkdownParser()
    val testCases = listOf(
        "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**",
        "Texto com **negrito** e *itálico*",
        "Apenas **negrito**",
        "**Início** e meio e **fim**"
    )

    testCases.forEach { test ->
        println("\n--- Testando: '$test' ---")

        // Testar regex diretamente
        val boldRegex = "\\*\\*([^*]+)\\*\\*".toRegex()
        val matches = boldRegex.findAll(test).toList()
        println("Regex encontrou ${matches.size} matches:")
        matches.forEach { match ->
            println("  '${match.value}' -> conteúdo: '${match.groupValues[1]}'")
        }

        // Testar parser
        val elements = parser.parseMarkdown(test)
        println("Parser retornou ${elements.size} elementos:")
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.Paragraph -> {
                    println("  Parágrafo com ${element.content.size} inline elements:")
                    element.content.forEachIndexed { index, inline ->
                        when (inline) {
                            is InlineElement.PlainText -> println("    [$index] Texto: '${inline.text}'")
                            is InlineElement.BoldText -> println("    [$index] NEGRITO: '${inline.text}'")
                            is InlineElement.ItalicText -> println("    [$index] Itálico: '${inline.text}'")
                            else -> println("    [$index] Outro: $inline")
                        }
                    }
                }
                else -> println("  Elemento não-parágrafo: $element")
            }
        }
    }

    println("\n=== FIM DO TESTE ===")
}
