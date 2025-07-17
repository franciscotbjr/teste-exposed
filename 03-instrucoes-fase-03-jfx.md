## Renderização de Markdowns nas Mensagens no Chat 
01 - As mensagens na área de chat devem exibir corretamente os textos formatados em Markdown, como negrito, itálico, links e listas.
02 - Substituir o componente de exibição de mensagens atual, o WebView, por um que suporte a renderização de Markdown.

## Detalhes da Implementação
1) Para renderizar o Markdown, utilize o componente `MarkdownView` conforme descrito no arquivo `03_1-instruções-markdown.md`.
2) As mensagens devem ser processadas para converter o Markdown em HTML.

## Manter
00 - Manter o carregamento das mensagens exatamente como está, sem alterações.
01 - Manter a estrutura do projeto organizada e modularizada, sem alterações.
02 - Manter a compatibilidade com diferentes sistemas operacionais, sem alterações.
03 - Manter o SQLite para persistência de dados, sem alterações.
04 - Manter Exposed como ORM (Object Relational Mapping), sem alterações.
05 - Manter Kotlin como linguagem de programação, sem alterações.
06 - Manter Gradle como sistema de build, sem alterações.
07 - Manter DeepSeek IA como API de IA, sem alterações.
08 - Manter Demais dependências e configurações já existentes no projeto, sem alterações.
09 - Manter Compatibilidade para o CLI (Command Line Interface) já existente, sem alterações.
10 - Manter a Compatibilidade de diferentes builds do projeto, como CLI e GUI, sem afetar a funcionalidade principal do sistema, sem alterações.
11 - Manter os alinhamentos das mensagens do usuário à direita e da IA à esquerda, conforme implementado na fase anterior.

## Referências e Recursos
Para aprofundar seu conhecimento nos padrões apresentados, recomendo estas referências essenciais:
Documentação Oficial:

Kotlin Coding Conventions - Guia oficial de convenções do Kotlin
JavaFX Documentation - Documentação completa do JavaFX

### Recursos Especializados:

PragmaticCoding - JavaFX Frameworks - Análise detalhada dos padrões MVC, MVP e MVVM em JavaFX
Griffon Framework MVC Tutorial - Implementação prática dos padrões MVC em JavaFX

### Livros Recomendados:

"Frontend Development with JavaFX and Kotlin" por Peter Späth - Este livro introduz JavaFX como tecnologia frontend e utiliza Kotlin em vez de Java para codificar artefatos do programa, aumentando a expressividade e manutenibilidade do código Frontend Development with JavaFX and Kotlin: Build State-of-the-Art Kotlin GUI Applications | SpringerLink