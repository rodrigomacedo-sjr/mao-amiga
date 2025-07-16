package com.backend.mao_amiga.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comentarios")
public class Comentario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID autorId;
    
    @Column(nullable = false, length = 10)
    private String tipoAutor; // "VOLUNTARIO" ou "ONG"
    
    @Column(nullable = false, length = 1000)
    private String conteudo;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(nullable = false)
    private Boolean ativo;

    // Construtores
    public Comentario() {
        this.criadoEm = LocalDateTime.now();
        this.ativo = true;
    }

    public Comentario(UUID autorId, String tipoAutor, String conteudo, Post post) {
        this();
        this.autorId = autorId;
        this.tipoAutor = tipoAutor;
        this.conteudo = conteudo;
        this.post = post;
    }

    // Métodos de negócio
    public void editarConteudo(String novoConteudo) {
        if (this.ativo && novoConteudo != null && !novoConteudo.trim().isEmpty()) {
            this.conteudo = novoConteudo;
        }
    }

    public void desativarComentario() {
        this.ativo = false;
    }

    public void reativarComentario() {
        this.ativo = true;
    }

    public boolean isAutorVoluntario() {
        return "VOLUNTARIO".equals(this.tipoAutor);
    }

    public boolean isAutorOng() {
        return "ONG".equals(this.tipoAutor);
    }

    public boolean isComentarioRecente() {
        return this.criadoEm.isAfter(LocalDateTime.now().minusHours(24));
    }

    // Métodos estáticos para criação
    public static Comentario criarComentarioVoluntario(UUID voluntarioId, String conteudo, Post post) {
        return new Comentario(voluntarioId, "VOLUNTARIO", conteudo, post);
    }

    public static Comentario criarComentarioOng(UUID ongId, String conteudo, Post post) {
        return new Comentario(ongId, "ONG", conteudo, post);
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

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}
