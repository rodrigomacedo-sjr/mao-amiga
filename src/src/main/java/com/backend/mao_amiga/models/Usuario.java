package com.backend.mao_amiga.models;

import jakarta.persistence.*;
import com.backend.mao_amiga.models.enums.PreferenciaTema;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Usuario {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected UUID id;
    
    @Column(nullable = false, unique = true, length = 100)
    protected String email;
    
    @Column(nullable = false)
    protected String senha;
    
    @Column(nullable = false, length = 100)
    protected String nomeCompleto;
    
    @Column(length = 500)
    protected String fotoDePerfil;
    
    @Column(nullable = false)
    protected Float nota;
    
    @Column(length = 1000)
    protected String maisSobre;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    protected PreferenciaTema preferenciaTema;
    
    @Column(nullable = false)
    protected LocalDateTime criadoEm;
    
    @Column(nullable = false)
    protected Boolean ativo;

    // Construtores
    public Usuario() {
        this.nota = 0.0f;
        this.preferenciaTema = PreferenciaTema.CLARO;
        this.criadoEm = LocalDateTime.now();
        this.ativo = true;
    }

    public Usuario(String email, String senha, String nomeCompleto) {
        this();
        this.email = email;
        this.senha = senha;
        this.nomeCompleto = nomeCompleto;
    }

    // Métodos de negócio
    public void atualizarNota(Float novaNota) {
        if (novaNota >= 1.0f && novaNota <= 5.0f) {
            this.nota = novaNota;
        } else {
            throw new IllegalArgumentException("Nota deve estar entre 1 e 5");
        }
    }

    public void desativarConta() {
        this.ativo = false;
    }

    public void ativarConta() {
        this.ativo = true;
    }

    // Getters essenciais
    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public String getFotoDePerfil() {
        return fotoDePerfil;
    }

    public void setFotoDePerfil(String fotoDePerfil) {
        this.fotoDePerfil = fotoDePerfil;
    }

    public Float getNota() {
        return nota;
    }

    public String getMaisSobre() {
        return maisSobre;
    }

    public void setMaisSobre(String maisSobre) {
        this.maisSobre = maisSobre;
    }

    public PreferenciaTema getPreferenciaTema() {
        return preferenciaTema;
    }

    public void setPreferenciaTema(PreferenciaTema preferenciaTema) {
        this.preferenciaTema = preferenciaTema;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}