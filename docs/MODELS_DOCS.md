# Models - Documentação

Parte de dados da aplicação, responsável por definir a estrutura dos objetos e sua "forma" no banco de dados. Contém as classes e enums que representam a estrutura da aplicação.

## Como funcionam

Cada model é uma classe marcada com `@Entity` que mapeia para uma tabela no banco usando JPA. As anotações definem relacionamentos, validações e estrutura da persistência.

### Estrutura básica

```java
@Entity
@Table(name = "tabela_exemplo")
public class ExemploModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // Outros campos e métodos
}
```

## Como funciona a estrutura

- Herança: Usuario como classe base para Voluntario e Ong
- Relacionamentos: Mapeamento automático de listas e coleções
- Enums: Tipos controlados para status e categorias

## Models da aplicação

### Usuario
Classe abstrata base para Voluntario e Ong

Campos principais:
- `id` - Identificador único UUID
- `email` - Email único do usuário
- `senha` - Senha criptografada
- `nomeCompleto` - Nome completo
- `fotoDePerfil` - URL da foto
- `nota` - Avaliação média
- `maisSobre` - Descrição pessoal
- `preferenciaTema` - Tema visual preferido
- `criadoEm` - Data de criação
- `ativo` - Status da conta

### Voluntario
Herda de Usuario, representa pessoas físicas

Campos específicos:
- `areasDeInteresse` - Lista de áreas de interesse
- `historicoDeEventos` - Eventos que participou
- `ongsSeguidas` - ONGs que segue
- `eventosFavoritos` - Eventos marcados como favoritos

Funcionalidades:
- Gerenciar áreas de interesse
- Seguir ONGs
- Histórico de participação
- Sistema de favoritos

### Ong
Herda de Usuario, representa organizações

Campos específicos:
- `cnpj` - CNPJ único da organização
- `endereco` - Endereço físico
- `telefone` - Telefone de contato
- `areasDeAtuacao` - Áreas onde atua
- `eventosOrganizados` - Eventos criados
- `seguidores` - Voluntários que seguem
- `verificada` - Status de verificação

Funcionalidades:
- Criar e gerenciar eventos
- Sistema de verificação
- Acompanhar seguidores

### Evento
Representa eventos de voluntariado

Campos principais:
- `titulo` - Nome do evento
- `descricao` - Descrição detalhada
- `dataHoraInicio` - Data e hora de início
- `dataHoraFim` - Data e hora de fim
- `ongResponsavelId` - ONG organizadora
- `local` - Local do evento
- `vagasDisponiveis` - Total de vagas
- `vagasOcupadas` - Vagas preenchidas
- `status` - Status atual do evento
- `areasEnvolvidas` - Áreas de atuação
- `voluntariosInscritos` - Lista de inscritos

Funcionalidades:
- Controle de vagas
- Gerenciamento de inscrições
- Alteração de status
- Sistema de imagens

### Post
Representa publicações na plataforma

Campos principais:
- `autorId` - ID do autor
- `tipoAutor` - VOLUNTARIO ou ONG
- `conteudo` - Texto da publicação
- `imagemUrl` - Imagem anexada
- `criadoEm` - Data de criação
- `curtidas` - Contador de curtidas
- `comentarios` - Lista de comentários

Funcionalidades:
- Sistema de curtidas
- Comentários aninhados
- Anexar imagens
- Controle de visibilidade

### Notificacao
Representa notificações do sistema

Campos principais:
- `tipo` - Tipo da notificação
- `mensagem` - Conteúdo da mensagem
- `usuarioDestinoId` - Destinatário
- `criadaEm` - Data de criação
- `lida` - Status de leitura
- `leituraEm` - Data da leitura

Funcionalidades:
- Múltiplos tipos de notificação
- Controle de leitura
- Histórico temporal

### Avaliacao
Representa avaliações de eventos

Campos principais:
- `eventoId` - Evento avaliado
- `voluntarioId` - Voluntário avaliador
- `nota` - Nota de 1 a 5
- `comentario` - Comentário opcional
- `criadaEm` - Data da avaliação

### Comentario
Representa comentários em posts

Campos principais:
- `postId` - Post comentado
- `autorId` - Autor do comentário
- `tipoAutor` - VOLUNTARIO ou ONG
- `conteudo` - Texto do comentário
- `criadoEm` - Data de criação

## Enums utilizados

### AreaInteresse
Define as áreas de atuação disponíveis:
- EDUCACAO, MEIO_AMBIENTE, SAUDE
- ASSISTENCIA_SOCIAL, CULTURA, ESPORTE
- DIREITOS_HUMANOS, TECNOLOGIA, ANIMAIS
- IDOSOS, CRIANCAS

### StatusEvento
Define os possíveis status de eventos:
- RASCUNHO, PUBLICADO, INSCRICOES_ABERTAS
- INSCRICOES_FECHADAS
- FINALIZADO, CANCELADO

### TipoNotificacao
Define os tipos de notificações:
- EVENTO_CRIADO, INSCRICAO_CONFIRMADA
- EVENTO_CANCELADO, EVENTO_ALTERADO
- NOVA_MENSAGEM, NOVO_SEGUIDOR

### PreferenciaTema
Define temas visuais:
- CLARO, ESCURO, AUTOMATICO

## Relacionamentos

### Um para vários
- Usuario -> Notificacoes
- Post -> Comentarios

### Coleção de elementos
- Voluntario -> AreasDeInteresse
- Ong -> AreasDeAtuacao
- Evento -> VoluntariosInscritos

### Herança
- Usuario -> Voluntario, Ong

