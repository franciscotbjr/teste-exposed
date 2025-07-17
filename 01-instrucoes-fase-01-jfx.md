# ALTERAR O PROJETO PARA QUE O USUÁRIO TENHA ACESSO A UMA INTERFACE GRÁFICA
O Objetivo é criar um tela inicial que tem o objetivo de se comportar com um Client de APIs de IA com o Client do DeepSeek IA.

As alterações não devem alterar ainda nenhuma funcionalidade já existente do projeto, apenas adicionar uma interface gráfica que permita ao usuário interagir com o sistema de forma mais amigável.

## Esta é a fase um da implementação da interface gráfica, que tem como objetivo criar uma tela inicial que permita ao usuário interagir com o sistema de forma mais amigável.
01 - Nessa fase, o foco é criar uma interface gráfica básica ainda sem nenhuma funcionalidade real.
02 - Utilizar dados mocados ou fallbacks para simular o comportamento da interface gráfica.
03 - É necessário que já nesta primeira fase a interface gráfica seja responsiva e intuitiva.
04 - É necessário que já nesta primeira fase a interface gráfica seja executável, ou seja, que ela possa ser testada pelo usuário ainda que apenas com dados mocados ou com fallbacks. Assim, deve estar devidamente configurada para ser executada com o comando `./gradlew run`.

## Funcionalidades básicas a serem implementadas na interface gráfica:
01 - Selecionar uma conversa existente ou criar uma nova conversa.
02 - Enviar mensagens para a IA e receber respostas.
03 - Visualizar o histórico de mensagens de uma conversa.
04 - Rolagem automática para a última mensagem enviada ou recebida.
05 - Estilização da interface para uma melhor experiência do usuário.

## CRIAR UMA GUI EM JAVA FX
01 - Utilizar JavaFX para a construção da interface gráfica.
02 - Utilizar Kotlin como linguagem de programação.
03 - Utilizar Gradle como sistema de build.
04 - Utilizar o DeepSeek IA como API de IA.
05 - Manter a estrutura do projeto organizada e modularizada.
06 - Garantir que a interface gráfica seja responsiva e intuitiva.
07 - Implementar testes unitários para a interface gráfica.
08 - Documentar o código e as funcionalidades da interface gráfica.
09 - Garantir que a interface gráfica seja compatível com diferentes sistemas operacionais.
10 - Implementar tratamento de erros e exceções na interface gráfica.
11 - Implementar logs para a interface gráfica.
12 - Implementar internacionalização (i18n) para a interface gráfica, permitindo suporte a múltiplos idiomas.

### Manter
01 - A estrutura do projeto organizada e modularizada.
02 - A compatibilidade com diferentes sistemas operacionais.
03 - SQLite para persistência de dados.
04 - Exposed como ORM (Object Relational Mapping).
05 - Kotlin como linguagem de programação.
06 - Gradle como sistema de build.
07 - DeepSeek IA como API de IA.
08 - Demais dependências e configurações já existentes no projeto.
09 - Compatibilidade para o CLI (Command Line Interface) já existente.
10 - Permitir diferentes builds do projeto, como CLI e GUI, sem afetar a funcionalidade principal do sistema.

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