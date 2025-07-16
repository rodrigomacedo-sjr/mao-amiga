package com.backend.mao_amiga.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
public class Avaliacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false)
    private UUID avaliadorId;
    
    @Column(nullable = false, length = 10)
    private String tipoAvaliador; // "VOLUNTARIO" ou "ONG"
    
    @Column(nullable = false)
    private UUID avaliadoId;
    
    @Column(nullable = false, length = 10)
    private String tipoAvaliado; // "VOLUNTARIO" ou "ONG"
    
    @Column(nullable = false)
    private Float nota; // 1.0 a 5.0
    
    @Column(length = 1000)
    private String comentario;
    
    @Column(nullable = false)
    private UUID eventoRelacionadoId;
    
    @Column(nullable = false)
    private LocalDateTime criadaEm;
    
    @Column(nullable = false)
    private Boolean ativa;

    // Construtores
    public Avaliacao() {
        this.criadaEm = LocalDateTime.now();
        this.ativa = true;
    }

    public Avaliacao(UUID avaliadorId, String tipoAvaliador, UUID avaliadoId, 
                     String tipoAvaliado, Float nota, String comentario, 
                     UUID eventoRelacionadoId) {
        this();
        this.avaliadorId = avaliadorId;
        this.tipoAvaliador = tipoAvaliador;
        this.avaliadoId = avaliadoId;
        this.tipoAvaliado = tipoAvaliado;
        this.nota = nota;
        this.comentario = comentario;
        this.eventoRelacionadoId = eventoRelacionadoId;
    }

    // Métodos de negócio
    public void validarNota() {
        if (this.nota == null || this.nota < 1.0f || this.nota > 5.0f) {
            throw new IllegalArgumentException("Nota deve estar entre 1.0 e 5.0");
        }
    }

    public void atualizarAvaliacao(Float novaNota, String novoComentario) {
        if (this.ativa) {
            this.nota = novaNota;
            this.comentario = novoComentario;
            validarNota();
        } else {
            throw new IllegalStateException("Não é possível atualizar uma avaliação inativa");
        }
    }

    public void desativarAvaliacao() {
        this.ativa = false;
    }

    public void reativarAvaliacao() {
        this.ativa = true;
    }

    public boolean isVoluntarioAvaliandoOng() {
        return "VOLUNTARIO".equals(this.tipoAvaliador) && "ONG".equals(this.tipoAvaliado);
    }

    public boolean isOngAvaliandoVoluntario() {
        return "ONG".equals(this.tipoAvaliador) && "VOLUNTARIO".equals(this.tipoAvaliado);
    }

    public boolean temComentario() {
        return this.comentario != null && !this.comentario.trim().isEmpty();
    }

    public boolean isAvaliacaoRecente() {
        return this.criadaEm.isAfter(LocalDateTime.now().minusDays(30));
    }

    // Métodos estáticos para criação
    public static Avaliacao criarAvaliacaoVoluntarioParaOng(UUID voluntarioId, UUID ongId, 
                                                           Float nota, String comentario, 
                                                           UUID eventoId) {
        return new Avaliacao(voluntarioId, "VOLUNTARIO", ongId, "ONG", 
                           nota, comentario, eventoId);
    }

    public static Avaliacao criarAvaliacaoOngParaVoluntario(UUID ongId, UUID voluntarioId, 
                                                          Float nota, String comentario, 
                                                          UUID eventoId) {
        return new Avaliacao(ongId, "ONG", voluntarioId, "VOLUNTARIO", 
                           nota, comentario, eventoId);
    }

    // Getters essenciais
    public UUID getId() {
        return id;
    }

    public UUID getAvaliadorId() {
        return avaliadorId;
    }

    public UUID getAvaliadoId() {
        return avaliadoId;
    }

    public Float getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    public UUID getEventoRelacionadoId() {
        return eventoRelacionadoId;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public Boolean getAtiva() {
        return ativa;
    }
}