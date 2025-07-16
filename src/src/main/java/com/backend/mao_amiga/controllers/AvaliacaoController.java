package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Avaliacao;
import com.backend.mao_amiga.services.AvaliacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    @Autowired
    private AvaliacaoService avaliacaoService;

    @PostMapping("/voluntario-avaliar-ong")
    public ResponseEntity<Avaliacao> voluntarioAvaliarOng(@RequestBody Map<String, Object> dados) {
        try {
            UUID voluntarioId = UUID.fromString((String) dados.get("voluntarioId"));
            UUID ongId = UUID.fromString((String) dados.get("ongId"));
            UUID eventoId = UUID.fromString((String) dados.get("eventoId"));
            Float nota = Float.parseFloat(dados.get("nota").toString());
            String comentario = (String) dados.get("comentario");
            
            Avaliacao avaliacao = avaliacaoService.voluntarioAvaliarOng(
                voluntarioId, ongId, eventoId, nota, comentario
            );
            
            return ResponseEntity.ok(avaliacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/ong-avaliar-voluntario")
    public ResponseEntity<Avaliacao> ongAvaliarVoluntario(@RequestBody Map<String, Object> dados) {
        try {
            UUID ongId = UUID.fromString((String) dados.get("ongId"));
            UUID voluntarioId = UUID.fromString((String) dados.get("voluntarioId"));
            UUID eventoId = UUID.fromString((String) dados.get("eventoId"));
            Float nota = Float.parseFloat(dados.get("nota").toString());
            String comentario = (String) dados.get("comentario");
            
            Avaliacao avaliacao = avaliacaoService.ongAvaliarVoluntario(
                ongId, voluntarioId, eventoId, nota, comentario
            );
            
            return ResponseEntity.ok(avaliacao);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/recebidas/{usuarioId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesRecebidas(
            @PathVariable UUID usuarioId,
            @RequestParam String tipoUsuario) {
        
        if (!"ONG".equals(tipoUsuario) && !"VOLUNTARIO".equals(tipoUsuario)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesRecebidas(usuarioId, tipoUsuario);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/feitas/{usuarioId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesFeitas(
            @PathVariable UUID usuarioId,
            @RequestParam String tipoUsuario) {
        
        if (!"ONG".equals(tipoUsuario) && !"VOLUNTARIO".equals(tipoUsuario)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Avaliacao> avaliacoes = avaliacaoService.listarAvaliacoesFeitas(usuarioId, tipoUsuario);
        return ResponseEntity.ok(avaliacoes);
    }

    @GetMapping("/nota-media/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obterNotaMedia(
            @PathVariable UUID usuarioId,
            @RequestParam String tipoUsuario) {
        
        if (!"ONG".equals(tipoUsuario) && !"VOLUNTARIO".equals(tipoUsuario)) {
            return ResponseEntity.badRequest().build();
        }
        
        Float notaMedia = avaliacaoService.calcularNotaMedia(usuarioId, tipoUsuario);
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("usuarioId", usuarioId);
        resposta.put("tipoUsuario", tipoUsuario);
        resposta.put("notaMedia", notaMedia);
        resposta.put("notaFormatada", notaMedia != null ? String.format("%.1f", notaMedia) : "Sem avaliações");
        
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{avaliacaoId}/remover/{usuarioId}")
    public ResponseEntity<Map<String, String>> removerAvaliacao(
            @PathVariable UUID avaliacaoId,
            @PathVariable UUID usuarioId) {
        
        try {
            avaliacaoService.removerAvaliacao(avaliacaoId, usuarioId);
            
            Map<String, String> resposta = new HashMap<>();
            resposta.put("mensagem", "Avaliação removida com sucesso");
            resposta.put("avaliacaoId", avaliacaoId.toString());
            
            return ResponseEntity.ok(resposta);
        } catch (IllegalArgumentException e) {
            Map<String, String> erro = new HashMap<>();
            erro.put("erro", e.getMessage());
            
            return ResponseEntity.badRequest().body(erro);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avaliacao> buscarAvaliacao(@PathVariable UUID id) {
        try {
            // Como o service atual não expõe método público, vamos simular
            Map<UUID, Avaliacao> todasAvaliacoes = avaliacaoService.getAvaliacoes();
            Avaliacao avaliacao = todasAvaliacoes.get(id);
            
            return avaliacao != null ? ResponseEntity.ok(avaliacao) : ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/do-evento/{eventoId}")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesDoEvento(@PathVariable UUID eventoId) {
        Map<UUID, Avaliacao> todasAvaliacoes = avaliacaoService.getAvaliacoes();
        
        List<Avaliacao> avaliacoesDoEvento = todasAvaliacoes.values().stream()
                .filter(avaliacao -> avaliacao.getEventoRelacionadoId().equals(eventoId))
                .filter(Avaliacao::getAtiva)
                .toList();
        
        return ResponseEntity.ok(avaliacoesDoEvento);
    }

    @GetMapping("/entre-usuarios")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesEntreUsuarios(
            @RequestParam UUID usuario1Id,
            @RequestParam UUID usuario2Id) {
        
        Map<UUID, Avaliacao> todasAvaliacoes = avaliacaoService.getAvaliacoes();
        
        List<Avaliacao> avaliacoesEntreUsuarios = todasAvaliacoes.values().stream()
                .filter(avaliacao -> 
                    (avaliacao.getAvaliadorId().equals(usuario1Id) && avaliacao.getAvaliadoId().equals(usuario2Id)) ||
                    (avaliacao.getAvaliadorId().equals(usuario2Id) && avaliacao.getAvaliadoId().equals(usuario1Id))
                )
                .filter(Avaliacao::getAtiva)
                .toList();
        
        return ResponseEntity.ok(avaliacoesEntreUsuarios);
    }

    @GetMapping("/por-nota")
    public ResponseEntity<List<Avaliacao>> listarAvaliacoesPorNota(
            @RequestParam Float notaMinima,
            @RequestParam(required = false) Float notaMaxima) {
        
        if (notaMinima < 1.0f || notaMinima > 5.0f) {
            return ResponseEntity.badRequest().build();
        }
        
        Float notaMax = notaMaxima != null ? notaMaxima : 5.0f;
        if (notaMax < notaMinima || notaMax > 5.0f) {
            return ResponseEntity.badRequest().build();
        }
        
        Map<UUID, Avaliacao> todasAvaliacoes = avaliacaoService.getAvaliacoes();
        
        List<Avaliacao> avaliacoesPorNota = todasAvaliacoes.values().stream()
                .filter(avaliacao -> avaliacao.getNota() >= notaMinima && avaliacao.getNota() <= notaMax)
                .filter(Avaliacao::getAtiva)
                .toList();
        
        return ResponseEntity.ok(avaliacoesPorNota);
    }

    @GetMapping("/estatisticas/gerais")
    public ResponseEntity<Map<String, Object>> obterEstatisticasGerais() {
        Map<UUID, Avaliacao> todasAvaliacoes = avaliacaoService.getAvaliacoes();
        
        long totalAvaliacoes = todasAvaliacoes.size();
        long avaliacoesAtivas = todasAvaliacoes.values().stream()
                .filter(Avaliacao::getAtiva)
                .count();
        
        double notaMediaGeral = todasAvaliacoes.values().stream()
                .filter(Avaliacao::getAtiva)
                .mapToDouble(Avaliacao::getNota)
                .average()
                .orElse(0.0);
        
        long avaliacoesComComentario = todasAvaliacoes.values().stream()
                .filter(Avaliacao::getAtiva)
                .filter(Avaliacao::temComentario)
                .count();
        
        long avaliacoesVoluntarioParaOng = todasAvaliacoes.values().stream()
                .filter(Avaliacao::getAtiva)
                .filter(Avaliacao::isVoluntarioAvaliandoOng)
                .count();
        
        long avaliacoesOngParaVoluntario = todasAvaliacoes.values().stream()
                .filter(Avaliacao::getAtiva)
                .filter(Avaliacao::isOngAvaliandoVoluntario)
                .count();
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalAvaliacoes", totalAvaliacoes);
        estatisticas.put("avaliacoesAtivas", avaliacoesAtivas);
        estatisticas.put("notaMediaGeral", Math.round(notaMediaGeral * 10.0) / 10.0);
        estatisticas.put("avaliacoesComComentario", avaliacoesComComentario);
        estatisticas.put("avaliacoesVoluntarioParaOng", avaliacoesVoluntarioParaOng);
        estatisticas.put("avaliacoesOngParaVoluntario", avaliacoesOngParaVoluntario);
        
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/estatisticas/usuario/{usuarioId}")
    public ResponseEntity<Map<String, Object>> obterEstatisticasUsuario(
            @PathVariable UUID usuarioId,
            @RequestParam String tipoUsuario) {
        
        if (!"ONG".equals(tipoUsuario) && !"VOLUNTARIO".equals(tipoUsuario)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Avaliacao> avaliacoesRecebidas = avaliacaoService.listarAvaliacoesRecebidas(usuarioId, tipoUsuario);
        List<Avaliacao> avaliacoesFeitas = avaliacaoService.listarAvaliacoesFeitas(usuarioId, tipoUsuario);
        Float notaMedia = avaliacaoService.calcularNotaMedia(usuarioId, tipoUsuario);
        
        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("usuarioId", usuarioId);
        estatisticas.put("tipoUsuario", tipoUsuario);
        estatisticas.put("totalAvaliacoesRecebidas", avaliacoesRecebidas.size());
        estatisticas.put("totalAvaliacoesFeitas", avaliacoesFeitas.size());
        estatisticas.put("notaMedia", notaMedia);
        estatisticas.put("avaliacoesRecentesRecebidas", 
            avaliacoesRecebidas.stream().filter(Avaliacao::isAvaliacaoRecente).count());
        
        return ResponseEntity.ok(estatisticas);
    }
}
