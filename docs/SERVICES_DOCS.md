# Services - Documentação

Parte de lógica da aplicação, responsável por implementar regras e processamentos complexos. É a camada intermediária entre controllers e models, contém operações que envolvem múltiplas entidades.

## Como funcionam

Cada service é uma classe marcada com `@Service` que implementa regras de negócio específicas. São injetados nos controllers via `@Autowired` e encapsulam operações que vão além de simples CRUD.

### Estrutura básica

```java
@Service
public class ExemploService {
    // Implementação de regras de negócio
    // Validações complexas
    // Operações que envolvem múltiplas entidades
}
```

## Como funciona a estrutura

- Encapsulamento: Lógicas complexas ficam isoladas dos controllers
- Reutilização: Métodos podem ser chamados por diferentes controllers
- Validação: Centraliza regras de negócio e validações

## Services da aplicação

### NotificacaoService
Gerencia todo o sistema de notificações da plataforma

Funcionalidades principais:
- `criarNotificacaoAvaliacao()` - Notifica quando usuário recebe avaliação
- `criarNotificacoesNovoEvento()` - Notifica seguidores sobre novos eventos
- `criarNotificacaoInscricaoEvento()` - Notifica ONG sobre nova inscrição
- `criarNotificacoesCancelamentoEvento()` - Notifica inscritos sobre cancelamento
- `criarNotificacaoNovoSeguidor()` - Notifica ONG sobre novo seguidor
- `buscarTodasNotificacoes()` - Lista notificações com paginação
- `buscarNotificacoesNaoLidas()` - Filtra apenas não lidas
- `contarNotificacoesNaoLidas()` - Conta notificações pendentes
- `marcarComoLida()` - Marca notificação específica como lida
- `marcarTodasComoLidas()` - Marca todas como lidas

Validações implementadas:
- Verificação de parâmetros nulos
- Validação de existência de usuários
- Controle de duplicatas
- Limite de notificações por usuário

Por que é necessário:
- Centraliza toda lógica de notificações
- Evita código duplicado nos controllers
- Garante consistência nas mensagens
- Facilita manutenção e mudanças

### AvaliacaoService
Gerencia sistema de avaliações entre usuários

Funcionalidades principais:
- `voluntarioAvaliarOng()` - Voluntário avalia ONG após evento
- `ongAvaliarVoluntario()` - ONG avalia voluntário após evento
- `removerAvaliacao()` - Remove avaliação existente
- `listarAvaliacoesRecebidas()` - Lista avaliações que usuário recebeu
- `listarAvaliacoesFeitas()` - Lista avaliações que usuário fez
- `calcularNotaMedia()` - Calcula média das avaliações recebidas

Validações implementadas:
- Nota entre 1.0 e 5.0
- Verificação de participação no evento
- Validação de organização do evento
- Prevenção de avaliações duplicadas
- Controle de autorização para remoção

Regras de negócio:
- Apenas participantes podem avaliar
- Uma avaliação por usuário por evento
- Apenas organizador pode avaliar voluntários
- Apenas voluntários podem avaliar organizador
- Avaliações são soft-delete (desativadas)

Por que é necessário:
- Implementa regras complexas de avaliação
- Garante integridade dos dados
- Centraliza cálculos de médias
- Controla permissões de acesso

## Padrões utilizados

### Validação em camadas
```java
private void validarDados(Float nota) {
    if (nota == null || nota < 1.0f || nota > 5.0f) {
        throw new IllegalArgumentException("Nota deve estar entre 1.0 e 5.0");
    }
}
```

### Operações em lote
```java
public List<Notificacao> criarNotificacoesNovoEvento(
    UUID eventoId, UUID ongId, List<UUID> seguidoresIds) {
    // Processa lista de seguidores
    // Cria múltiplas notificações
    // Retorna lista de resultados
}
```

### Tratamento de erros
- `IllegalArgumentException` para dados inválidos
- `IllegalStateException` para estados inconsistentes
- Validação de nulos antes de processamento
- Mensagens descritivas para debugging


### Segregação de responsabilidades
- NotificacaoService: foca apenas em notificações
- AvaliacaoService: foca apenas em avaliações
- Cada service tem responsabilidade única e bem definida

## Integração com Controllers

Services são utilizados dentro dos controllers com `@Autowired`:

```java
@RestController
public class NotificacaoController {
    @Autowired
    private NotificacaoService notificacaoService;
    
    // Controller usa methods do service
}
```

Esta separação permite:
- Controllers focam apenas em HTTP
- Services implementam lógica de negócio
- Fácil testabilidade de cada camada
- Reutilização de código entre controllers


