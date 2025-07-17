# ALTERAR O PROJETO PARA QUE O USUÁRIO TENHA ACESSO A UMA INTERFACE GRÁFICA
O Objetivo é criar um tela inicial que tem o objetivo de se comportar com um Client de APIs de IA com o Client do DeepSeek IA.

As alterações não devem alterar ainda nenhuma funcionalidade já existente do projeto, apenas adicionar uma interface gráfica que permita ao usuário interagir com o sistema de forma mais amigável.

## Funcionalidades básicas a serem implementadas na interface gráfica:
01 - Selecionar uma conversa existente ou criar uma nova conversa.
02 - Enviar mensagens para a IA e receber respostas.
03 - Visualizar o histórico de mensagens de uma conversa.
04 - Rolagem automática para a última mensagem enviada ou recebida.
05 - A área de exibição de mensagens deve ter scroll para permitir a visualização de mensagens antigas.
06 - A área de exibição de mensagens deve ter suporte a Markdown para formatação de texto.
07 - A área de exibição de mensagens deve ter diferenciação visual entre mensagens do usuário e da IA.
08 - Estilização da interface para uma melhor experiência do usuário.

## Funcionalidades adicionais a serem implementadas na interface gráfica:
01 - Implementar uma tela de boas-vindas com informações sobre o projeto e instruções de uso.
02 - Implementar um botão de ajuda que exiba informações sobre como usar a interface gráfica.
03 - Implementar um botão de feedback que permita ao usuário enviar sugestões ou relatar problemas.
04 - Implementar um botão de sair que encerre a aplicação de forma adequada.
05 - Implementar um botão de configurações que permita ao usuário ajustar preferências da interface gráfica.
06 - Implementar um botão de histórico que permita ao usuário visualizar o histórico de conversas.
07 - Implementar um botão de exportar que permita ao usuário exportar o histórico de conversas para os formatos:
07.1 - ODT (Libre Office)
07.2 - PDF (Portable Document Format)
07.3 - TXT (Plain Text)
07.4 - JSON (JavaScript Object Notation): Neste caso deve exportar o histórico de conversas em um formato estruturado que possa ser facilmente lido por outras aplicações.
07.5 - HTML (HyperText Markup Language)
08 - Implementar um botão de importar que permita ao usuário importar conversas de um arquivo a partir dos formatos:
08.1 - JSON (JavaScript Object Notation): Neste caso deve importar o histórico de conversas de um arquivo JSON estruturado.
09 - Implementar um botão de limpar que permita ao usuário limpar o histórico de conversas.
10 - Implementar um botão de salvar que permita ao usuário salvar o estado atual da conversa.  
11 - Implementar um botão de carregar que permita ao usuário carregar uma conversa salva anteriormente.
12 - Implementar um botão de compartilhar que permita ao usuário compartilhar a conversa atual em redes sociais ou aplicativos de mensagens.
13 - Implementar um botão de pesquisa que permita ao usuário buscar mensagens específicas no histórico de conversas.
14 - Implementar um botão de favoritos que permita ao usuário marcar mensagens importantes para fácil acesso posterior.
15 - Implementar um botão de notificação que permita ao usuário receber notificações sobre novas mensagens ou atualizações.
16 - Implementar um botão de tema que permita ao usuário escolher entre diferentes temas para a interface gráfica.
17 - Implementar um botão de atalho que permita ao usuário acessar rapidamente funcionalidades comuns.

## Funcionalidades avançadas a serem implementadas na interface gráfica:
01 - Implementar suporte a chat interativo com a IA e mais de um Usuário em uma conversa P2P (Peer-to-Peer).
02 - Implementar sumarização automática de conversas, permitindo que o usuário visualize um resumo das mensagens trocadas.
03 - Implementar a continuidade de conversas que tenham atingido o limite de tokens (janela de contexto) da IA, usando a sumarização automática para manter o contexto.

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