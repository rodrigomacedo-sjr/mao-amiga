package com.backend.mao_amiga.models;

import jakarta.persistence.*;
import com.backend.mao_amiga.models.enums.AreaInteresse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ongs")
public class Ong extends Usuario {

  @Column(unique = true, length = 18)
  private String cnpj;

  @Column(length = 500)
  private String endereco;

  @Column(length = 20)
  private String telefone;

  @ElementCollection(targetClass = AreaInteresse.class)
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "ong_areas_atuacao", joinColumns = @JoinColumn(name = "ong_id"))
  @Column(name = "area_atuacao")
  private Set<AreaInteresse> areasDeAtuacao;

  @ElementCollection
  @CollectionTable(name = "ong_eventos_criados", joinColumns = @JoinColumn(name = "ong_id"))
  @Column(name = "evento_id")
  private List<UUID> eventosOrganizados;

  @ElementCollection
  @CollectionTable(name = "ong_seguidores", joinColumns = @JoinColumn(name = "ong_id"))
  @Column(name = "voluntario_id")
  private Set<UUID> seguidores;

  @Column(nullable = false)
  private Boolean verificada;

  // Construtores
  public Ong() {
    super();
    this.areasDeAtuacao = new HashSet<>();
    this.eventosOrganizados = new ArrayList<>();
    this.seguidores = new HashSet<>();
    this.verificada = false;
  }

  public Ong(String email, String senha, String nomeCompleto, String cnpj) {
    super(email, senha, nomeCompleto);
    this.cnpj = cnpj;
    this.areasDeAtuacao = new HashSet<>();
    this.eventosOrganizados = new ArrayList<>();
    this.seguidores = new HashSet<>();
    this.verificada = false;
  }

  // Métodos de negócio
  public void adicionarAreaAtuacao(AreaInteresse area) {
    if (area != null) {
      this.areasDeAtuacao.add(area);
    }
  }

  public void adicionarAreaDeAtuacao(AreaInteresse area) {
    adicionarAreaAtuacao(area);
  }

  public void removerAreaAtuacao(AreaInteresse area) {
    this.areasDeAtuacao.remove(area);
  }

  public void removerAreaDeAtuacao(AreaInteresse area) {
    removerAreaAtuacao(area);
  }

  public void adicionarEventoOrganizado(UUID eventoId) {
    if (eventoId != null && !this.eventosOrganizados.contains(eventoId)) {
      this.eventosOrganizados.add(eventoId);
    }
  }

  public void adicionarSeguidor(UUID voluntarioId) {
    if (voluntarioId != null) {
      this.seguidores.add(voluntarioId);
    }
  }

  public void removerSeguidor(UUID voluntarioId) {
    this.seguidores.remove(voluntarioId);
  }

  public boolean temSeguidor(UUID voluntarioId) {
    return this.seguidores.contains(voluntarioId);
  }

  public void verificarOng() {
    this.verificada = true;
  }

  public void setVerificada(boolean verificada) {
    this.verificada = verificada;
  }

  public int getQuantidadeSeguidores() {
    return this.seguidores.size();
  }

  public int getQuantidadeEventosOrganizados() {
    return this.eventosOrganizados.size();
  }

  public boolean atuaNaArea(AreaInteresse area) {
    return this.areasDeAtuacao.contains(area);
  }

  public boolean jaOrganizouEvento(UUID eventoId) {
    return this.eventosOrganizados.contains(eventoId);
  }

  // Getters essenciais
  public String getCnpj() {
    return cnpj;
  }

  public String getEndereco() {
    return endereco;
  }

  public void setEndereco(String endereco) {
    this.endereco = endereco;
  }

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }

  public Set<AreaInteresse> getAreasDeAtuacao() {
    return areasDeAtuacao;
  }

  public List<UUID> getEventosOrganizados() {
    return eventosOrganizados;
  }

  public Set<UUID> getSeguidores() {
    return seguidores;
  }

  public Boolean getVerificada() {
    return verificada;
  }
}
