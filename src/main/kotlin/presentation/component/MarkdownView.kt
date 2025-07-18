package org.hexasilith.presentation.component

import javafx.scene.Node
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.layout.Region
import javafx.scene.text.*
import javafx.scene.paint.Color
import java.awt.Desktop
import java.net.URI

/**
 * Componente JavaFX personalizado para renderizar Markdown
 * Substitui o WebView para renderização nativa
 */
class MarkdownView : VBox() {

    private var isUserMessage: Boolean = false
    private val parser = MarkdownParser()

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

        val elements = parser.parseMarkdown(markdown)
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
     * Método público para permitir testes unitários
     */
    fun parseMarkdown(markdown: String): List<MarkdownElement> {
        return parser.parseMarkdown(markdown)
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
            is MarkdownElement.Blockquote -> renderBlockquote(element)
            is MarkdownElement.HorizontalRule -> renderHorizontalRule()
            is MarkdownElement.LineBreak -> renderLineBreak()
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
                    is InlineElement.BoldItalicText -> createBoldItalicText(element.text)
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
     * Renderiza blocos de citação
     */
    private fun renderBlockquote(blockquote: MarkdownElement.Blockquote): VBox {
        return VBox().apply {
            spacing = 4.0
            styleClass.add("markdown-blockquote")

            val quoteNode = Text(blockquote.content).apply {
                font = Font.font("Arial", FontWeight.NORMAL, 14.0)
                fill = Color.WHITE
                style = "-fx-font-style: italic;"
            }

            children.add(quoteNode)
        }
    }

    /**
     * Renderiza linhas horizontais
     */
    private fun renderHorizontalRule(): Node {
        return Region().apply {
            prefHeight = 1.0
            styleClass.add("markdown-horizontal-rule")
        }
    }

    /**
     * Renderiza quebras de linha
     */
    private fun renderLineBreak(): Node {
        return Region().apply {
            prefHeight = 8.0 // Espaço para a quebra de linha
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

    private fun createBoldItalicText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, 14.0)
            fill = Color.WHITE
            style = "-fx-font-style: italic;"
        }
    }
}
