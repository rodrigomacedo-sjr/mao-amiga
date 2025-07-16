package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Post;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    // Simulação de banco em memória
    private final Map<UUID, Post> posts = new ConcurrentHashMap<>();

    @PostMapping
    public ResponseEntity<Post> criarPost(@RequestBody Map<String, Object> dados) {
        try {
            UUID autorId = UUID.fromString((String) dados.get("autorId"));
            String tipoAutor = (String) dados.get("tipoAutor"); // "VOLUNTARIO" ou "ONG"
            String conteudo = (String) dados.get("conteudo");
            
            Post post;
            if ("VOLUNTARIO".equals(tipoAutor)) {
                post = Post.criarPostVoluntario(autorId, conteudo);
            } else if ("ONG".equals(tipoAutor)) {
                post = Post.criarPostOng(autorId, conteudo);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
            posts.put(post.getId(), post);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> buscarPost(@PathVariable UUID id) {
        Post post = posts.get(id);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Post>> listarPosts() {
        List<Post> postsList = posts.values().stream()
                .filter(post -> post.getAtivo())
                .sorted((p1, p2) -> p2.getCriadoEm().compareTo(p1.getCriadoEm())) // Mais recentes primeiro
                .toList();
        
        return ResponseEntity.ok(postsList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> editarPost(
            @PathVariable UUID id,
            @RequestBody Map<String, String> dados) {
        
        Post post = posts.get(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        String novoConteudo = dados.get("conteudo");
        if (novoConteudo != null) {
            post.editarConteudo(novoConteudo);
        }

        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPost(@PathVariable UUID id) {
        Post post = posts.get(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        
        post.desativarPost();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{postId}/curtir/{usuarioId}")
    public ResponseEntity<Map<String, Object>> curtirPost(
            @PathVariable UUID postId,
            @PathVariable UUID usuarioId) {
        
        Post post = posts.get(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        boolean sucesso = post.adicionarCurtida(usuarioId);
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("curtido", sucesso);
        resposta.put("totalCurtidas", post.getQuantidadeCurtidas());
        
        return ResponseEntity.ok(resposta);
    }

    @DeleteMapping("/{postId}/descurtir/{usuarioId}")
    public ResponseEntity<Map<String, Object>> descurtirPost(
            @PathVariable UUID postId,
            @PathVariable UUID usuarioId) {
        
        Post post = posts.get(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        boolean sucesso = post.removerCurtida(usuarioId);
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("descurtido", sucesso);
        resposta.put("totalCurtidas", post.getQuantidadeCurtidas());
        
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{postId}/curtidas")
    public ResponseEntity<Set<UUID>> listarCurtidas(@PathVariable UUID postId) {
        Post post = posts.get(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(post.getCurtidas());
    }

    @GetMapping("/{postId}/usuario-curtiu/{usuarioId}")
    public ResponseEntity<Map<String, Boolean>> verificarSeCurtiu(
            @PathVariable UUID postId,
            @PathVariable UUID usuarioId) {
        
        Post post = posts.get(postId);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Boolean> resposta = new HashMap<>();
        resposta.put("curtiu", post.usuarioCurtiu(usuarioId));
        
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/do-autor/{autorId}")
    public ResponseEntity<List<Post>> listarPostsDoAutor(@PathVariable UUID autorId) {
        List<Post> postsDoAutor = posts.values().stream()
                .filter(post -> post.getAutorId().equals(autorId) && post.getAtivo())
                .sorted((p1, p2) -> p2.getCriadoEm().compareTo(p1.getCriadoEm()))
                .toList();
        
        return ResponseEntity.ok(postsDoAutor);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Post>> listarPostsPorTipo(@PathVariable String tipo) {
        if (!"VOLUNTARIO".equals(tipo) && !"ONG".equals(tipo)) {
            return ResponseEntity.badRequest().build();
        }
        
        List<Post> postsPorTipo = posts.values().stream()
                .filter(post -> post.getAtivo())
                .filter(post -> tipo.equals("VOLUNTARIO") ? post.isAutorVoluntario() : post.isAutorOng())
                .sorted((p1, p2) -> p2.getCriadoEm().compareTo(p1.getCriadoEm()))
                .toList();
        
        return ResponseEntity.ok(postsPorTipo);
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        Post post = posts.get(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> estatisticas = new HashMap<>();
        estatisticas.put("quantidadeCurtidas", post.getQuantidadeCurtidas());
        estatisticas.put("quantidadeComentarios", post.getQuantidadeComentarios());
        estatisticas.put("isAutorVoluntario", post.isAutorVoluntario());
        estatisticas.put("isAutorOng", post.isAutorOng());
        estatisticas.put("temImagem", post.temImagem());
        estatisticas.put("isPostRecente", post.isPostRecente());
        
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/feed/recentes")
    public ResponseEntity<List<Post>> feedRecente(@RequestParam(defaultValue = "20") int limite) {
        List<Post> feedRecente = posts.values().stream()
                .filter(post -> post.getAtivo())
                .sorted((p1, p2) -> p2.getCriadoEm().compareTo(p1.getCriadoEm()))
                .limit(limite)
                .toList();
        
        return ResponseEntity.ok(feedRecente);
    }

    @GetMapping("/feed/populares")
    public ResponseEntity<List<Post>> feedPopular(@RequestParam(defaultValue = "20") int limite) {
        List<Post> feedPopular = posts.values().stream()
                .filter(post -> post.getAtivo())
                .sorted((p1, p2) -> Integer.compare(p2.getQuantidadeCurtidas(), p1.getQuantidadeCurtidas()))
                .limit(limite)
                .toList();
        
        return ResponseEntity.ok(feedPopular);
    }
}
