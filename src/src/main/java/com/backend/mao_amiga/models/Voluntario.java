package com.backend.mao_amiga.models;

import jakarta.persistence.*;
import com.backend.mao_amiga.models.enums.AreaInteresse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "voluntarios")
public class Voluntario extends Usuario {
    
    @ElementCollection(targetClass = AreaInteresse.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "voluntario_areas_interesse", 
                    joinColumns = @JoinColumn(name = "voluntario_id"))
    @Column(name = "area_interesse")
    private Set<AreaInteresse> areasDeInteresse;
    
    @ElementCollection
    @CollectionTable(name = "voluntario_historico_eventos", 
                    joinColumns = @JoinColumn(name = "voluntario_id"))
    @Column(name = "evento_id")
    private List<UUID> historicoDeEventos;
    
    @ElementCollection
    @CollectionTable(name = "voluntario_ongs_seguidas", 
                    joinColumns = @JoinColumn(name = "voluntario_id"))
    @Column(name = "ong_id")
    private Set<UUID> ongsSeguidas;
    
    @ElementCollection
    @CollectionTable(name = "voluntario_eventos_favoritos", 
                    joinColumns = @JoinColumn(name = "voluntario_id"))
    @Column(name = "evento_id")
    private Set<UUID> eventosFavoritos;

    // Construtores
    public Voluntario() {
        super();
        this.areasDeInteresse = new HashSet<>();
        this.historicoDeEventos = new ArrayList<>();
        this.ongsSeguidas = new HashSet<>();
        this.eventosFavoritos = new HashSet<>();
    }

    public Voluntario(String email, String senha, String nomeCompleto) {
        super(email, senha, nomeCompleto);
        this.areasDeInteresse = new HashSet<>();
        this.historicoDeEventos = new ArrayList<>();
        this.ongsSeguidas = new HashSet<>();
        this.eventosFavoritos = new HashSet<>();
    }

    // Métodos de negócio
    public void adicionarAreaInteresse(AreaInteresse area) {
        if (area != null) {
            this.areasDeInteresse.add(area);
        }
    }

    public void adicionarAreaDeInteresse(AreaInteresse area) {
        adicionarAreaInteresse(area);
    }

    public void removerAreaInteresse(AreaInteresse area) {
        this.areasDeInteresse.remove(area);
    }

    public void removerAreaDeInteresse(AreaInteresse area) {
        removerAreaInteresse(area);
    }

    public void adicionarEventoAoHistorico(UUID eventoId) {
        if (eventoId != null && !this.historicoDeEventos.contains(eventoId)) {
            this.historicoDeEventos.add(eventoId);
        }
    }

    public void seguirOng(UUID ongId) {
        if (ongId != null) {
            this.ongsSeguidas.add(ongId);
        }
    }

    public void deixarDeSeguirOng(UUID ongId) {
        this.ongsSeguidas.remove(ongId);
    }

    public boolean estaSeguindoOng(UUID ongId) {
        return this.ongsSeguidas.contains(ongId);
    }

    public void favoritarEvento(UUID eventoId) {
        if (eventoId != null) {
            this.eventosFavoritos.add(eventoId);
        }
    }

    public void desfavoritarEvento(UUID eventoId) {
        this.eventosFavoritos.remove(eventoId);
    }

    public boolean temInteresseNaArea(AreaInteresse area) {
        return this.areasDeInteresse.contains(area);
    }

    public boolean temInteresseEm(AreaInteresse area) {
        return this.areasDeInteresse.contains(area);
    }

    public boolean jaParticipouDoEvento(UUID eventoId) {
        return this.historicoDeEventos.contains(eventoId);
    }

    public boolean eventoEstaFavoritado(UUID eventoId) {
        return this.eventosFavoritos.contains(eventoId);
    }

    public int getQuantidadeEventosParticipados() {
        return this.historicoDeEventos.size();
    }

    // Getters essenciais
    public Set<AreaInteresse> getAreasDeInteresse() {
        return areasDeInteresse;
    }

    public List<UUID> getHistoricoDeEventos() {
        return historicoDeEventos;
    }

    public Set<UUID> getOngsSeguidas() {
        return ongsSeguidas;
    }

    public Set<UUID> getEventosFavoritos() {
        return eventosFavoritos;
    }
}
