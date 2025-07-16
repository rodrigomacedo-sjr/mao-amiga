package com.backend.mao_amiga.models;

import jakarta.persistence.*;
import com.backend.mao_amiga.models.enums.TipoNotificacao;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notificacoes")
public class Notificacao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;
    
    @Column(nullable = false, length = 500)
    private String mensagem;
    
    @Column(nullable = false)
    private UUID usuarioDestinoId;
    
    @Column(nullable = false)
    private LocalDateTime criadaEm;
    
    @Column(nullable = false)
    private Boolean lida;
    
    private UUID eventoRelacionadoId;
    private UUID usuarioOrigemId;

    // Construtores
    public Notificacao() {
        this.criadaEm = LocalDateTime.now();
        this.lida = false;
    }

    public Notificacao(TipoNotificacao tipo, String mensagem, UUID usuarioDestinoId) {
        this();
        this.tipo = tipo;
        this.mensagem = mensagem;
        this.usuarioDestinoId = usuarioDestinoId;
    }

    // Métodos de negócio
    public void marcarComoLida() {
        this.lida = true;
    }

    public boolean isRecente() {
        return criadaEm.isAfter(LocalDateTime.now().minusHours(24));
    }

    // Getters essenciais
    public UUID getId() {
        return id;
    }

    public TipoNotificacao getTipo() {
        return tipo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public UUID getUsuarioDestinoId() {
        return usuarioDestinoId;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public Boolean getLida() {
        return lida;
    }

    public Boolean isLida() {
        return lida;
    }

    public UUID getEventoRelacionadoId() {
        return eventoRelacionadoId;
    }

    public void setEventoRelacionadoId(UUID eventoRelacionadoId) {
        this.eventoRelacionadoId = eventoRelacionadoId;
    }

    public UUID getUsuarioOrigemId() {
        return usuarioOrigemId;
    }

    public void setUsuarioOrigemId(UUID usuarioOrigemId) {
        this.usuarioOrigemId = usuarioOrigemId;
    }
}
