package org.hexasilith.presentation.component

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.control.TextArea
import javafx.scene.layout.VBox
import javafx.scene.layout.HBox
import javafx.scene.control.Button
import javafx.stage.Stage

/**
 * Aplicação de teste simples para verificar se o negrito está funcionando
 */
class SimpleMarkdownTestApp : Application() {

    override fun start(primaryStage: Stage) {
        val markdownView = MarkdownView()
        val testArea = TextArea().apply {
            text = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"
            prefRowCount = 3
        }

        val testButton = Button("Testar Negrito").apply {
            setOnAction {
                println("Testando: ${testArea.text}")

                // Testar parser diretamente
                val elements = markdownView.parseMarkdown(testArea.text)
                println("Elementos encontrados: ${elements.size}")

                elements.forEach { element ->
                    when (element) {
                        is MarkdownElement.Paragraph -> {
                            println("Parágrafo com ${element.content.size} elementos inline:")
                            element.content.forEachIndexed { i, inline ->
                                when (inline) {
                                    is InlineElement.PlainText -> println("  [$i] Texto: '${inline.text}'")
                                    is InlineElement.BoldText -> println("  [$i] *** NEGRITO ***: '${inline.text}'")
                                    is InlineElement.ItalicText -> println("  [$i] Itálico: '${inline.text}'")
                                    else -> println("  [$i] Outro: $inline")
                                }
                            }
                        }
                        else -> println("Elemento: $element")
                    }
                }

                // Renderizar na UI
                markdownView.setMarkdown(testArea.text)
            }
        }

        val controls = HBox(10.0, testButton)
        val content = VBox(10.0, testArea, controls, ScrollPane(markdownView).apply {
            isFitToWidth = true
            prefHeight = 300.0
        })

        val sceneInstance = Scene(content, 600.0, 500.0)
        primaryStage.apply {
            title = "Teste Negrito - MarkdownView"
            scene = sceneInstance
            show()
        }

        // Testar automaticamente na inicialização
        testButton.fire()
    }
}

fun main() {
    Application.launch(SimpleMarkdownTestApp::class.java)
}
