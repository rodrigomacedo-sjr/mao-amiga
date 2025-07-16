package com.backend.mao_amiga.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "posts")
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID autorId;
    
    @Column(nullable = false, length = 10)
    private String tipoAutor; // "VOLUNTARIO" ou "ONG"
    
    @Column(nullable = false, length = 2000)
    private String conteudo;
    
    @Column(length = 500)
    private String imagemUrl;
    
    @ElementCollection
    @CollectionTable(name = "post_curtidas", 
                    joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "usuario_id")
    private Set<UUID> curtidas;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comentario> comentarios;
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private Boolean ativo;

    // Construtores
    public Post() {
        this.criadoEm = LocalDateTime.now();
        this.ativo = true;
        this.curtidas = new HashSet<>();
        this.comentarios = new ArrayList<>();
    }

    public Post(UUID autorId, String tipoAutor, String conteudo) {
        this();
        this.autorId = autorId;
        this.tipoAutor = tipoAutor;
        this.conteudo = conteudo;
    }

    // Métodos de negócio
    public boolean adicionarCurtida(UUID usuarioId) {
        if (usuarioId != null && this.ativo) {
            return this.curtidas.add(usuarioId);
        }
        return false;
    }

    public boolean removerCurtida(UUID usuarioId) {
        return this.curtidas.remove(usuarioId);
    }

    public boolean usuarioCurtiu(UUID usuarioId) {
        return this.curtidas.contains(usuarioId);
    }

    public int getQuantidadeCurtidas() {
        return this.curtidas.size();
    }

    public int getQuantidadeComentarios() {
        return this.comentarios.size();
    }

    public void adicionarComentario(Comentario comentario) {
        if (comentario != null && this.ativo) {
            comentario.setPost(this);
            this.comentarios.add(comentario);
        }
    }

    public void removerComentario(Comentario comentario) {
        if (comentario != null) {
            this.comentarios.remove(comentario);
            comentario.setPost(null);
        }
    }

    public void editarConteudo(String novoConteudo) {
        if (this.ativo && novoConteudo != null && !novoConteudo.trim().isEmpty()) {
            this.conteudo = novoConteudo;
        }
    }

    public void desativarPost() {
        this.ativo = false;
    }

    public void reativarPost() {
        this.ativo = true;
    }

    public boolean isAutorVoluntario() {
        return "VOLUNTARIO".equals(this.tipoAutor);
    }

    public boolean isAutorOng() {
        return "ONG".equals(this.tipoAutor);
    }

    public boolean temImagem() {
        return this.imagemUrl != null && !this.imagemUrl.trim().isEmpty();
    }

    public boolean isPostRecente() {
        return this.criadoEm.isAfter(LocalDateTime.now().minusHours(24));
    }

    // Métodos estáticos para criação
    public static Post criarPostVoluntario(UUID voluntarioId, String conteudo) {
        return new Post(voluntarioId, "VOLUNTARIO", conteudo);
    }

    public static Post criarPostOng(UUID ongId, String conteudo) {
        return new Post(ongId, "ONG", conteudo);
    }

    // Getters essenciais
    public UUID getId() {
        return id;
    }

    public UUID getAutorId() {
        return autorId;
    }

    public String getConteudo() {
        return conteudo;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public Set<UUID> getCurtidas() {
        return curtidas;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}