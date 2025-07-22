package org.hexasilith.presentation.component

/**
 * Sealed class para representar diferentes tipos de elementos Markdown
 * Fornece type safety e facilita o pattern matching
 */
sealed class MarkdownElement {
    data class Header(val text: String, val level: Int) : MarkdownElement()
    data class Paragraph(val content: List<InlineElement>) : MarkdownElement()
    data class CodeBlock(val code: String, val language: String? = null) : MarkdownElement()
    data class UnorderedList(val items: List<String>) : MarkdownElement()
    data class OrderedList(val items: List<String>) : MarkdownElement()
    data class Blockquote(val content: String) : MarkdownElement()
    data class HorizontalRule(val placeholder: Unit = Unit) : MarkdownElement()
    data class LineBreak(val placeholder: Unit = Unit) : MarkdownElement()
    object EmptyLine : MarkdownElement()
}

/**
 * Elementos inline dentro de parágrafos
 */
sealed class InlineElement {
    data class PlainText(val text: String) : InlineElement()
    data class BoldText(val text: String) : InlineElement()
    data class ItalicText(val text: String) : InlineElement()
    data class InlineCode(val code: String) : InlineElement()
    data class Link(val text: String, val url: String) : InlineElement()
    data class StrikeThrough(val text: String) : InlineElement()
    data class BoldItalicText(val text: String) : InlineElement()
}

/**
 * Extensões para String que facilitam a detecção de padrões Markdown
 */
fun String.isHeader(): Boolean = startsWith("#")
fun String.getHeaderLevel(): Int = takeWhile { it == '#' }.length
fun String.getHeaderText(): String = dropWhile { it == '#' }.trim()
fun String.isCodeBlock(): Boolean = startsWith("```")
fun String.isUnorderedListItem(): Boolean = startsWith("- ") || startsWith("* ") || startsWith("+ ")
fun String.isOrderedListItem(): Boolean = matches("^\\d+\\.\\s+.*".toRegex())
fun String.isBlockquote(): Boolean = startsWith("> ")
fun String.isHorizontalRule(): Boolean = matches("^(---+|\\*\\*\\*+|___+)\\s*$".toRegex())
fun String.getBlockquoteContent(): String = removePrefix("> ").trim()

/**
 * Parser dedicado para converter Markdown em elementos estruturados
 * Separado da lógica de renderização para maior modularidade
 */
class MarkdownParser {

    /**
     * Converte markdown em uma lista de elementos estruturados
     */
    fun parseMarkdown(markdown: String): List<MarkdownElement> {
        val elements = mutableListOf<MarkdownElement>()
        val lines = markdown.lines()
        var i = 0

        while (i < lines.size) {
            val line = lines[i]

            when {
                line.isHeader() -> {
                    elements.add(MarkdownElement.Header(
                        text = line.getHeaderText(),
                        level = line.getHeaderLevel()
                    ))
                    i++
                }
                line.isCodeBlock() -> {
                    val (codeBlock, nextIndex) = parseCodeBlock(lines, i)
                    elements.add(codeBlock)
                    i = nextIndex
                }
                line.isUnorderedListItem() -> {
                    val (listElement, nextIndex) = parseUnorderedList(lines, i)
                    elements.add(listElement)
                    i = nextIndex
                }
                line.isOrderedListItem() -> {
                    val (listElement, nextIndex) = parseOrderedList(lines, i)
                    elements.add(listElement)
                    i = nextIndex
                }
                line.isBlockquote() -> {
                    val (blockquote, nextIndex) = parseBlockquote(lines, i)
                    elements.add(blockquote)
                    i = nextIndex
                }
                line.isHorizontalRule() -> {
                    elements.add(MarkdownElement.HorizontalRule())
                    i++
                }
                line.trim().isNotEmpty() -> {
                    elements.add(MarkdownElement.Paragraph(
                        content = parseInlineElements(line)
                    ))
                    i++
                }
                else -> {
                    elements.add(MarkdownElement.EmptyLine)
                    i++
                }
            }
        }

        return elements
    }

    /**
     * Analisa blocos de código
     */
    private fun parseCodeBlock(lines: List<String>, startIndex: Int): Pair<MarkdownElement.CodeBlock, Int> {
        val firstLine = lines[startIndex]
        val language = if (firstLine.length > 3) firstLine.substring(3).trim() else null
        val codeLines = mutableListOf<String>()
        var i = startIndex + 1

        while (i < lines.size && !lines[i].startsWith("```")) {
            codeLines.add(lines[i])
            i++
        }

        return MarkdownElement.CodeBlock(
            code = codeLines.joinToString("\n"),
            language = language
        ) to (i + 1)
    }

    /**
     * Analisa listas não ordenadas
     */
    private fun parseUnorderedList(lines: List<String>, startIndex: Int): Pair<MarkdownElement.UnorderedList, Int> {
        val items = mutableListOf<String>()
        var i = startIndex

        while (i < lines.size && lines[i].isUnorderedListItem()) {
            items.add(lines[i].substring(2).trim())
            i++
        }

        return MarkdownElement.UnorderedList(items) to i
    }

    /**
     * Analisa listas ordenadas
     */
    private fun parseOrderedList(lines: List<String>, startIndex: Int): Pair<MarkdownElement.OrderedList, Int> {
        val items = mutableListOf<String>()
        var i = startIndex

        while (i < lines.size && lines[i].isOrderedListItem()) {
            val item = lines[i].replaceFirst("^\\d+\\.\\s+".toRegex(), "")
            items.add(item)
            i++
        }

        return MarkdownElement.OrderedList(items) to i
    }

    /**
     * Analisa blocos de citação
     */
    private fun parseBlockquote(lines: List<String>, startIndex: Int): Pair<MarkdownElement.Blockquote, Int> {
        val items = mutableListOf<String>()
        var i = startIndex

        while (i < lines.size && lines[i].isBlockquote()) {
            items.add(lines[i].getBlockquoteContent())
            i++
        }

        return MarkdownElement.Blockquote(items.joinToString(" ")) to i
    }

    /**
     * Analisa elementos inline dentro de um texto usando CommonMark spec
     * Suporta aninhamento de elementos markdown 
     */
    private fun parseInlineElements(text: String): List<InlineElement> {
        val elements = mutableListOf<InlineElement>()
        var remainingText = text

        while (remainingText.isNotEmpty()) {
            val match = findFirstMatch(remainingText)

            if (match != null) {
                // Adicionar texto antes do match
                if (match.startIndex > 0) {
                    val plainText = remainingText.substring(0, match.startIndex)
                    elements.add(InlineElement.PlainText(plainText))
                }

                // Para elementos que podem conter outros elementos, processar recursivamente apenas para links
                when (match.element) {
                    is InlineElement.ItalicText -> {
                        // Processar o conteúdo do itálico recursivamente para encontrar links
                        val nestedElements = parseInlineElements(match.element.text)
                        val hasLinks = nestedElements.any { it is InlineElement.Link }
                        if (hasLinks) {
                            // Se tem links, expandir mantendo a estrutura
                            elements.addAll(nestedElements)
                        } else {
                            // Se não tem links, manter como itálico
                            elements.add(match.element)
                        }
                    }
                    is InlineElement.BoldText -> {
                        // Processar o conteúdo do negrito recursivamente para encontrar links
                        val nestedElements = parseInlineElements(match.element.text)
                        val hasLinks = nestedElements.any { it is InlineElement.Link }
                        if (hasLinks) {
                            // Se tem links, expandir mantendo a estrutura
                            elements.addAll(nestedElements)
                        } else {
                            // Se não tem links, manter como negrito
                            elements.add(match.element)
                        }
                    }
                    else -> {
                        // Outros elementos não precisam de processamento recursivo
                        elements.add(match.element)
                    }
                }

                // Continuar com o resto do texto
                remainingText = remainingText.substring(match.endIndex)
            } else {
                // Não há mais elementos formatados
                elements.add(InlineElement.PlainText(remainingText))
                break
            }
        }

        return elements.ifEmpty { listOf(InlineElement.PlainText(text)) }
    }

    /**
     * Encontra o primeiro match de elemento inline no texto
     * Processa padrões em ordem de precedência, retornando o mais prioritário entre os que ocorrem mais cedo
     */
    private fun findFirstMatch(text: String): InlineMatch? {
        // Padrões em ordem de precedência (mais específicos primeiro)
        val patterns = listOf(
            // Links [text](url) - PRIMEIRA PRIORIDADE 
            "\\[([^\\]]+)\\]\\(([^\\)]+)\\)".toRegex() to { match: MatchResult ->
                InlineElement.Link(match.groupValues[1], match.groupValues[2])
            },
            // Negrito + Itálico ***text***
            "\\*\\*\\*(.+?)\\*\\*\\*".toRegex() to { match: MatchResult ->
                InlineElement.BoldItalicText(match.groupValues[1])
            },
            // Negrito com ** 
            "\\*\\*(.+?)\\*\\*".toRegex() to { match: MatchResult ->
                InlineElement.BoldText(match.groupValues[1])
            },
            // Negrito com __
            "__(.+?)__".toRegex() to { match: MatchResult ->
                InlineElement.BoldText(match.groupValues[1])
            },
            // Código inline
            "`([^`]+)`".toRegex() to { match: MatchResult ->
                InlineElement.InlineCode(match.groupValues[1])
            },
            // Texto tachado
            "~~(.+?)~~".toRegex() to { match: MatchResult ->
                InlineElement.StrikeThrough(match.groupValues[1])
            },
            // Itálico com * - APÓS links 
            "(?<!\\*)\\*(.+?)\\*(?!\\*)".toRegex() to { match: MatchResult ->
                InlineElement.ItalicText(match.groupValues[1])
            },
            // Itálico com _
            "(?<!_)_(.+?)_(?!_)".toRegex() to { match: MatchResult ->
                InlineElement.ItalicText(match.groupValues[1])
            }
        )

        var bestMatch: InlineMatch? = null
        var bestPriority = Int.MAX_VALUE

        patterns.forEachIndexed { priority, (regex, factory) ->
            val match = regex.find(text)
            if (match != null && match.groupValues.size > 1 && match.groupValues[1].isNotEmpty()) {
                val inlineMatch = InlineMatch(
                    element = factory(match),
                    startIndex = match.range.first,
                    endIndex = match.range.last + 1
                )

                // Priorizar por precedência (índice menor = maior prioridade)
                // Em caso de empate na posição, usar a prioridade do padrão
                if (bestMatch == null || 
                    inlineMatch.startIndex < bestMatch.startIndex ||
                    (inlineMatch.startIndex == bestMatch.startIndex && priority < bestPriority)) {
                    bestMatch = inlineMatch
                    bestPriority = priority
                }
            }
        }

        return bestMatch
    }

    /**
     * Classe auxiliar para representar um match de elemento inline
     */
    private data class InlineMatch(
        val element: InlineElement,
        val startIndex: Int,
        val endIndex: Int
    )
}
