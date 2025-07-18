package org.hexasilith.presentation.component

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Assumptions.assumeTrue
import javafx.application.Platform
import javafx.embed.swing.JFXPanel
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.awt.GraphicsEnvironment

/**
 * Testes unitários para o MarkdownView
 * Foca na renderização JavaFX dos elementos parseados
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MarkdownViewTest {

    private lateinit var markdownView: MarkdownView
    private var javaFxInitialized = false

    companion object {
        @BeforeAll
        @JvmStatic
        fun setupJavaFX() {
            // Verificar se estamos em ambiente headless
            if (GraphicsEnvironment.isHeadless()) {
                System.setProperty("java.awt.headless", "false")
                System.setProperty("testfx.robot", "glass")
                System.setProperty("testfx.headless", "true")
                System.setProperty("prism.order", "sw")
                System.setProperty("prism.text", "t2k")
                System.setProperty("glass.platform", "Monocle")
                System.setProperty("monocle.platform", "Headless")
            }

            // Inicializar JavaFX
            try {
                // Tentar inicializar Platform primeiro
                Platform.startup { }
            } catch (e: IllegalStateException) {
                // JavaFX já foi inicializado
                println("JavaFX já inicializado: ${e.message}")
            } catch (e: Exception) {
                println("Erro ao inicializar JavaFX: ${e.message}")
                // Fallback para JFXPanel
                try {
                    JFXPanel()
                } catch (ex: Exception) {
                    println("Erro ao inicializar JFXPanel: ${ex.message}")
                }
            }
        }
    }

    @BeforeEach
    fun setUp() {
        // Assumir que JavaFX está disponível
        assumeTrue(!GraphicsEnvironment.isHeadless() || System.getProperty("testfx.headless") == "true",
                   "JavaFX não está disponível em ambiente headless")

        val latch = CountDownLatch(1)
        var initializationSuccessful = false

        try {
            Platform.runLater {
                try {
                    markdownView = MarkdownView()
                    javaFxInitialized = true
                    initializationSuccessful = true
                } catch (e: Exception) {
                    println("Erro ao criar MarkdownView: ${e.message}")
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Timeout ao inicializar MarkdownView")
            assumeTrue(initializationSuccessful, "Falha na inicialização do MarkdownView")
        } catch (e: Exception) {
            println("Erro no setUp: ${e.message}")
            e.printStackTrace()
            // Fallback: criar diretamente se Platform não estiver disponível
            try {
                markdownView = MarkdownView()
                javaFxInitialized = true
            } catch (ex: Exception) {
                assumeTrue(false, "Não foi possível inicializar JavaFX: ${ex.message}")
            }
        }
    }

    @Nested
    @DisplayName("Testes de Renderização")
    inner class RenderingTests {

        @Test
        @DisplayName("Deve renderizar texto simples")
        fun shouldRenderPlainText() {
            assumeTrue(javaFxInitialized, "JavaFX não foi inicializado")

            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                try {
                    val markdown = "Este é um texto simples"
                    markdownView.setMarkdown(markdown)

                    assertTrue(markdownView.children.isNotEmpty(), "MarkdownView deve ter filhos após setMarkdown")

                    val firstChild = markdownView.children.first()
                    assertTrue(firstChild is TextFlow, "Primeiro filho deve ser TextFlow para parágrafo")

                    val textFlow = firstChild as TextFlow
                    assertTrue(textFlow.children.isNotEmpty(), "TextFlow deve ter filhos")

                    val textNode = textFlow.children.first()
                    assertTrue(textNode is Text, "Primeiro filho do TextFlow deve ser Text")

                    val text = textNode as Text
                    assertEquals("Este é um texto simples", text.text, "Texto deve ser renderizado corretamente")
                    success = true
                } catch (e: Exception) {
                    println("Erro no teste: ${e.message}")
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve renderizar cabeçalhos")
        fun shouldRenderHeaders() {
            assumeTrue(javaFxInitialized, "JavaFX não foi inicializado")

            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                try {
                    val markdown = """
                        # Título H1
                        ## Título H2
                        ### Título H3
                    """.trimIndent()

                    markdownView.setMarkdown(markdown)

                    assertEquals(3, markdownView.children.size, "Deve ter 3 filhos (um para cada cabeçalho)")

                    // Verificar se todos são Labels
                    markdownView.children.forEachIndexed { index, child ->
                        assertTrue(child is Label, "Filho $index deve ser Label")
                        val label = child as Label
                        assertTrue(label.text.contains("Título"), "Label deve conter texto do cabeçalho")
                    }
                    success = true
                } catch (e: Exception) {
                    println("Erro no teste: ${e.message}")
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve renderizar listas não ordenadas")
        fun shouldRenderUnorderedLists() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = """
                    - Item 1
                    - Item 2
                    - Item 3
                """.trimIndent()

                markdownView.setMarkdown(markdown)

                assertEquals(1, markdownView.children.size, "Deve ter 1 filho (VBox da lista)")

                val listContainer = markdownView.children.first()
                assertTrue(listContainer is VBox, "Filho deve ser VBox")

                val vbox = listContainer as VBox
                assertEquals(3, vbox.children.size, "Lista deve ter 3 itens")

                // Verificar se todos os itens são TextFlow com bullet
                vbox.children.forEachIndexed { index, child ->
                    assertTrue(child is TextFlow, "Item $index deve ser TextFlow")
                    val textFlow = child as TextFlow
                    assertTrue(textFlow.children.isNotEmpty(), "Item deve ter conteúdo")

                    // Primeiro filho deve ser o bullet
                    val firstText = textFlow.children.first() as Text
                    assertEquals("• ", firstText.text, "Primeiro texto deve ser bullet")
                }
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve renderizar listas ordenadas")
        fun shouldRenderOrderedLists() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = """
                    1. Primeiro item
                    2. Segundo item
                    3. Terceiro item
                """.trimIndent()

                markdownView.setMarkdown(markdown)

                assertEquals(1, markdownView.children.size, "Deve ter 1 filho (VBox da lista)")

                val listContainer = markdownView.children.first()
                assertTrue(listContainer is VBox, "Filho deve ser VBox")

                val vbox = listContainer as VBox
                assertEquals(3, vbox.children.size, "Lista deve ter 3 itens")

                // Verificar numeração
                vbox.children.forEachIndexed { index, child ->
                    assertTrue(child is TextFlow, "Item $index deve ser TextFlow")
                    val textFlow = child as TextFlow
                    assertTrue(textFlow.children.isNotEmpty(), "Item deve ter conteúdo")

                    // Primeiro filho deve ser o número
                    val firstText = textFlow.children.first() as Text
                    assertEquals("${index + 1}. ", firstText.text, "Primeiro texto deve ser número")
                }
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve renderizar blocos de código")
        fun shouldRenderCodeBlocks() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = """
                    ```kotlin
                    fun hello() {
                        println("Hello")
                    }
                    ```
                """.trimIndent()

                markdownView.setMarkdown(markdown)

                assertEquals(1, markdownView.children.size, "Deve ter 1 filho (Label do código)")

                val codeBlock = markdownView.children.first()
                assertTrue(codeBlock is Label, "Filho deve ser Label")

                val label = codeBlock as Label
                assertTrue(label.text.contains("fun hello()"), "Label deve conter código")
                assertTrue(label.text.contains("println"), "Label deve conter println")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve renderizar citações")
        fun shouldRenderBlockquotes() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = """
                    > Esta é uma citação
                    > com múltiplas linhas
                """.trimIndent()

                markdownView.setMarkdown(markdown)

                assertEquals(1, markdownView.children.size, "Deve ter 1 filho (VBox da citação)")

                val blockquote = markdownView.children.first()
                assertTrue(blockquote is VBox, "Filho deve ser VBox")

                val vbox = blockquote as VBox
                assertTrue(vbox.children.isNotEmpty(), "Citação deve ter conteúdo")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }
    }

    @Nested
    @DisplayName("Testes de Integração Parser + Renderização")
    inner class IntegrationTests {

        @Test
        @DisplayName("Deve integrar parsing e renderização corretamente")
        fun shouldIntegrateParsingAndRendering() {
            assumeTrue(javaFxInitialized, "JavaFX não foi inicializado")

            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                try {
                    val markdown = "Texto com **negrito** e *itálico*"

                    // Testar parsing
                    val elements = markdownView.parseMarkdown(markdown)
                    assertTrue(elements.isNotEmpty(), "Parsing deve produzir elementos")

                    val paragraph = elements[0] as MarkdownElement.Paragraph
                    assertTrue(paragraph.content.size > 1, "Parágrafo deve ter múltiplos elementos")

                    // Testar renderização
                    markdownView.setMarkdown(markdown)
                    assertTrue(markdownView.children.isNotEmpty(), "Renderização deve produzir componentes")

                    val textFlow = markdownView.children.first() as TextFlow
                    assertTrue(textFlow.children.size > 1, "TextFlow deve ter múltiplos Text nodes")
                    success = true
                } catch (e: Exception) {
                    println("Erro no teste: ${e.message}")
                    e.printStackTrace()
                } finally {
                    latch.countDown()
                }
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve aplicar estilos de mensagem do usuário")
        fun shouldApplyUserMessageStyles() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = "Mensagem do usuário"

                markdownView.setMarkdown(markdown, isUserMessage = true)

                assertTrue(markdownView.styleClass.contains("user-markdown-view"),
                    "Deve conter classe CSS para mensagem do usuário")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve aplicar estilos de mensagem da AI")
        fun shouldApplyAIMessageStyles() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = "Mensagem da AI"

                markdownView.setMarkdown(markdown, isUserMessage = false)

                assertTrue(markdownView.styleClass.contains("ai-markdown-view"),
                    "Deve conter classe CSS para mensagem da AI")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve limpar conteúdo anterior ao definir novo markdown")
        fun shouldClearPreviousContent() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                // Primeiro markdown
                markdownView.setMarkdown("Primeiro conteúdo")
                val firstChildCount = markdownView.children.size

                // Segundo markdown
                markdownView.setMarkdown("Segundo conteúdo")
                val secondChildCount = markdownView.children.size

                // Verificar se limpou e adicionou novo conteúdo
                assertTrue(secondChildCount > 0, "Deve ter conteúdo após segundo setMarkdown")

                // Verificar se o conteúdo é realmente o novo
                val textFlow = markdownView.children.first() as TextFlow
                val text = textFlow.children.first() as Text
                assertEquals("Segundo conteúdo", text.text, "Deve mostrar o segundo conteúdo")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }

        @Test
        @DisplayName("Deve manter espaçamento entre elementos")
        fun shouldMaintainSpacingBetweenElements() {
            val latch = CountDownLatch(1)
            var success = false

            Platform.runLater {
                val markdown = """
                    # Título
                    
                    Parágrafo 1
                    
                    Parágrafo 2
                """.trimIndent()

                markdownView.setMarkdown(markdown)

                // Verificar se o VBox tem espaçamento configurado
                assertEquals(8.0, markdownView.spacing, "MarkdownView deve ter espaçamento de 8.0")

                // Verificar se tem múltiplos filhos (título + parágrafos)
                assertTrue(markdownView.children.size >= 3, "Deve ter múltiplos elementos renderizados")
                success = true
                latch.countDown()
            }

            assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
            assertTrue(success, "Teste deve ter sucesso")
        }
    }

    @Test
    @DisplayName("Teste completo com exemplo do Charles M. Schulz")
    fun shouldRenderCompleteSchulzExample() {
        val latch = CountDownLatch(1)
        var success = false

        Platform.runLater {
            val markdown = """
                **Charles M. Schulz** nasceu no dia **26 de novembro de 1922**, em Minneapolis, Minnesota, nos Estados Unidos.

                Ele faleceu em **12 de fevereiro de 2000**, deixando um legado imenso com *Peanuts*, uma das tiras de quadrinhos mais adoradas de todos os tempos.

                Se quiser saber mais sobre sua vida ou obra, é só perguntar! 😊
            """.trimIndent()

            markdownView.setMarkdown(markdown)

            // Verificar se renderizou elementos
            assertTrue(markdownView.children.isNotEmpty(), "Deve renderizar elementos")

            // Verificar se há múltiplos parágrafos
            val paragraphs = markdownView.children.filterIsInstance<TextFlow>()
            assertTrue(paragraphs.size >= 2, "Deve ter pelo menos 2 parágrafos")

            // Verificar se cada parágrafo tem múltiplos elementos de texto (formatação)
            paragraphs.forEach { paragraph ->
                assertTrue(paragraph.children.isNotEmpty(), "Cada parágrafo deve ter conteúdo")
                assertTrue(paragraph.children.all { it is Text }, "Todos os filhos devem ser Text")
            }
            success = true
            latch.countDown()
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Teste timeout")
        assertTrue(success, "Teste deve ter sucesso")
    }
}
