## A Realidade Atual do Ecosistema JavaFX
- Infelizmente, não existe um componente JavaFX nativo que renderize Markdown diretamente. O JavaFX foi projetado com foco em interfaces gráficas tradicionais, e o suporte a formatos de marcação não foi uma prioridade na sua concepção original. Isso significa que, tecnicamente, qualquer renderização de Markdown precisará passar por algum processo de conversão ou interpretação.

## Detalhes da Implementação
- Vamos criar um novo componente JavaFX chamado `MarkdownView` que será responsável por renderizar o Markdown diretamente em componentes JavaFX.
- O `MarkdownView` receberá uma string de Markdown e converterá cada elemento em componentes JavaFX nativos, como `Label`, `Text`, `Hyperlink`, etc.
- O componente será capaz de lidar com formatação básica como negrito, itálico, links e listas.
- O `MarkdownView` será utilizado na área de chat para exibir as mensagens formatadas.
- Vamos garantir que o `MarkdownView` seja eficiente e responsivo, utilizando técnicas de layout adequadas do JavaFX.
- O `MarkdownView` será projetado para ser reutilizável em outras partes do aplicativo, caso necessário.
- Vamos garantir que o `MarkdownView` seja facilmente integrável com o restante do sistema, mantendo a modularidade e a separação de responsabilidades.
- O `MarkdownView` será testado para garantir que renderize corretamente os diferentes tipos de Markdown e que se comporte bem em diferentes tamanhos de tela e resoluções.
- Vamos garantir que o `MarkdownView` seja compatível com as diretrizes de acessibilidade, permitindo que usuários com deficiências visuais possam interagir com o conteúdo renderizado.
- Vamos documentar o `MarkdownView` para que outros desenvolvedores possam entender como utilizá-lo e integrá-lo em seus próprios projetos.
- Vamos garantir que o `MarkdownView` seja facilmente extensível, permitindo que novos tipos de formatação possam ser adicionados no futuro sem grandes dificuldades.
- Vamos garantir que o `MarkdownView` seja testado em diferentes versões do JavaFX para garantir compatibilidade e estabilidade.
- Vamos garantir que o `MarkdownView` seja otimizado para desempenho, evitando renderizações desnecessárias e garantindo uma experiência fluida para o usuário.
- Vamos garantir que o `MarkdownView` seja facilmente configurável, permitindo que desenvolvedores possam personalizar seu comportamento e aparência conforme necessário.
- Vamos garantir que o `MarkdownView` seja integrado com o sistema de mensagens existente, permitindo que as mensagens enviadas e recebidas sejam renderizadas corretamente.
- Vamos garantir que o `MarkdownView` esteja pronto para externalizado em um repositório público, permitindo que outros desenvolvedores possam utilizá-lo em seus próprios projetos.

## Substituição
- O Componente `MarkdownView` substituirá o WebView atual de exibição de mensagens na área de chat.

## Informações Adicionais
- Vamos explorar como poderíamos construir um sistema que leia Markdown e produza uma árvore de componentes JavaFX. Isso nos daria o controle total sobre a renderização e evitaria as complexidades de segurança do HTML.
- Primeiro, precisamos entender a estrutura básica do Markdown. O Markdown funciona com uma sintaxe relativamente simples: cabeçalhos usam #, texto em negrito usa **, links usam [texto](url), e assim por diante. Podemos criar um parser que reconheça esses padrões e os converta diretamente em componentes JavaFX.

## Aqui está um exemplo conceitual de como isso funcionaria:
- Os exemplos abaixo são apenas para fins de demonstração e não devem ser usados diretamente no projeto. Eles ilustram como você poderia estruturar um renderizador Markdown em Kotlin usando JavaFX.
- Vamos começar com um exemplo fundamental que demonstra como criar um renderizador Markdown básico em Kotlin:

```kotlin
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.paint.Color

class MarkdownRenderer {
    
    /**
     * Função principal que converte markdown em componentes JavaFX
     * Note como usamos 'apply' para configurar o VBox de forma mais fluida
     */
    fun renderMarkdown(markdown: String): VBox {
        return VBox().apply {
            spacing = 8.0
            // Dividimos o markdown em linhas e processamos cada uma
            markdown.lines().forEach { line ->
                parseLine(line)?.let { component ->
                    children.add(component)
                }
            }
        }
    }
    
    /**
     * Analisa uma linha individual de markdown
     * Usamos when em vez de if-else encadeados para maior clareza
     */
    private fun parseLine(line: String): Node? {
        return when {
            line.startsWith("# ") -> createHeader(line.removePrefix("# "), 1)
            line.startsWith("## ") -> createHeader(line.removePrefix("## "), 2)
            line.startsWith("### ") -> createHeader(line.removePrefix("### "), 3)
            line.trim().isNotEmpty() -> createParagraph(line)
            else -> null // Linha vazia
        }
    }
    
    /**
     * Cria cabeçalhos com diferentes níveis
     * Note como usamos a expressão when para determinar o tamanho da fonte
     */
    private fun createHeader(text: String, level: Int): Label {
        val fontSize = when (level) {
            1 -> 24.0
            2 -> 20.0
            3 -> 16.0
            else -> 14.0
        }
        
        return Label(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, fontSize)
            textFill = Color.DARKBLUE
        }
    }
    
    /**
     * Cria parágrafos com suporte a formatação inline
     * Aqui vemos como o Kotlin nos permite encadear operações de forma mais natural
     */
    private fun createParagraph(text: String): TextFlow {
        return TextFlow().apply {
            parseInlineFormatting(text, this)
        }
    }
    
    /**
     * Processa formatação inline como negrito e itálico
     * Este exemplo mostra como o Kotlin facilita o trabalho com strings e loops
     */
    private fun parseInlineFormatting(text: String, container: TextFlow) {
        // Dividimos o texto em partes, procurando por marcadores de negrito
        val parts = text.split("(?=\\*\\*)|(?<=\\*\\*)".toRegex())
        var isBold = false
        
        parts.forEach { part ->
            when {
                part == "**" -> isBold = !isBold // Alternar estado de negrito
                part.isNotEmpty() -> {
                    val textNode = Text(part).apply {
                        font = if (isBold) {
                            Font.font("Arial", FontWeight.BOLD, 14.0)
                        } else {
                            Font.font("Arial", FontWeight.NORMAL, 14.0)
                        }
                    }
                    container.children.add(textNode)
                }
            }
        }
    }
}
```
### Aprimorando com Recursos Avançados do Kotlin
- Agora, vamos ver como podemos usar recursos mais avançados do Kotlin, como sealed classes e funções de extensão, para criar uma versão mais robusta e expressiva:
```kotlin
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.scene.text.*
import javafx.scene.paint.Color

/**
 * Sealed class para representar diferentes tipos de elementos Markdown
 * Isso nos dá type safety e facilita o pattern matching
 */
sealed class MarkdownElement {
    data class Header(val text: String, val level: Int) : MarkdownElement()
    data class Paragraph(val content: List<InlineElement>) : MarkdownElement()
    data class EmptyLine(val placeholder: Unit = Unit) : MarkdownElement()
}

/**
 * Elementos inline dentro de parágrafos
 * Note como usamos data classes para representar diferentes tipos de conteúdo
 */
sealed class InlineElement {
    data class PlainText(val text: String) : InlineElement()
    data class BoldText(val text: String) : InlineElement()
    data class ItalicText(val text: String) : InlineElement()
    data class Link(val text: String, val url: String) : InlineElement()
}

/**
 * Extensão para String que facilita a detecção de padrões Markdown
 */
fun String.isHeader(): Boolean = startsWith("#")
fun String.getHeaderLevel(): Int = takeWhile { it == '#' }.length
fun String.getHeaderText(): String = dropWhile { it == '#' }.trim()

/**
 * Versão aprimorada do renderizador usando recursos avançados do Kotlin
 */
class AdvancedMarkdownRenderer {
    
    /**
     * Converte markdown em uma lista de elementos estruturados
     * Separamos a análise da renderização para maior flexibilidade
     */
    fun parseMarkdown(markdown: String): List<MarkdownElement> {
        return markdown.lines().mapNotNull { line ->
            when {
                line.isHeader() -> MarkdownElement.Header(
                    text = line.getHeaderText(),
                    level = line.getHeaderLevel()
                )
                line.trim().isNotEmpty() -> MarkdownElement.Paragraph(
                    content = parseInlineElements(line)
                )
                else -> null
            }
        }
    }
    
    /**
     * Analisa elementos inline dentro de um texto
     * Usamos regex com grupos nomeados para maior clareza
     */
    private fun parseInlineElements(text: String): List<InlineElement> {
        val elements = mutableListOf<InlineElement>()
        var currentText = text
        
        // Regex para encontrar diferentes padrões de formatação
        val patterns = mapOf(
            "\\*\\*(.*?)\\*\\*".toRegex() to { match: MatchResult ->
                InlineElement.BoldText(match.groupValues[1])
            },
            "\\*(.*?)\\*".toRegex() to { match: MatchResult ->
                InlineElement.ItalicText(match.groupValues[1])
            },
            "\\[(.*?)\\]\\((.*?)\\)".toRegex() to { match: MatchResult ->
                InlineElement.Link(match.groupValues[1], match.groupValues[2])
            }
        )
        
        // Processamos cada padrão e construímos a lista de elementos
        var lastEnd = 0
        val allMatches = patterns.flatMap { (regex, factory) ->
            regex.findAll(currentText).map { match -> match to factory }
        }.sortedBy { it.first.range.first }
        
        allMatches.forEach { (match, factory) ->
            // Adicionar texto antes do match
            if (match.range.first > lastEnd) {
                val plainText = currentText.substring(lastEnd, match.range.first)
                if (plainText.isNotEmpty()) {
                    elements.add(InlineElement.PlainText(plainText))
                }
            }
            
            // Adicionar elemento formatado
            elements.add(factory(match))
            lastEnd = match.range.last + 1
        }
        
        // Adicionar texto restante
        if (lastEnd < currentText.length) {
            val remainingText = currentText.substring(lastEnd)
            if (remainingText.isNotEmpty()) {
                elements.add(InlineElement.PlainText(remainingText))
            }
        }
        
        return elements.ifEmpty { listOf(InlineElement.PlainText(currentText)) }
    }
    
    /**
     * Renderiza os elementos parseados em componentes JavaFX
     * Note como usamos when exhaustivo com sealed classes
     */
    fun renderElements(elements: List<MarkdownElement>): VBox {
        return VBox().apply {
            spacing = 8.0
            elements.forEach { element ->
                val node = when (element) {
                    is MarkdownElement.Header -> renderHeader(element)
                    is MarkdownElement.Paragraph -> renderParagraph(element)
                    is MarkdownElement.EmptyLine -> null
                }
                node?.let { children.add(it) }
            }
        }
    }
    
    /**
     * Renderiza cabeçalhos com estilo baseado no nível
     */
    private fun renderHeader(header: MarkdownElement.Header): Label {
        val fontSize = (26 - header.level * 2).toDouble()
        
        return Label(header.text).apply {
            font = Font.font("Arial", FontWeight.BOLD, fontSize)
            textFill = Color.DARKBLUE
            // Adicionar margem superior para cabeçalhos
            style = "-fx-padding: 10 0 5 0;"
        }
    }
    
    /**
     * Renderiza parágrafos com elementos inline
     * Aqui vemos como o when exhaustivo garante que tratamos todos os casos
     */
    private fun renderParagraph(paragraph: MarkdownElement.Paragraph): TextFlow {
        return TextFlow().apply {
            paragraph.content.forEach { element ->
                val textNode = when (element) {
                    is InlineElement.PlainText -> createPlainText(element.text)
                    is InlineElement.BoldText -> createBoldText(element.text)
                    is InlineElement.ItalicText -> createItalicText(element.text)
                    is InlineElement.Link -> createLinkText(element.text, element.url)
                }
                children.add(textNode)
            }
        }
    }
    
    /**
     * Funções auxiliares para criar diferentes tipos de texto
     * Note como usamos apply para configuração fluida
     */
    private fun createPlainText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
        }
    }
    
    private fun createBoldText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, 14.0)
        }
    }
    
    private fun createItalicText(text: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontPosture.ITALIC, 14.0)
        }
    }
    
    private fun createLinkText(text: String, url: String): Text {
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
            fill = Color.BLUE
            isUnderline = true
            // Adicionar handler para clique no link
            setOnMouseClicked { 
                println("Link clicado: $url") // Aqui você implementaria a abertura do link
            }
            style = "-fx-cursor: hand;"
        }
    }
    
    /**
     * Função de conveniência que combina parsing e renderização
     */
    fun renderMarkdown(markdown: String): VBox {
        val elements = parseMarkdown(markdown)
        return renderElements(elements)
    }
}
```
### Utilizando a Biblioteca CommonMark com Kotlin
- Agora vamos ver como adaptar a biblioteca CommonMark para trabalhar mais elegantemente com Kotlin:
```kotlin
import org.commonmark.parser.Parser
import org.commonmark.node.*
import javafx.scene.layout.VBox
import javafx.scene.text.TextFlow
import javafx.scene.control.Label
import javafx.scene.text.Text
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.FontPosture

/**
 * Renderizador que usa a biblioteca CommonMark com idiomas Kotlin
 */
class CommonMarkRenderer {
    
    private val parser = Parser.builder().build()
    
    /**
     * Função principal que combina parsing e renderização
     */
    fun renderMarkdown(markdown: String): VBox {
        val document = parser.parse(markdown)
        return VBox().apply {
            spacing = 10.0
            walkTree(document) { node ->
                renderNode(node)?.let { children.add(it) }
            }
        }
    }
    
    /**
     * Função de extensão para percorrer a árvore de nós
     * Isso demonstra como o Kotlin facilita a criação de DSLs
     */
    private fun walkTree(node: Node, action: (Node) -> Unit) {
        action(node)
        var child = node.firstChild
        while (child != null) {
            walkTree(child, action)
            child = child.next
        }
    }
    
    /**
     * Renderiza diferentes tipos de nós
     * Pattern matching com when torna o código mais legível
     */
    private fun renderNode(node: Node): javafx.scene.Node? {
        return when (node) {
            is Heading -> renderHeading(node)
            is Paragraph -> renderParagraph(node)
            else -> null
        }
    }
    
    /**
     * Renderiza cabeçalhos extraindo o texto de forma funcional
     */
    private fun renderHeading(heading: Heading): Label {
        val text = extractText(heading)
        val fontSize = (20 - heading.level * 1.5).coerceAtLeast(12.0)
        
        return Label(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, fontSize)
        }
    }
    
    /**
     * Renderiza parágrafos processando elementos inline
     */
    private fun renderParagraph(paragraph: Paragraph): TextFlow {
        return TextFlow().apply {
            processInlineElements(paragraph, this)
        }
    }
    
    /**
     * Processa elementos inline de forma recursiva
     */
    private fun processInlineElements(parent: Node, container: TextFlow) {
        var child = parent.firstChild
        while (child != null) {
            val textNode = when (child) {
                is org.commonmark.node.Text -> createText(child.literal)
                is Emphasis -> createEmphasis(child)
                is StrongEmphasis -> createStrongEmphasis(child)
                is Link -> createLink(child)
                else -> null
            }
            
            textNode?.let { container.children.add(it) }
            child = child.next
        }
    }
    
    /**
     * Funções para criar diferentes tipos de texto
     * Note como usamos higher-order functions para reutilizar código
     */
    private fun createText(content: String): Text {
        return Text(content).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
        }
    }
    
    private fun createEmphasis(emphasis: Emphasis): Text {
        val text = extractText(emphasis)
        return Text(text).apply {
            font = Font.font("Arial", FontPosture.ITALIC, 14.0)
        }
    }
    
    private fun createStrongEmphasis(strong: StrongEmphasis): Text {
        val text = extractText(strong)
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.BOLD, 14.0)
        }
    }
    
    private fun createLink(link: Link): Text {
        val text = extractText(link)
        return Text(text).apply {
            font = Font.font("Arial", FontWeight.NORMAL, 14.0)
            fill = javafx.scene.paint.Color.BLUE
            isUnderline = true
            setOnMouseClicked { 
                println("Link para: ${link.destination}")
            }
        }
    }
    
    /**
     * Função auxiliar para extrair texto de qualquer nó
     * Usamos tail recursion para eficiência
     */
    private tailrec fun extractText(node: Node, accumulator: StringBuilder = StringBuilder()): String {
        when (node) {
            is org.commonmark.node.Text -> accumulator.append(node.literal)
            else -> {
                var child = node.firstChild
                while (child != null) {
                    extractText(child, accumulator)
                    child = child.next
                }
            }
        }
        return accumulator.toString()
    }
}
```
### Exemplo de Uso Prático (APENAS REFEREÊNCIA, NÃO USAR NO PROJETO)
- Vamos criar um exemplo que demonstra como usar esses renderizadores em uma aplicação JavaFX real:
```kotlin
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.stage.Stage

class MarkdownViewerApp : Application() {
    
    override fun start(primaryStage: Stage) {
        val markdownContent = """
            # Título Principal
            
            Este é um parágrafo com **texto em negrito** e *texto em itálico*.
            
            ## Subtítulo
            
            Aqui temos um [link para o Google](https://www.google.com) e mais texto.
            
            ### Subtítulo Menor
            
            Último parágrafo com formatação **combinada e *aninhada***.
        """.trimIndent()
        
        // Usar o renderizador avançado
        val renderer = AdvancedMarkdownRenderer()
        val content = renderer.renderMarkdown(markdownContent)
        
        // Criar interface
        val scrollPane = ScrollPane(content).apply {
            isFitToWidth = true
            style = "-fx-padding: 20;"
        }
        
        val scene = Scene(scrollPane, 800.0, 600.0)
        
        primaryStage.apply {
            title = "Visualizador Markdown"
            scene = scene
            show()
        }
    }
}

fun main() {
    Application.launch(MarkdownViewerApp::class.java)
}
```

## Reflexões sobre a Implementação em Kotlin
- Observe como o Kotlin nos permitiu criar um código mais expressivo e menos propenso a erros. As sealed classes garantem que tratamos todos os casos possíveis, as funções de extensão nos permitem adicionar funcionalidades de forma natural, e o sistema de null safety reduz significativamente a possibilidade de NullPointerExceptions.
- A abordagem funcional do Kotlin também nos permite pensar sobre o problema de forma diferente. Em vez de focar em loops e mutabilidade, podemos usar operações como map, filter, e fold para transformar dados de forma mais declarativa.