package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Voluntario;
import com.backend.mao_amiga.models.enums.AreaInteresse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/voluntarios")
public class VoluntarioController {

    // Simulação de banco em memória
    private final Map<UUID, Voluntario> voluntarios = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Voluntario> criarVoluntario(@RequestBody Map<String, String> dados) {
        try {
            Voluntario voluntario = new Voluntario(
                dados.get("email"),
                dados.get("senha"),
                dados.get("nomeCompleto")
            );
            
            if (dados.containsKey("maisSobre")) {
                voluntario.setMaisSobre(dados.get("maisSobre"));
            }
            
            voluntarios.put(voluntario.getId(), voluntario);
            return ResponseEntity.ok(voluntario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voluntario> buscarVoluntario(@PathVariable UUID id) {
        Voluntario voluntario = voluntarios.get(id);
        return voluntario != null ? ResponseEntity.ok(voluntario) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Voluntario>> listarVoluntarios() {
        return ResponseEntity.ok(new ArrayList<>(voluntarios.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voluntario> atualizarVoluntario(
            @PathVariable UUID id, 
            @RequestBody Map<String, String> dados) {
        
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        if (dados.containsKey("nomeCompleto")) {
            // Note: Em uma implementação real, você usaria setters apropriados
            // voluntario.setNomeCompleto(dados.get("nomeCompleto"));
        }
        if (dados.containsKey("maisSobre")) {
            voluntario.setMaisSobre(dados.get("maisSobre"));
        }
        if (dados.containsKey("fotoDePerfil")) {
            voluntario.setFotoDePerfil(dados.get("fotoDePerfil"));
        }

        return ResponseEntity.ok(voluntario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarVoluntario(@PathVariable UUID id) {
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }
        
        voluntario.desativarConta();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/areas-interesse")
    public ResponseEntity<Voluntario> adicionarAreaInteresse(
            @PathVariable UUID id, 
            @RequestBody Map<String, String> dados) {
        
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse area = AreaInteresse.valueOf(dados.get("area"));
            voluntario.adicionarAreaInteresse(area);
            return ResponseEntity.ok(voluntario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/areas-interesse/{area}")
    public ResponseEntity<Voluntario> removerAreaInteresse(
            @PathVariable UUID id, 
            @PathVariable String area) {
        
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            voluntario.removerAreaInteresse(areaEnum);
            return ResponseEntity.ok(voluntario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{voluntarioId}/seguir-ong/{ongId}")
    public ResponseEntity<Voluntario> seguirOng(
            @PathVariable UUID voluntarioId, 
            @PathVariable UUID ongId) {
        
        Voluntario voluntario = voluntarios.get(voluntarioId);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        voluntario.seguirOng(ongId);
        return ResponseEntity.ok(voluntario);
    }

    @DeleteMapping("/{voluntarioId}/deixar-seguir-ong/{ongId}")
    public ResponseEntity<Voluntario> deixarDeSeguirOng(
            @PathVariable UUID voluntarioId, 
            @PathVariable UUID ongId) {
        
        Voluntario voluntario = voluntarios.get(voluntarioId);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        voluntario.deixarDeSeguirOng(ongId);
        return ResponseEntity.ok(voluntario);
    }

    @GetMapping("/{id}/ongs-seguidas")
    public ResponseEntity<Set<UUID>> obterOngsSeguidas(@PathVariable UUID id) {
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(voluntario.getOngsSeguidas());
    }

    @GetMapping("/buscar-por-area/{area}")
    public ResponseEntity<List<Voluntario>> buscarVoluntariosPorArea(@PathVariable String area) {
        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            List<Voluntario> voluntariosPorArea = voluntarios.values().stream()
                    .filter(voluntario -> voluntario.temInteresseNaArea(areaEnum))
                    .toList();
            
            return ResponseEntity.ok(voluntariosPorArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("quantidadeEventosParticipados", voluntario.getQuantidadeEventosParticipados());
        estatisticas.put("quantidadeAreasDeInteresse", voluntario.getAreasDeInteresse().size());
        estatisticas.put("quantidadeOngsSeguidas", voluntario.getOngsSeguidas().size());
        estatisticas.put("quantidadeEventosFavoritos", voluntario.getEventosFavoritos().size());
        estatisticas.put("areasDeInteresse", voluntario.getAreasDeInteresse());
        estatisticas.put("nota", voluntario.getNota());

        return ResponseEntity.ok(estatisticas);
    }

    @PostMapping("/{voluntarioId}/favoritar-evento/{eventoId}")
    public ResponseEntity<Voluntario> favoritarEvento(
            @PathVariable UUID voluntarioId, 
            @PathVariable UUID eventoId) {
        
        Voluntario voluntario = voluntarios.get(voluntarioId);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        voluntario.favoritarEvento(eventoId);
        return ResponseEntity.ok(voluntario);
    }

    @DeleteMapping("/{voluntarioId}/desfavoritar-evento/{eventoId}")
    public ResponseEntity<Voluntario> desfavoritarEvento(
            @PathVariable UUID voluntarioId, 
            @PathVariable UUID eventoId) {
        
        Voluntario voluntario = voluntarios.get(voluntarioId);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        voluntario.desfavoritarEvento(eventoId);
        return ResponseEntity.ok(voluntario);
    }

    @GetMapping("/{id}/historico-eventos")
    public ResponseEntity<List<UUID>> obterHistoricoEventos(@PathVariable UUID id) {
        Voluntario voluntario = voluntarios.get(id);
        if (voluntario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(voluntario.getHistoricoDeEventos());
    }
}
