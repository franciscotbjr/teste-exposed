## Fase 4: Sumarização de Conversa
    01 - Criação de uma nova funcionalidade para sumarização de conversa utilizando a API DeepSeek IA.

## Definições da nova funcionalidade
    01 - Implementar uma funcionalidade de sumarização de conversa, onde o usuário pode solicitar um resumo da conversa atual. ✅
    02 - O resumo deve ser exibido em uma nova janela ou área de texto, permitindo ao usuário visualizar rapidamente os pontos principais da conversa. ✅
    03 - A funcionalidade deve ser acessível através de um botão na interface do usuário, que aciona a chamada à API para gerar o resumo. ✅
    05 - O resumo deve ser formatado de forma clara e legível, utilizando Markdown para destacar pontos importantes, se necessário. ✅
    04 - A sumarização deve ser feita utilizando a API DeepSeek IA, que já está integrada ao projeto. ⬜
    06 - A implementação deve garantir que a sumarização não afete o desempenho da aplicação, utilizando chamadas assíncronas para a API. ⬜
    07 - A funcionalidade deve ser testada para garantir que o resumo gerado seja relevante e útil para o usuário, cobrindo os principais tópicos discutidos na conversa. ⬜
    08 - A interface deve permitir que o usuário visualize o resumo sem perder o contexto da conversa, mantendo a usabilidade e a fluidez da interação. 
        08.1 - Porém, deve ser feita em uma aréa de texto separada, para não misturar com as mensagens do chat e que não seja visível por padrão, mas que possa ser aberta pelo usuário. ⬜
    09 - A partir de uma sumarização, deverá ser possível gerar uma nova conversa, onde o usuário poderá continuar a conversa a partir do resumo gerado. ⬜
    10 - A sumarização deve ser armazenada no histórico de mensagens, permitindo que o usuário acesse resumos anteriores a qualquer momento. 
        10.1 - Criar uma nova entidade de mensagem para o resumo, com um tipo específico que identifique que é um resumo de conversa. ⬜
        10.2 - Deve ser criada a persistência do resumo no banco de dados, garantindo que os resumos sejam salvos e possam ser recuperados posteriormente. ⬜
        10.3 - A mensagem de resumo deve ser diferenciada visualmente das mensagens normais, utilizando um estilo específico para destacar que é um resumo. ⬜
        10.4 - A mensagem de resumo deve conter um link ou botão que permita ao usuário iniciar uma nova conversa a partir daquele resumo, facilitando a continuidade da interação. ⬜
    11 - O aplicativo deverá ser capaz de calcular a quantidade de tokens utilizados na conversa, tanto para o usuário quanto para a IA, e exibir essa informação ao usuário. 
        11.1 - A contagem de tokens deve ser feita antes de enviar a mensagem para a API DeepSeek IA, garantindo que o usuário esteja ciente do consumo de tokens. ⬜
        11.2 - A contagem de tokens deve ser exibida de forma clara na interface, permitindo que o usuário veja quantos tokens foram utilizados na conversa atual. ⬜
    12 - Quando a quantidade máxima de tokens estiver próxima do limite, o aplicativo deverá alertar o usuário, informando que a conversa está se aproximando do limite de tokens e sugerindo a sumarização da conversa. 
        12.1 - O alerta deve ser exibido de forma discreta, mas visível, para não interromper a experiência do usuário. ⬜
        12.2 - O alerta deve incluir um botão que permita ao usuário gerar um resumo da conversa atual imediatamente. ⬜

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

    - Kotlin Coding Conventions - Guia oficial de convenções do Kotlin
    - JavaFX Documentation - Documentação completa do JavaFX

### Recursos Especializados:

    - PragmaticCoding - JavaFX Frameworks - Análise detalhada dos padrões MVC, MVP e MVVM em JavaFX
    - Griffon Framework MVC Tutorial - Implementação prática dos padrões MVC em JavaFX

### Livros Recomendados:

    - "Frontend Development with JavaFX and Kotlin" por Peter Späth - Este livro introduz JavaFX como tecnologia frontend e utiliza Kotlin em vez de Java para codificar artefatos do programa, aumentando a expressividade e manutenibilidade do código Frontend Development with JavaFX and Kotlin: Build State-of-the-Art Kotlin GUI Applications | SpringerLink