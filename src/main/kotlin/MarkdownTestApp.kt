package org.hexasilith.presentation.component

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import javafx.stage.Stage

/**
 * Aplicação de teste para verificar o funcionamento do MarkdownView
 */
class MarkdownTestApp : Application() {

    override fun start(primaryStage: Stage) {
        val markdownView = MarkdownView()

        // Testar com o exemplo que está falhando
        val testMarkdown = "**Charles M. Schulz** nasceu no dia **26 de novembro de 1922**"

        println("Testando: $testMarkdown")

        // Testar o parser diretamente
        val elements = markdownView.parseMarkdown(testMarkdown)
        println("Elementos parseados:")
        elements.forEach { element ->
            when (element) {
                is MarkdownElement.Paragraph -> {
                    println("  Parágrafo com ${element.content.size} elementos:")
                    element.content.forEachIndexed { index, inline ->
                        when (inline) {
                            is InlineElement.PlainText -> println("    [$index] Texto: '${inline.text}'")
                            is InlineElement.BoldText -> println("    [$index] Negrito: '${inline.text}'")
                            is InlineElement.ItalicText -> println("    [$index] Itálico: '${inline.text}'")
                            else -> println("    [$index] Outro: $inline")
                        }
                    }
                }
                else -> println("  Outro elemento: $element")
            }
        }

        // Renderizar na interface
        markdownView.setMarkdown(testMarkdown)

        val scrollPane = ScrollPane(markdownView).apply {
            isFitToWidth = true
            style = "-fx-padding: 20;"
        }

        val sceneInstance = Scene(scrollPane, 600.0, 400.0)

        primaryStage.apply {
            title = "Teste MarkdownView - Negrito"
            scene = sceneInstance
            show()
        }
    }
}

fun main() {
    Application.launch(MarkdownTestApp::class.java)
}
