## Fase 4: Sumarização de Conversa
    01 - Criação de uma nova funcionalidade para sumarização de conversa utilizando a API DeepSeek IA.

## Definições da nova funcionalidade
    01 - Implementar uma funcionalidade de sumarização de conversa, onde o usuário pode solicitar um resumo da conversa atual. ✅
    02 - O resumo deve ser exibido em uma nova janela ou área de texto, permitindo ao usuário visualizar rapidamente os pontos principais da conversa. ✅
    03 - A funcionalidade deve ser acessível através de um botão na interface do usuário, que aciona a chamada à API para gerar o resumo. ✅
    05 - O resumo deve ser formatado de forma clara e legível, utilizando Markdown para destacar pontos importantes, se necessário. ✅
    04 - A sumarização deve ser feita utilizando a API DeepSeek IA, que já está integrada ao projeto. ✅
    06 - A implementação deve garantir que a sumarização não afete o desempenho da aplicação, utilizando chamadas assíncronas para a API. ✅
    07 - A funcionalidade deve ser testada para garantir que o resumo gerado seja relevante e útil para o usuário, cobrindo os principais tópicos discutidos na conversa. ✅
    08 - A interface deve permitir que o usuário visualize o resumo sem perder o contexto da conversa, mantendo a usabilidade e a fluidez da interação. 
        08.1 - Porém, deve ser feita em uma aréa de texto separada, para não misturar com as mensagens do chat e que não seja visível por padrão, mas que possa ser aberta pelo usuário. ✅
    09 - A partir de uma sumarização, deverá ser possível gerar uma nova conversa, onde o usuário poderá continuar a conversa a partir do resumo gerado. ✅
    10 - A sumarização deve ser armazenada no histórico de mensagens, permitindo que o usuário acesse resumos anteriores a qualquer momento. 
        10.1 - Criar uma nova entidade de mensagem para o resumo, com um tipo específico que identifique que é um resumo de conversa. ✅
        10.2 - Deve ser criada a persistência do resumo no banco de dados, garantindo que os resumos sejam salvos e possam ser recuperados posteriormente. ✅
        10.3 - A mensagem de resumo deve ser diferenciada visualmente das mensagens normais, utilizando um estilo específico para destacar que é um resumo. ✅
        10.4 - A mensagem de resumo deve conter um link ou botão que permita ao usuário iniciar uma nova conversa a partir daquele resumo, facilitando a continuidade da interação. ✅
    11 - O aplicativo deverá ser capaz de calcular a quantidade de tokens utilizados na conversa, tanto para o usuário quanto para a IA, e exibir essa informação ao usuário. 
        11.1 - A contagem de tokens deve ser feita antes de enviar a mensagem para a API DeepSeek IA, garantindo que o usuário esteja ciente do consumo de tokens. ✅
        11.2 - A contagem de tokens deve ser exibida de forma clara na interface, permitindo que o usuário veja quantos tokens foram utilizados na conversa atual. ✅
    12 - Quando a quantidade máxima de tokens estiver próxima do limite, o aplicativo deverá alertar o usuário, informando que a conversa está se aproximando do limite de tokens e sugerindo a sumarização da conversa. 
        12.1 - O alerta deve ser exibido de forma discreta, mas visível, para não interromper a experiência do usuário. ⬜
        12.2 - O alerta deve incluir um botão que permita ao usuário gerar um resumo da conversa atual imediatamente. ⬜

## PASSOS DO PROCESSO

### PASSO 1
Neste primeiro passo vamos criar apenas os elementos de GUI.

Garantir que eles estejam funcionando.
- Não vamos alterar ou usar persistência
- Não vamos alterar ou usar chamadas http para a API

Garantir que os testes unitários estão rodando.

Garantir que o ./gradlew build está gerando a compilação com sucesso.

### 1.1 - Correção de implementaçao do PASSO 1
A implementação funcionou.

Porém a solução ficou com uma usabilidade ruim, pois a view com a sumarização ficou muito pequena.

Também percebo que não ficará boa em nenhuma outra posição na mesma janela que a janela atual.

Vamos alterar a implementação para que a view de sumarização aparece com uma janela flutuante que trava a janela principal. Não permite que o usuário interaja com a jenela principal enquanto a janela de sumarização está aberta.

Deve manter os botões atuais inclusive o botão de fechar.

### PASSO 2
Vamos implementar a funcionalidade que mostra a contagem de tokens para seja visível na label "tokenCountLabel".
A janela de contexto do DeepSeek tem limite de 128k: assim a label deve exibir a contagem no modelo "0/128"

Garantir que eles estejam funcionando.
- Não vamos alterar ou usar persistência
- Não vamos alterar ou usar chamadas http para a API

Garantir que os testes unitários estão rodando.

Garantir que o ./gradlew build está gerando a compilação com sucesso.


### PASSO 3
Vamos implementar a confirmação de "Sumarização".
Quando o usuário clicar no botão "summarizeButton", antes da abertura da Janela de Sumarização, deverá ser apresentada uma modal de confirmação. Só com a confirmação do usuário é que será aberta a Modal de Sumarização. Deverão ser criados os botões "Confirmar Sumarização" e "Cancelar" na modal de confirmação.
- Ao clicar em "Cancela" apenas fechar a modal de confirmação
- Ao clicar em "Confirmar Sumarização", exibir a Modal de Sumarização

Garantir que eles estejam funcionando.
- Não vamos alterar ou usar persistência
- Não vamos alterar ou usar chamadas http para a API

Garantir que os testes unitários estão rodando.

Garantir que o ./gradlew build está gerando a compilação com sucesso.


### PASSO 4
Vamos implementar a confirmação de "Nova Conversa" (botão "newConversationFromSummaryButton").
Quando o usuário clicar no botão "newConversationFromSummaryButton", antes da abertura da criação de uma nova conversa, deverá ser apresentada uma modal de confirmação. Só com a confirmação do usuário é que será inicada uma nova conversa. Deverão ser criados os botões "Confirmar" e "Cancelar" na modal de confirmação.
- Ao clicar em "Cancela" apenas fechar a modal de confirmação
- Ao clicar em "Confirmar", iniciar nova conversa. Por enquanto não fazer chamadas reais à API DeepSeek ou Persistência. Apenas garantir a funcionalidade dos componentes da GUI bem como garantir a navegação e experiência do usuário.

Garantir que eles estejam funcionando.
- Não vamos alterar ou usar persistência
- Não vamos alterar ou usar chamadas http para a API

Garantir que os testes unitários estão rodando.

Garantir que o ./gradlew build está gerando a compilação com sucesso.


### PASSO 5
Vamos agora implementar a persistência da "Conversation Summarization".
# Alterar a persistência existente para incluir os seguintes atributos:
    val tokensUsed: Int, // Total de tokens 
    val summaryMethod: String, // Tipo de sumarização (ex: "deepseek", "auto", etc.)
    val isActive: Boolean = true // Para permitir marcar resumos como inativos sem deletar
# As entidades de classe e migration já existem (Leia os arquivos antes de qualquer alteração).
1- ConversationSummarization
2- ConversationsSummarizations
3- ConversationSummarizationRepository
 * Os métodos da repository existente precisam ser mantidos, no máximo fazer os ajustes necessários para os novos atributos
4- ConversationSummarizationRepositoryTest
5- V5__Create_conversation_summarization_table.sql

# Quando o usuário escolher criar uma sumarização, utilizar a ConversationSummarizationRepository para persistir a sumarização.
# Neste momento ainda não chamar de fato a API DeepSeek para sumarizar, apenas persistir simulando que houve uma chamada para a API	


### PASSO 6
Remover os mocks e/ou fallbacks da Sumarização e implementar as chamadas reais 
Execute as chamadas à API DeepSeek para Sumarização 
Armazenar a resposta da API

Garantir que os testes unitários atuais estão rodando.
Criar novos testes unitários necessários para este sexto passo
Excluir testes unitários que deixem de fazer sentido diante das mudanças deste passo.

Não alterar outras funcionalidades

Garantir que o ./gradlew build está gerando a compilação com sucesso.


### 6.1 - Correção de implementaçao do PASSO 6
A implementação do sexto passo funcionou muito bem.

Porém alguns problemas foram identificados.
1) Comportamento da Janela/Tela de Sumarização: quando a tela é aberta, as operações HTTP e de Acesso ao Banco de Dados estão fazendo a tela não ser exibida. A tela só é exibida para o usuário quando as opereções HTTP e SQL são concluídas.
O comportamento esperado é que as opeções HTTP e SQL sejam assíncronas e que a tela seja exibida mesmo antes da conclusão dessa operações.
O Comportamento deve ser:
1.1 - Abrir tela  após o usuário confirmar que quer fazer uma sumarização;1
1.2 - Exibir um elemento de UI que mostre um Feedback do progresso das operações SQL e HTTP para o usuário;1
1.3 - Efetuar as operações SQL e HTTP de forma assíncrona sem travar a UI;
1.4 - Mostrar os resultados tão logo a operações assíncronas tenha sido concluídas.

Garantir que os testes unitários atuais estão rodando.

Não alterar outras funcionalidades

Garantir que o ./gradlew build está gerando a compilação com sucesso.

### 6.2 - Correção de implementaçao do PASSO 6
A implementação do sexto passo funcionou muito bem.

Porém alguns problemas foram identificados.
2) O contéudo da sumarização não está sendo totalmente exibido, embora esja sendo armazenado no banco de dados corretamente. O problema é que os subitens de "### 📊 Estatísticas", "### � Tópicos Principais", "### 💬 Resumo do Conteúdo" e "### ✨ Pontos-Chave" não estão visíveis. O espeço corresponde há ele está sendo ocupado. Então, é provavel que seja uma questão de COR. O Fundo da área de exibição da sumarização é BRANCO, então é possível que os subitens estejam invisíveis por estarem na mesma cor BRANCA. Isso é apenas uma suposição. É preciso analizar.

Aqui está um exemplo de texto real:
```
## Resumo da Conversa

### 📊 Estatísticas
- Total de mensagens: 4  
- Mensagens do usuário: 2  
- Respostas da IA: 2  

### � Tópicos Principais
1. Biografia e importância de Masaoka Shiki na poesia japonesa.  
2. Principais contribuições de Shiki para o haiku, tanka e literatura.  
3. Exemplos icônicos de haikus de Shiki e sua técnica do *shasei*.  

### 💬 Resumo do Conteúdo  
A conversa iniciou com o usuário perguntando sobre a identidade do poeta japonês Shiki. A IA respondeu detalhando sua vida, obras e papel central na reforma do haiku e tanka, destacando seu conceito de *shasei* ("esboço da vida"). Em seguida, o usuário solicitou um exemplo de seu haiku mais famoso, e a IA apresentou dois poemas emblemáticos, analisando sua estética e contexto. A discussão evoluiu de uma visão geral para exemplos concretos da poesia de Shiki, enfatizando sua habilidade em capturar a beleza efêmera.  

### ✨ Pontos-Chave  
- Shiki foi um reformador dos gêneros poéticos tradicionais japoneses, promovendo uma abordagem realista (*shasei*).  
- Seu haiku sobre o orvalho e a folha de lírio é icônico por sua simplicidade e reflexão sobre a transitoriedade.  
- Mesmo enfrentando doenças, Shiki deixou um legado duradouro, influenciando a poesia moderna e formando discípulos importantes.  
- Sua obra combina observação do cotidiano com profundidade filosófica, marcada pela apreciação do efêmero (*mono no aware*).
```

### PASSO 7
Neste passo será criada uma nova conversa a partir do botão "Nova Conversa" (newConversationFromSummaryButton) da Janela de Sumarização.
Como deverá funcionar:
1) Após usuário confirmar que quer criar uma nova conversa (confirmButton), criar uma nova Conversation:
1.1) O Título deverá ser o mesmo da conversa original;
1.2) O atributo "conversationSummarizationId" deverá ser preenchido com a chave (PK) da sumarização que está na tela;
2) Criar a primeira mensagem dessa nova conversa com o exato conteúdo da sumarização;
2.1) A Role da primeira mensagem deverá ser "ASSISTANT"
3) Após criados os registros no banco de dados, fechar a janela de Sumarização e retornar à Janela principal já com os registros de Conversation atualizados e com a nova Conversation selecionada com exibição da primeira mensagem que é a sumarização. No campo da Sumarização, ao final do texto da sumarização, incluir um link que permita abrir a Conversation que deu origem a atual Conversation.

Garantir que os testes unitários atuais estão rodando.
Criar novos testes unitários necessários para este sexto passo
Excluir testes unitários que deixem de fazer sentido diante das mudanças deste passo.

Não alterar outras funcionalidades

Garantir que o ./gradlew build está gerando a compilação com sucesso.


### PASSO 8
A implementação do sétimo passo funcionou muito bem.
Leia os arquivos:
00-instrucoes-gerais-interface-jfx.md
04-instrucoes-fase-04-jfx.md

database-schema.md

Neste passo será criada uma nova tela: A Tela de Sumarizações
1) Semelhante a janela de Conversas atual, a janela de Sumarizações deverá exibir a listagem de sumarizações existentes;
2) Ao clicar em um item, o conteúdo da sumarização será exibido em uma área lateral direita;
3) Nesta tela deverá existir um botão com a opção de simplesmente voltar para a janela de mensagens;
4) Na tela de mensagens deverá ser incluída uma nova opção que permita ao usuário navegar para a janela de sumarizações;
5) As telas devem ser independentes para garantir desacoplamento entre as funcionalidades:
5.1) Controllers diferentes para cada tela;
5.2) FXMLs diferentes;
5.2) Segregação de Responsabilidades;
5.3) As manutenções em uma funcionalidade não podem impactar na outra;
5.4) Empregue principios de Orientação a Objeto e especialmente Design Patter GoF e Princípios S.O.L.I.D

Garantir que os testes unitários atuais estão rodando.
Criar novos testes unitários necessários para este sexto passo
Excluir testes unitários que deixem de fazer sentido diante das mudanças deste passo.

Não alterar outras funcionalidades

Garantir que o ./gradlew build está gerando a compilação com sucesso.

### PASSO 9
- DEFINIÇÕES
    - Neste passo será alterado o modelo de implementação das telas:
    - Atualmente, quando a tela de Sumarizações é exibida, uma segunda janela se sobrepõe à janela principal.
    - É necessário que a estrutura suporte várias telas com Controllers e FXMLs independentes, porém dentro da mesma Janela. 
        1) Controllers diferentes para cada tela;
        2) FXMLs diferentes;
        3) Segregação de Responsabilidades;
        4) As manutenções em uma funcionalidade não podem impactar na outra;
        5) Empregue principios de Orientação a Objeto e especialmente Design Patter GoF e Princípios S.O.L.I.D
        6) Uma única Janela para o sistema com suporte a muitas telas.
        7) Testes uniários específicos por Tela

- RESTRIÇÕES
    1) Não alterar NENHUMA funcionalidade existente, APENAS refatorar a SOLUÇÃO TÉCNICA de estrutura de telas.
    2) Não alterar chamadas a APIs;
    3) Não aletrar camada de Persistência;
    4) Não alterar nenhuma lógica funcional

- CRITÉRIOS DE ACEITE
    1) Garantir que os testes unitários atuais estão rodando: ./gradlew test 
    2) Criar novos testes unitários necessários para este sexto passo
    3) Excluir testes unitários que deixem de fazer sentido diante das mudanças deste passo.
    4) Não alterar outras funcionalidades
    5) Garantir que o ./gradlew build está gerando a compilação com sucesso.

### PASSO 10
Neste passo deverá ser incluído um link na forma de uma Mensagem na Conversa original que foi sumarizada.
Como deverá funcionar:
1) Quando uma Sumarização for criar, uma mensagem deverá ser adicionada à conversa contendo um link para a Sumarização;
1.1) A Role dessa mensagem será "SYSTEM"
1.2) Criar um CSS específico para mensagens da Role "SYSTEM" para que sejam diferenciadas das demais mensagens;
1.3) A mensagem de "SYSTEM" deverá ter um ícone diferente das demais.
2) Quando o usuário clicar nesse link, ele abrir a tela de sumarizações com a sumarização específica selecionada;
3) A mensagem "SYSTEM" não será enviada para a API do DeepSeek como aconteceria se fosse uma mensagem de "USER"


Garantir que os testes unitários atuais estão rodando.
Criar novos testes unitários necessários para este sexto passo
Excluir testes unitários que deixem de fazer sentido diante das mudanças deste passo.

Não alterar outras funcionalidades

Garantir que o ./gradlew build está gerando a compilação com sucesso.



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