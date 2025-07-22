## Esta é a segunda fase da implementação: melhorias da interface gráfica
    01 - As mensagens na área de chat devem ser exibidas com estilo, diferenciando mensagens do usuário e da IA.
    02 - As mensagens do usuário na área de chat devem ser exibidas alinhadas à direita, enquanto as mensagens da IA devem ser alinhadas à esquerda.
    03 - Incluir ícones de usuário e IA ao lado das respectivas mensagens.

## Detalhes da Implementação
    01 - Para identificar que uma mensagem é do USER ou do ASSISTANT, observar o valor do atributo "val role: Role," da classe "org.hexasilith.model.Message"
    02 - Os valores possíveis estão especificados no arquivo "V1__Create_roles_table.sql" de "db.migration"
    03 - Aplicar diferentes estilos CSS para as mensagens do usuário e da IA, utilizando classes CSS específicas.
    04 - As mensagens do usuário devem ser alinhadas à direita e as mensagens da IA alinhadas à esquerda.
    05 - As mensagens devem ser exibidas em caixas de texto estilizadas, com bordas e fundo diferenciados.

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