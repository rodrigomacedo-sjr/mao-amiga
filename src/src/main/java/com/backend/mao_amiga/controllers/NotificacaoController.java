package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Notificacao;
import com.backend.mao_amiga.services.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Notificacao>> listarNotificacoesDoUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam(defaultValue = "20") int limite) {
        
        List<Notificacao> notificacoes = notificacaoService.buscarTodasNotificacoes(usuarioId, limite);
        return ResponseEntity.ok(notificacoes);
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public ResponseEntity<List<Notificacao>> listarNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        List<Notificacao> notificacoesNaoLidas = notificacaoService.buscarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(notificacoesNaoLidas);
    }

    @GetMapping("/usuario/{usuarioId}/contador-nao-lidas")
    public ResponseEntity<Map<String, Long>> contarNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        long quantidade = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        return ResponseEntity.ok(Map.of("naoLidas", quantidade));
    }

    @PostMapping("/{notificacaoId}/marcar-lida/{usuarioId}")
    public ResponseEntity<Map<String, Object>> marcarComoLida(
            @PathVariable UUID notificacaoId,
            @PathVariable UUID usuarioId) {
        
        boolean sucesso = notificacaoService.marcarComoLida(notificacaoId, usuarioId);
        
        return ResponseEntity.ok(Map.of(
            "sucesso", sucesso,
            "mensagem", sucesso ? "Notificação marcada como lida" : "Notificação não encontrada"
        ));
    }

    @PostMapping("/usuario/{usuarioId}/marcar-todas-lidas")
    public ResponseEntity<Map<String, Object>> marcarTodasComoLidas(@PathVariable UUID usuarioId) {
        int quantidade = notificacaoService.marcarTodasComoLidas(usuarioId);
        
        return ResponseEntity.ok(Map.of(
            "quantidade", quantidade,
            "mensagem", String.format("%d notificações marcadas como lidas", quantidade)
        ));
    }

    @PostMapping("/avaliacao")
    public ResponseEntity<Notificacao> criarNotificacaoAvaliacao(
            @RequestBody Map<String, Object> dados) {
        
        try {
            UUID usuarioDestinoId = UUID.fromString((String) dados.get("usuarioDestinoId"));
            String tipoUsuarioDestino = (String) dados.get("tipoUsuarioDestino");
            UUID usuarioOrigemId = UUID.fromString((String) dados.get("usuarioOrigemId"));
            
            Notificacao notificacao = notificacaoService.criarNotificacaoAvaliacao(
                usuarioDestinoId, tipoUsuarioDestino, usuarioOrigemId
            );
            
            return ResponseEntity.ok(notificacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/novo-seguidor")
    public ResponseEntity<Notificacao> criarNotificacaoNovoSeguidor(
            @RequestBody Map<String, Object> dados) {
        
        try {
            UUID ongId = UUID.fromString((String) dados.get("ongId"));
            UUID voluntarioId = UUID.fromString((String) dados.get("voluntarioId"));
            
            Notificacao notificacao = notificacaoService.criarNotificacaoNovoSeguidor(ongId, voluntarioId);
            
            return ResponseEntity.ok(notificacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/novo-evento")
    public ResponseEntity<List<Notificacao>> criarNotificacoesNovoEvento(
            @RequestBody Map<String, Object> dados) {
        
        try {
            UUID eventoId = UUID.fromString((String) dados.get("eventoId"));
            UUID ongId = UUID.fromString((String) dados.get("ongId"));
            
            @SuppressWarnings("unchecked")
            List<String> seguidoresStr = (List<String>) dados.get("seguidores");
            List<UUID> seguidores = seguidoresStr.stream()
                    .map(UUID::fromString)
                    .toList();
            
            List<Notificacao> notificacoes = notificacaoService.criarNotificacoesNovoEvento(
                eventoId, ongId, seguidores
            );
            
            return ResponseEntity.ok(notificacoes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/inscricao-evento")
    public ResponseEntity<Notificacao> criarNotificacaoInscricaoEvento(
            @RequestBody Map<String, Object> dados) {
        
        try {
            UUID voluntarioId = UUID.fromString((String) dados.get("voluntarioId"));
            UUID ongId = UUID.fromString((String) dados.get("ongId"));
            UUID eventoId = UUID.fromString((String) dados.get("eventoId"));
            
            Notificacao notificacao = notificacaoService.criarNotificacaoInscricaoEvento(
                voluntarioId, ongId, eventoId
            );
            
            return ResponseEntity.ok(notificacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notificacao> buscarNotificacao(@PathVariable UUID id) {
        try {
            // Como o service atual não tem método específico, vamos simular
            List<Notificacao> todasNotificacoes = notificacaoService.buscarTodasNotificacoes(UUID.randomUUID(), 1000);
            Notificacao notificacao = todasNotificacoes.stream()
                    .filter(n -> n.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            
            return notificacao != null ? ResponseEntity.ok(notificacao) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarNotificacao(@PathVariable UUID id) {
        // Em uma implementação real, você implementaria este método no service
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticasGerais() {
        Map<String, Object> estatisticas = notificacaoService.obterEstatisticas();
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/tipos")
    public ResponseEntity<List<String>> listarTiposNotificacao() {
        List<String> tipos = List.of(
            "NOVA_AVALIACAO",
            "NOVO_SEGUIDOR", 
            "NOVO_EVENTO",
            "INSCRICAO_EVENTO"
        );
        return ResponseEntity.ok(tipos);
    }

    // Endpoint para notificações em tempo real (polling simples)
    @GetMapping("/usuario/{usuarioId}/polling")
    public ResponseEntity<Map<String, Object>> pollingNotificacoes(@PathVariable UUID usuarioId) {
        List<Notificacao> naoLidas = notificacaoService.buscarNotificacoesNaoLidas(usuarioId);
        long quantidade = notificacaoService.contarNotificacoesNaoLidas(usuarioId);
        
        return ResponseEntity.ok(Map.of(
            "naoLidas", naoLidas,
            "quantidade", quantidade,
            "temNovas", quantidade > 0
        ));
    }
}
