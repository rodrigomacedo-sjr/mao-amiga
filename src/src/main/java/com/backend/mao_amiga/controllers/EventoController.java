package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Evento;
import com.backend.mao_amiga.models.enums.AreaInteresse;
import com.backend.mao_amiga.models.enums.StatusEvento;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {

    // Simulação de banco em memória
    private final Map<UUID, Evento> eventos = new ConcurrentHashMap<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @PostMapping
    public ResponseEntity<Evento> criarEvento(@RequestBody Map<String, Object> dados) {
        try {
            Evento evento = new Evento(
                (String) dados.get("titulo"),
                (String) dados.get("descricao"),
                LocalDateTime.parse((String) dados.get("dataHoraInicio"), formatter),
                LocalDateTime.parse((String) dados.get("dataHoraFim"), formatter),
                UUID.fromString((String) dados.get("ongResponsavelId")),
                (String) dados.get("local"),
                (Integer) dados.get("vagasDisponiveis")
            );
            
            if (dados.containsKey("imagemCapa")) {
                evento.setImagemCapa((String) dados.get("imagemCapa"));
            }
            
            eventos.put(evento.getId(), evento);
            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> buscarEvento(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        return evento != null ? ResponseEntity.ok(evento) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Evento>> listarEventos() {
        return ResponseEntity.ok(new ArrayList<>(eventos.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Evento> atualizarEvento(
            @PathVariable UUID id, 
            @RequestBody Map<String, Object> dados) {
        
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            if (dados.containsKey("dataHoraInicio") && dados.containsKey("dataHoraFim")) {
                LocalDateTime novaDataInicio = LocalDateTime.parse((String) dados.get("dataHoraInicio"), formatter);
                LocalDateTime novaDataFim = LocalDateTime.parse((String) dados.get("dataHoraFim"), formatter);
                evento.alterarDataHora(novaDataInicio, novaDataFim);
            }
            
            if (dados.containsKey("imagemCapa")) {
                evento.setImagemCapa((String) dados.get("imagemCapa"));
            }

            return ResponseEntity.ok(evento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelarEvento(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }
        
        evento.cancelarEvento();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/abrir-inscricoes")
    public ResponseEntity<Evento> abrirInscricoes(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        evento.abrirInscricoes();
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{id}/fechar-inscricoes")
    public ResponseEntity<Evento> fecharInscricoes(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        evento.fecharInscricoes();
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{id}/iniciar")
    public ResponseEntity<Evento> iniciarEvento(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        evento.iniciarEvento();
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Evento> finalizarEvento(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        evento.finalizarEvento();
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{eventoId}/inscrever-voluntario/{voluntarioId}")
    public ResponseEntity<Evento> inscreverVoluntario(
            @PathVariable UUID eventoId,
            @PathVariable UUID voluntarioId) {
        
        Evento evento = eventos.get(eventoId);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        boolean sucesso = evento.inscreverVoluntario(voluntarioId);
        if (!sucesso) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(evento);
    }

    @DeleteMapping("/{eventoId}/cancelar-inscricao/{voluntarioId}")
    public ResponseEntity<Evento> cancelarInscricao(
            @PathVariable UUID eventoId,
            @PathVariable UUID voluntarioId) {
        
        Evento evento = eventos.get(eventoId);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        boolean sucesso = evento.cancelarInscricao(voluntarioId);
        if (!sucesso) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{eventoId}/confirmar-participacao/{voluntarioId}")
    public ResponseEntity<Evento> confirmarParticipacao(
            @PathVariable UUID eventoId,
            @PathVariable UUID voluntarioId) {
        
        Evento evento = eventos.get(eventoId);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        evento.confirmarParticipacao(voluntarioId);
        return ResponseEntity.ok(evento);
    }

    @PostMapping("/{id}/adicionar-area")
    public ResponseEntity<Evento> adicionarAreaRelacionada(
            @PathVariable UUID id,
            @RequestBody Map<String, String> dados) {
        
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse area = AreaInteresse.valueOf(dados.get("area"));
            evento.adicionarAreaRelacionada(area);
            return ResponseEntity.ok(evento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/area-interesse/{area}")
    public ResponseEntity<?> adicionarAreaInteresse(@PathVariable UUID id, @PathVariable String area) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            evento.adicionarAreaDeInteresse(areaEnum);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/voluntarios-inscritos")
    public ResponseEntity<Set<UUID>> listarVoluntariosInscritos(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(evento.getVoluntariosInscritos());
    }

    @GetMapping("/{id}/voluntarios-participaram")
    public ResponseEntity<Set<UUID>> listarVoluntariosParticiparam(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(evento.getVoluntariosParticiparam());
    }

    @GetMapping("/por-status/{status}")
    public ResponseEntity<List<Evento>> listarEventosPorStatus(@PathVariable String status) {
        try {
            StatusEvento statusEnum = StatusEvento.valueOf(status);
            List<Evento> eventosPorStatus = eventos.values().stream()
                    .filter(evento -> evento.getStatus() == statusEnum)
                    .toList();
            
            return ResponseEntity.ok(eventosPorStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/por-area/{area}")
    public ResponseEntity<List<Evento>> listarEventosPorArea(@PathVariable String area) {
        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            List<Evento> eventosPorArea = eventos.values().stream()
                    .filter(evento -> evento.getAreasRelacionadas().contains(areaEnum))
                    .toList();
            
            return ResponseEntity.ok(eventosPorArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/da-ong/{ongId}")
    public ResponseEntity<List<Evento>> listarEventosDaOng(@PathVariable UUID ongId) {
        List<Evento> eventosDaOng = eventos.values().stream()
                .filter(evento -> evento.getOngResponsavelId().equals(ongId))
                .toList();
        
        return ResponseEntity.ok(eventosDaOng);
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        Evento evento = eventos.get(id);
        if (evento == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("vagasDisponiveis", evento.getVagasDisponiveis());
        estatisticas.put("vagasOcupadas", evento.getVagasOcupadas());
        estatisticas.put("vagasRestantes", evento.getVagasRestantes());
        estatisticas.put("quantidadeInscritos", evento.getVoluntariosInscritos().size());
        estatisticas.put("quantidadeParticiparam", evento.getVoluntariosParticiparam().size());
        estatisticas.put("status", evento.getStatus());
        estatisticas.put("areasRelacionadas", evento.getAreasRelacionadas());

        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/buscar-por-area/{area}")
    public ResponseEntity<List<Evento>> buscarEventosPorArea(@PathVariable String area) {
        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            List<Evento> eventosPorArea = eventos.values().stream()
                    .filter(evento -> evento.getAreasDeInteresse().contains(areaEnum))
                    .toList();
            return ResponseEntity.ok(eventosPorArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
