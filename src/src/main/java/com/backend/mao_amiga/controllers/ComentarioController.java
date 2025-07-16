package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Comentario;
import com.backend.mao_amiga.models.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/comentarios")
public class ComentarioController {

    // Simulação de banco em memória
    private final Map<UUID, Comentario> comentarios = new ConcurrentHashMap<>();
    private final Map<UUID, Post> posts = new ConcurrentHashMap<>(); // Referência aos posts

    @PostMapping
    public ResponseEntity<Comentario> criarComentario(@RequestBody Map<String, Object> dados) {
        try {
            UUID autorId = UUID.fromString((String) dados.get("autorId"));
            String tipoAutor = (String) dados.get("tipoAutor"); // "VOLUNTARIO" ou "ONG"
            String conteudo = (String) dados.get("conteudo");
            UUID postId = UUID.fromString((String) dados.get("postId"));
            
            Post post = posts.get(postId);
            if (post == null) {
                return ResponseEntity.badRequest().build();
            }
            
            Comentario comentario;
            if ("VOLUNTARIO".equals(tipoAutor)) {
                comentario = Comentario.criarComentarioVoluntario(autorId, conteudo, post);
            } else if ("ONG".equals(tipoAutor)) {
                comentario = Comentario.criarComentarioOng(autorId, conteudo, post);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            // Adicionar comentário ao post
            post.adicionarComentario(comentario);
            
            comentarios.put(comentario.getId(), comentario);
            return ResponseEntity.ok(comentario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comentario> buscarComentario(@PathVariable UUID id) {
        Comentario comentario = comentarios.get(id);
        return comentario != null ? ResponseEntity.ok(comentario) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comentario> editarComentario(
            @PathVariable UUID id,
            @RequestBody Map<String, String> dados) {
        
        Comentario comentario = comentarios.get(id);
        if (comentario == null) {
            return ResponseEntity.notFound().build();
        }

        String novoConteudo = dados.get("conteudo");
        if (novoConteudo != null) {
            comentario.editarConteudo(novoConteudo);
        }

        return ResponseEntity.ok(comentario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarComentario(@PathVariable UUID id) {
        Comentario comentario = comentarios.get(id);
        if (comentario == null) {
            return ResponseEntity.notFound().build();
        }
        
        comentario.desativarComentario();
        
        // Remover comentário do post
        Post post = comentario.getPost();
        if (post != null) {
            post.removerComentario(comentario);
        }
        
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/do-post/{postId}")
    public ResponseEntity<List<Comentario>> listarComentariosDoPost(@PathVariable UUID postId) {
        Post post = posts.get(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        List<Comentario> comentariosDoPost = comentarios.values().stream()
                .filter(comentario -> comentario.getPost() != null && 
                                    comentario.getPost().getId().equals(postId) && 
                                    comentario.getAtivo())
                .sorted((c1, c2) -> c1.getCriadoEm().compareTo(c2.getCriadoEm())) // Mais antigos primeiro
                .toList();
        
        return ResponseEntity.ok(comentariosDoPost);
    }

    @GetMapping("/do-autor/{autorId}")
    public ResponseEntity<List<Comentario>> listarComentariosDoAutor(@PathVariable UUID autorId) {
        List<Comentario> comentariosDoAutor = comentarios.values().stream()
                .filter(comentario -> comentario.getAutorId().equals(autorId) && comentario.getAtivo())
                .sorted((c1, c2) -> c2.getCriadoEm().compareTo(c1.getCriadoEm())) // Mais recentes primeiro
                .toList();
        
        return ResponseEntity.ok(comentariosDoAutor);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Comentario>> listarComentariosPorTipo(@PathVariable String tipo) {
        if (!"VOLUNTARIO".equals(tipo) && !"ONG".equals(tipo)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Comentario> comentariosPorTipo = comentarios.values().stream()
                .filter(comentario -> comentario.getAtivo())
                .filter(comentario -> tipo.equals("VOLUNTARIO") ? 
                                    comentario.isAutorVoluntario() : 
                                    comentario.isAutorOng())
                .sorted((c1, c2) -> c2.getCriadoEm().compareTo(c1.getCriadoEm()))
                .toList();
        
        return ResponseEntity.ok(comentariosPorTipo);
    }

    @GetMapping("/recentes")
    public ResponseEntity<List<Comentario>> listarComentariosRecentes(
            @RequestParam(defaultValue = "50") int limite) {
        
        List<Comentario> comentariosRecentes = comentarios.values().stream()
                .filter(comentario -> comentario.getAtivo())
                .filter(Comentario::isComentarioRecente) // Últimas 24 horas
                .sorted((c1, c2) -> c2.getCriadoEm().compareTo(c1.getCriadoEm()))
                .limit(limite)
                .toList();
        
        return ResponseEntity.ok(comentariosRecentes);
    }

    @PostMapping("/{id}/reativar")
    public ResponseEntity<Comentario> reativarComentario(@PathVariable UUID id) {
        Comentario comentario = comentarios.get(id);
        if (comentario == null) {
            return ResponseEntity.notFound().build();
        }
        
        comentario.reativarComentario();
        
        // Re-adicionar comentário ao post se necessário
        Post post = comentario.getPost();
        if (post != null && !post.getComentarios().contains(comentario)) {
            post.adicionarComentario(comentario);
        }
        
        return ResponseEntity.ok(comentario);
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        Comentario comentario = comentarios.get(id);
        if (comentario == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("isAutorVoluntario", comentario.isAutorVoluntario());
        estatisticas.put("isAutorOng", comentario.isAutorOng());
        estatisticas.put("isComentarioRecente", comentario.isComentarioRecente());
        estatisticas.put("ativo", comentario.getAtivo());
        estatisticas.put("criadoEm", comentario.getCriadoEm());
        
        return ResponseEntity.ok(estatisticas);
    }

    // Método auxiliar para sincronizar com posts (usado por outros controllers)
    public void sincronizarPost(UUID postId, Post post) {
        this.posts.put(postId, post);
    }

    // Método para obter estatísticas gerais
    @GetMapping("/estatisticas/gerais")
    public ResponseEntity<Map<String, Object>> obterEstatisticasGerais() {
        long totalComentarios = comentarios.size();
        long comentariosAtivos = comentarios.values().stream()
                .filter(Comentario::getAtivo)
                .count();
        long comentariosRecentes = comentarios.values().stream()
                .filter(comentario -> comentario.getAtivo() && comentario.isComentarioRecente())
                .count();
        long comentariosVoluntarios = comentarios.values().stream()
                .filter(comentario -> comentario.getAtivo() && comentario.isAutorVoluntario())
                .count();
        long comentariosOngs = comentarios.values().stream()
                .filter(comentario -> comentario.getAtivo() && comentario.isAutorOng())
                .count();

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("totalComentarios", totalComentarios);
        estatisticas.put("comentariosAtivos", comentariosAtivos);
        estatisticas.put("comentariosRecentes", comentariosRecentes);
        estatisticas.put("comentariosVoluntarios", comentariosVoluntarios);
        estatisticas.put("comentariosOngs", comentariosOngs);
        
        return ResponseEntity.ok(estatisticas);
    }
}
