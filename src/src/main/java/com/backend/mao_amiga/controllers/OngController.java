package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Ong;
import com.backend.mao_amiga.models.enums.AreaInteresse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/ongs")
public class OngController {

    // Simulação de banco em memória
    private final Map<UUID, Ong> ongs = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Ong> criarOng(@RequestBody Map<String, String> dados) {
        try {
            Ong ong = new Ong(
                dados.get("email"),
                dados.get("senha"),
                dados.get("nomeCompleto"),
                dados.get("cnpj")
            );
            
            if (dados.containsKey("endereco")) {
                ong.setEndereco(dados.get("endereco"));
            }
            if (dados.containsKey("telefone")) {
                ong.setTelefone(dados.get("telefone"));
            }
            if (dados.containsKey("maisSobre")) {
                ong.setMaisSobre(dados.get("maisSobre"));
            }
            
            ongs.put(ong.getId(), ong);
            return ResponseEntity.ok(ong);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ong> buscarOng(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        return ong != null ? ResponseEntity.ok(ong) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Ong>> listarOngs() {
        return ResponseEntity.ok(new ArrayList<>(ongs.values()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ong> atualizarOng(
            @PathVariable UUID id, 
            @RequestBody Map<String, String> dados) {
        
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        if (dados.containsKey("endereco")) {
            ong.setEndereco(dados.get("endereco"));
        }
        if (dados.containsKey("telefone")) {
            ong.setTelefone(dados.get("telefone"));
        }
        if (dados.containsKey("maisSobre")) {
            ong.setMaisSobre(dados.get("maisSobre"));
        }
        if (dados.containsKey("fotoDePerfil")) {
            ong.setFotoDePerfil(dados.get("fotoDePerfil"));
        }

        return ResponseEntity.ok(ong);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarOng(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }
        
        ong.desativarConta();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/areas-atuacao")
    public ResponseEntity<Ong> adicionarAreaAtuacao(
            @PathVariable UUID id, 
            @RequestBody Map<String, String> dados) {
        
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse area = AreaInteresse.valueOf(dados.get("area"));
            ong.adicionarAreaAtuacao(area);
            return ResponseEntity.ok(ong);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/areas-atuacao/{area}")
    public ResponseEntity<Ong> removerAreaAtuacao(
            @PathVariable UUID id, 
            @PathVariable String area) {
        
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            ong.removerAreaAtuacao(areaEnum);
            return ResponseEntity.ok(ong);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/seguidores")
    public ResponseEntity<Set<UUID>> listarSeguidores(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ong.getSeguidores());
    }

    @GetMapping("/{id}/eventos-organizados")
    public ResponseEntity<List<UUID>> listarEventosOrganizados(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(ong.getEventosOrganizados());
    }

    @PostMapping("/{id}/verificar")
    public ResponseEntity<Ong> verificarOng(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        ong.verificarOng();
        return ResponseEntity.ok(ong);
    }

    @GetMapping("/verificadas")
    public ResponseEntity<List<Ong>> listarOngsVerificadas() {
        List<Ong> ongsVerificadas = ongs.values().stream()
                .filter(ong -> ong.getVerificada())
                .toList();
        
        return ResponseEntity.ok(ongsVerificadas);
    }

    @GetMapping("/por-area/{area}")
    public ResponseEntity<List<Ong>> listarOngsPorArea(@PathVariable String area) {
        try {
            AreaInteresse areaEnum = AreaInteresse.valueOf(area);
            List<Ong> ongsPorArea = ongs.values().stream()
                    .filter(ong -> ong.atuaNaArea(areaEnum))
                    .toList();
            
            return ResponseEntity.ok(ongsPorArea);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        Ong ong = ongs.get(id);
        if (ong == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("quantidadeSeguidores", ong.getQuantidadeSeguidores());
        estatisticas.put("quantidadeEventosOrganizados", ong.getQuantidadeEventosOrganizados());
        estatisticas.put("verificada", ong.getVerificada());
        estatisticas.put("areasAtuacao", ong.getAreasDeAtuacao());
        estatisticas.put("nota", ong.getNota());

        return ResponseEntity.ok(estatisticas);
    }
}
