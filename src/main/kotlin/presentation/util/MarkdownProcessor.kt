package org.hexasilith.presentation.util

import org.commonmark.Extension
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

/**
 * Utilitário para processar Markdown e converter para HTML
 * Suporta:
 * - Formatação básica (negrito, itálico, código)
 * - Links automáticos
 * - Tabelas (GitHub Flavored Markdown)
 * - Texto tachado
 * - Listas ordenadas e não ordenadas
 * - Blocos de código
 */
object MarkdownProcessor {

    private val extensions: List<Extension> = listOf(
        TablesExtension.create(),
        StrikethroughExtension.create(),
        AutolinkExtension.create()
    )

    private val parser: Parser = Parser.builder()
        .extensions(extensions)
        .build()

    private val renderer: HtmlRenderer = HtmlRenderer.builder()
        .extensions(extensions)
        .build()

    /**
     * Converte texto Markdown para HTML
     * @param markdownText Texto em formato Markdown
     * @return HTML renderizado
     */
    fun markdownToHtml(markdownText: String): String {
        val document = parser.parse(markdownText)
        return renderer.render(document)
    }

    /**
     * Converte texto Markdown para HTML com CSS personalizado
     * @param markdownText Texto em formato Markdown
     * @param isUserMessage Indica se é mensagem do usuário para aplicar estilos específicos
     * @return HTML renderizado com CSS inline
     */
    fun markdownToStyledHtml(markdownText: String, isUserMessage: Boolean): String {
        val htmlContent = markdownToHtml(markdownText)
        val backgroundColor = if (isUserMessage) "#3498db" else "#27ae60"
        val textColor = "white"

        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body {
                        font-family: 'Segoe UI', Arial, sans-serif;
                        font-size: 14px;
                        color: $textColor;
                        background-color: $backgroundColor;
                        margin: 0;
                        padding: 12px 16px;
                        line-height: 1.4;
                        word-wrap: break-word;
                    }
                    
                    h1, h2, h3, h4, h5, h6 {
                        color: $textColor;
                        margin: 8px 0 4px 0;
                    }
                    
                    h1 { font-size: 18px; }
                    h2 { font-size: 16px; }
                    h3 { font-size: 15px; }
                    h4, h5, h6 { font-size: 14px; }
                    
                    p {
                        margin: 4px 0;
                    }
                    
                    strong {
                        font-weight: bold;
                        color: $textColor;
                    }
                    
                    em {
                        font-style: italic;
                        color: $textColor;
                    }
                    
                    code {
                        background-color: rgba(255, 255, 255, 0.2);
                        padding: 2px 4px;
                        border-radius: 3px;
                        font-family: 'Consolas', 'Monaco', monospace;
                        font-size: 13px;
                    }
                    
                    pre {
                        background-color: rgba(255, 255, 255, 0.1);
                        padding: 8px;
                        border-radius: 4px;
                        overflow-x: auto;
                        margin: 8px 0;
                    }
                    
                    pre code {
                        background-color: transparent;
                        padding: 0;
                    }
                    
                    ul, ol {
                        margin: 4px 0;
                        padding-left: 20px;
                    }
                    
                    li {
                        margin: 2px 0;
                    }
                    
                    a {
                        color: rgba(255, 255, 255, 0.9);
                        text-decoration: underline;
                    }
                    
                    a:hover {
                        color: $textColor;
                    }
                    
                    blockquote {
                        border-left: 3px solid rgba(255, 255, 255, 0.3);
                        padding-left: 12px;
                        margin: 8px 0;
                        font-style: italic;
                    }
                    
                    table {
                        border-collapse: collapse;
                        width: 100%;
                        margin: 8px 0;
                    }
                    
                    th, td {
                        border: 1px solid rgba(255, 255, 255, 0.3);
                        padding: 6px 8px;
                        text-align: left;
                    }
                    
                    th {
                        background-color: rgba(255, 255, 255, 0.1);
                        font-weight: bold;
                    }
                    
                    del {
                        text-decoration: line-through;
                        opacity: 0.7;
                    }
                    
                    hr {
                        border: none;
                        border-top: 1px solid rgba(255, 255, 255, 0.3);
                        margin: 12px 0;
                    }
                </style>
            </head>
            <body>
                $htmlContent
            </body>
            </html>
        """.trimIndent()
    }
}
