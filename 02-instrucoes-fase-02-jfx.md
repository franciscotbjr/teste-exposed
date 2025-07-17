## Esta é a segunda fase da implementação: melhorias da interface gráfica
01 - As mensagens na área de chat devem ser exibidas com estilo, diferenciando mensagens do usuário e da IA.
02 - As mensagens do usuário na área de chat devem ser exibidas alinhadas à direita, enquanto as mensagens da IA devem ser alinhadas à esquerda.
03 - Incluir ícones de usuário e IA ao lado das respectivas mensagens.

## Detalhes da Implementação
1) Para identificar que uma mensagem é do USER ou do ASSISTANT, observar o valor do atributo "val role: Role," da classe "org.hexasilith.model.Message"
2) Os valores possíveis estão especificados no arquivo "V1__Create_roles_table.sql" de "db.migration"
3) Aplicar diferentes estilos CSS para as mensagens do usuário e da IA, utilizando classes CSS específicas.
4) As mensagens do usuário devem ser alinhadas à direita e as mensagens da IA alinhadas à esquerda.
5) As mensagens devem ser exibidas em caixas de texto estilizadas, com bordas e fundo diferenciados.

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