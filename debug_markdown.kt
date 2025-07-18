import org.hexasilith.presentation.component.MarkdownParser
import org.hexasilith.presentation.component.MarkdownElement
import org.hexasilith.presentation.component.InlineElement

fun main() {
    val parser = MarkdownParser()
    val markdown = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"

    println("Input: $markdown")
    println("=" * 50)

    val elements = parser.parseMarkdown(markdown)

    elements.forEach { element ->
        when (element) {
            is MarkdownElement.Paragraph -> {
                println("Paragraph found with ${element.content.size} inline elements:")
                element.content.forEachIndexed { index, inlineElement ->
                    when (inlineElement) {
                        is InlineElement.PlainText -> println("  [$index] PlainText: '${inlineElement.text}'")
                        is InlineElement.BoldText -> println("  [$index] BoldText: '${inlineElement.text}'")
                        is InlineElement.ItalicText -> println("  [$index] ItalicText: '${inlineElement.text}'")
                        is InlineElement.BoldItalicText -> println("  [$index] BoldItalicText: '${inlineElement.text}'")
                        is InlineElement.InlineCode -> println("  [$index] InlineCode: '${inlineElement.code}'")
                        is InlineElement.Link -> println("  [$index] Link: '${inlineElement.text}' -> '${inlineElement.url}'")
                        is InlineElement.StrikeThrough -> println("  [$index] StrikeThrough: '${inlineElement.text}'")
                    }
                }
            }
            else -> println("Other element: $element")
        }
    }
}
