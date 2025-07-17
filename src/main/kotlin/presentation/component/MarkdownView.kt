package org.hexasilith.presentation.component

import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.*
import javafx.scene.paint.Color
import java.awt.Desktop
import java.net.URI

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
}

/**
 * Extensões para String que facilitam a detecção de padrões Markdown
 */
fun String.isHeader(): Boolean = startsWith("#")
fun String.getHeaderLevel(): Int = takeWhile { it == '#' }.length
fun String.getHeaderText(): String = dropWhile { it == '#' }.trim()
fun String.isCodeBlock(): Boolean = startsWith("```")
fun String.isUnorderedListItem(): Boolean = startsWith("- ") || startsWith("* ")
fun String.isOrderedListItem(): Boolean = matches("^\\d+\\.\\s+.*".toRegex())

/**
 * Componente JavaFX personalizado para renderizar Markdown
 * Substitui o WebView para renderização nativa
 */
class MarkdownView : VBox() {

    private var isUserMessage: Boolean = false

    init {
        spacing = 8.0
        styleClass.add("markdown-view")
    }

    /**
     * Define o conteúdo Markdown a ser renderizado
     */
    fun setMarkdown(markdown: String, isUserMessage: Boolean = false) {
        this.isUserMessage = isUserMessage
        children.clear()

        val elements = parseMarkdown(markdown)
        elements.forEach { element ->
            renderElement(element)?.let { node ->
                children.add(node)
            }
        }

        // Aplicar estilo baseado no tipo de mensagem
        applyMessageStyle()
    }

    /**
     * Aplica estilo específico baseado no tipo de mensagem
     */
    private fun applyMessageStyle() {
        if (isUserMessage) {
            styleClass.add("user-markdown-view")
        } else {
            styleClass.add("ai-markdown-view")
        }
    }

    /**
     * Converte markdown em uma lista de elementos estruturados
     */
    private fun parseMarkdown(markdown: String): List<MarkdownElement> {
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
     * Analisa elementos inline dentro de um texto
     */
    private fun parseInlineElements(text: String): List<InlineElement> {
        val elements = mutableListOf<InlineElement>()
        var currentText = text

        val patterns = listOf(
            "\\*\\*(.*?)\\*\\*".toRegex() to { match: MatchResult ->
                InlineElement.BoldText(match.groupValues[1])
            },
            "\\*(.*?)\\*".toRegex() to { match: MatchResult ->
                InlineElement.ItalicText(match.groupValues[1])
            },
            "`(.*?)`".toRegex() to { match: MatchResult ->
                InlineElement.InlineCode(match.groupValues[1])
            },
            "\\[(.*?)\\]\\((.*?)\\)".toRegex() to { match: MatchResult ->
                InlineElement.Link(match.groupValues[1], match.groupValues[2])
            },
            "~~(.*?)~~".toRegex() to { match: MatchResult ->
                InlineElement.StrikeThrough(match.groupValues[1])
            }
        )

        var lastEnd = 0
        val allMatches = patterns.flatMap { (regex, factory) ->
            regex.findAll(currentText).map { match -> match to factory }
        }.sortedBy { it.first.range.first }

        allMatches.forEach { (match, factory) ->
            if (match.range.first > lastEnd) {
                val plainText = currentText.substring(lastEnd, match.range.first)
                if (plainText.isNotEmpty()) {
                    elements.add(InlineElement.PlainText(plainText))
                }
            }

            elements.add(factory(match))
            lastEnd = match.range.last + 1
        }

        if (lastEnd < currentText.length) {
            val remainingText = currentText.substring(lastEnd)
            if (remainingText.isNotEmpty()) {
                elements.add(InlineElement.PlainText(remainingText))
            }
        }

        return elements.ifEmpty { listOf(InlineElement.PlainText(currentText)) }
    }

    /**
     * Renderiza um elemento Markdown em componente JavaFX
     */
    private fun renderElement(element: MarkdownElement): Node? {
        return when (element) {
            is MarkdownElement.Header -> renderHeader(element)
            is MarkdownElement.Paragraph -> renderParagraph(element)
            is MarkdownElement.CodeBlock -> renderCodeBlock(element)
            is MarkdownElement.UnorderedList -> renderUnorderedList(element)
            is MarkdownElement.OrderedList -> renderOrderedList(element)
            is MarkdownElement.EmptyLine -> null
        }
    }

    /**
     * Renderiza cabeçalhos
     */
    private fun renderHeader(header: MarkdownElement.Header): Label {
        val fontSize = (18 - header.level * 1.5).coerceAtLeast(12.0)

        return Label(header.text).apply {
            font = Font.font("Arial", FontWeight.BOLD, fontSize)
            textFill = Color.WHITE
            styleClass.add("markdown-header")
            styleClass.add("markdown-header-${header.level}")
        }
    }

    /**
     * Renderiza parágrafos com elementos inline
     */
    private fun renderParagraph(paragraph: MarkdownElement.Paragraph): TextFlow {
        return TextFlow().apply {
            styleClass.add("markdown-paragraph")

            paragraph.content.forEach { element ->
                val textNode = when (element) {
                    is InlineElement.PlainText -> createPlainText(element.text)
                    is InlineElement.BoldText -> createBoldText(element.text)
                    is InlineElement.ItalicText -> createItalicText(element.text)
                    is InlineElement.InlineCode -> createInlineCode(element.code)
                    is InlineElement.Link -> createLink(element.text, element.url)
                    is InlineElement.StrikeThrough -> createStrikeThrough(element.text)
                }
                children.add(textNode)
            }
        }
    }

    /**
     * Renderiza blocos de código
     */
    private fun renderCodeBlock(codeBlock: MarkdownElement.CodeBlock): Label {
        return Label(codeBlock.code).apply {
            font = Font.font("Consolas", FontWeight.NORMAL, 12.0)
            textFill = Color.WHITE
            styleClass.add("markdown-code-block")
            style = "-fx-background-color: rgba(255, 255, 255, 0.1); -fx-padding: 8px; -fx-background-radius: 4px;"
        }
    }

    /**
     * Renderiza listas não ordenadas
     */
    private fun renderUnorderedList(list: MarkdownElement.UnorderedList): VBox {
        return VBox().apply {
            spacing = 4.0
            styleClass.add("markdown-unordered-list")

            list.items.forEach { item ->
                val listItem = TextFlow().apply {
                    children.addAll(
                        Text("• ").apply {
                            font = Font.font("Arial", FontWeight.BOLD, 14.0)
                            fill = Color.WHITE
                        },
                        Text(item).apply {
                            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
                            fill = Color.WHITE
                        }
                    )
                }
                children.add(listItem)
            }
        }
    }

    /**
     * Renderiza listas ordenadas
     */
    private fun renderOrderedList(list: MarkdownElement.OrderedList): VBox {
        return VBox().apply {
            spacing = 4.0
            styleClass.add("markdown-ordered-list")

            list.items.forEachIndexed { index, item ->
                val listItem = TextFlow().apply {
                    children.addAll(
                        Text("${index + 1}. ").apply {
                            font = Font.font("Arial", FontWeight.BOLD, 14.0)
                            fill = Color.WHITE
                        },
                        Text(item).apply {
                            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
                            fill = Color.WHITE
                        }
                    )
                }
                children.add(listItem)
            }
        }
    }

    /**
     * Funções auxiliares para criar diferentes tipos de texto
     */
    private fun createPlainText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
            fill = Color.WHITE
        }
    }

    private fun createBoldText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, 14.0)
            fill = Color.WHITE
        }
    }

    private fun createItalicText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontPosture.ITALIC, 14.0)
            fill = Color.WHITE
        }
    }

    private fun createInlineCode(code: String): Text {
        return Text(code).apply {
            font = Font.font("Consolas", FontWeight.NORMAL, 12.0)
            fill = Color.WHITE
            style = "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-background-radius: 2px;"
        }
    }

    private fun createStrikeThrough(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
            fill = Color.WHITE
            isStrikethrough = true
        }
    }

    private fun createLink(text: String, url: String): Hyperlink {
        return Hyperlink(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
            textFill = Color.LIGHTBLUE
            styleClass.add("markdown-link")

            setOnAction {
                try {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI(url))
                    }
                } catch (e: Exception) {
                    println("Erro ao abrir link: $url")
                }
            }
        }
    }
}
