# Controllers - Documentação

Parte API da apliacação, responsável por receber requisições HHTP do front. Contém toda a lógica de serviço e a conexão + persistência no banco de dados.

## Como funcionam

Cada controller é uma classe marcada com `@RestController` que mapeia URLs para métodos específicos usando anotações como `@GetMapping`, `@PostMapping`, `@PutMapping` e `@DeleteMapping`.

### Estrutura básica

```java
@RestController
@RequestMapping("/api/recurso")
public class RecursoController {
    // Métodos que respondem a requisições HTTP
}
```

## Como funciona a estrutura

- Divisão de responsabilidade: Controllers só lidam com HTTP, delegando lógica para services
- Padrão REST: URLs padronizadas (GET para buscar, POST para criar, PUT para atualizar, DELETE para remover)
- Facilidade de teste: Endpoints bem definidos facilitam testes automatizados

## Controllers da aplicação

### VoluntarioController
Endpoint: `/api/voluntarios`

Funcionalidades:
- `POST /` - Criar voluntário
- `GET /{id}` - Buscar voluntário específico
- `GET /` - Listar todos os voluntários
- `PUT /{id}` - Atualizar dados do voluntário
- `DELETE /{id}` - Remover voluntário

### EventoController
Endpoint: `/api/eventos`

Funcionalidades:
- `POST /` - Criar evento
- `GET /{id}` - Buscar evento específico
- `GET /` - Listar eventos
- `PUT /{id}` - Atualizar evento
- `DELETE /{id}` - Cancelar evento
- `POST /{id}/abrir-inscricoes` - Abrir inscrições
- `POST /{id}/fechar-inscricoes` - Fechar inscrições
- `POST /{id}/inscrever/{voluntarioId}` - Inscrever voluntário

### NotificacaoController
Endpoint: `/api/notificacoes`

Funcionalidades:
- `GET /usuario/{usuarioId}` - Listar notificações do usuário
- `GET /usuario/{usuarioId}/nao-lidas` - Listar não lidas
- `GET /usuario/{usuarioId}/contador-nao-lidas` - Contar não lidas
- `POST /{notificacaoId}/marcar-lida/{usuarioId}` - Marcar como lida
- `POST /marcar-todas-lidas/{usuarioId}` - Marcar todas como lidas

### OngController
Endpoint: `/api/ongs`

Funcionalidades:
- `POST /` - Criar ONG
- `GET /{id}` - Buscar ONG específica
- `GET /` - Listar ONGs
- `PUT /{id}` - Atualizar dados da ONG
- `DELETE /{id}` - Remover ONG

### PostController
Endpoint: `/api/posts`

Funcionalidades:
- `POST /` - Criar post
- `GET /{id}` - Buscar post específico
- `GET /` - Listar posts
- `PUT /{id}` - Atualizar post
- `DELETE /{id}` - Remover post

### AvaliacaoController
Endpoint: `/api/avaliacoes`

Funcionalidades:
- `POST /` - Criar avaliação
- `GET /evento/{eventoId}` - Listar avaliações de um evento
- `GET /media/evento/{eventoId}` - Calcular média de avaliações

### ComentarioController
Endpoint: `/api/comentarios`

Funcionalidades:
- `POST /` - Criar comentário
- `GET /post/{postId}` - Listar comentários de um post
- `DELETE /{id}` - Remover comentário

## Tipos de retorno utilizados

### Tratamento de erros
- `ResponseEntity.ok()` - Sucesso (200)
- `ResponseEntity.badRequest()` - Erro de validação (400)
- `ResponseEntity.notFound()` - Recurso não encontrado (404)
- `ResponseEntity.noContent()` - Operação sem retorno (204)

### Validação de dados
- Try-catch para capturar erros
- Verificação de existência antes de operações


