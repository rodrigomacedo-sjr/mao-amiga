package com.backend.mao_amiga.models;

import jakarta.persistence.*;
import com.backend.mao_amiga.models.enums.AreaInteresse;
import com.backend.mao_amiga.models.enums.StatusEvento;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "eventos")
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 200)
    private String titulo;
    
    @Column(nullable = false, length = 1000)
    private String descricao;
    
    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;
    
    @Column(nullable = false)
    private LocalDateTime dataHoraFim;
    
    @Column(nullable = false)
    private UUID ongResponsavelId;
    
    @Column(length = 500)
    private String local;
    
    @Column(nullable = false)
    private Integer vagasDisponiveis;
    
    @Column(nullable = false)
    private Integer vagasOcupadas;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEvento status;
    
    @ElementCollection(targetClass = AreaInteresse.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "evento_areas_interesse", 
                    joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "area_interesse")
    private Set<AreaInteresse> areasRelacionadas;
    
    @ElementCollection
    @CollectionTable(name = "evento_inscritos", 
                    joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "voluntario_id")
    private Set<UUID> voluntariosInscritos;
    
    @ElementCollection
    @CollectionTable(name = "evento_participantes", 
                    joinColumns = @JoinColumn(name = "evento_id"))
    @Column(name = "voluntario_id")
    private Set<UUID> voluntariosParticiparam;
    
    @Column(nullable = false)
    private LocalDateTime criadoEm;
    
    @Column(length = 500)
    private String imagemCapa;

    // Construtores
    public Evento() {
        this.status = StatusEvento.PLANEJADO;
        this.vagasOcupadas = 0;
        this.criadoEm = LocalDateTime.now();
        this.areasRelacionadas = new HashSet<>();
        this.voluntariosInscritos = new HashSet<>();
        this.voluntariosParticiparam = new HashSet<>();
    }

    public Evento(String titulo, String descricao, LocalDateTime dataHoraInicio, 
                  LocalDateTime dataHoraFim, UUID ongResponsavelId, String local, 
                  Integer vagasDisponiveis) {
        this();
        
        // Validações
        if (dataHoraFim != null && dataHoraInicio != null && dataHoraFim.isBefore(dataHoraInicio)) {
            throw new IllegalArgumentException("Data de fim não pode ser anterior à data de início");
        }
        
        this.titulo = titulo;
        this.descricao = descricao;
        this.dataHoraInicio = dataHoraInicio;
        this.dataHoraFim = dataHoraFim;
        this.ongResponsavelId = ongResponsavelId;
        this.local = local;
        this.vagasDisponiveis = vagasDisponiveis;
    }

    // Métodos de negócio
    public boolean inscreverVoluntario(UUID voluntarioId) {
        if (podeReceberInscricoes() && temVagasDisponiveis() && voluntarioId != null) {
            boolean inscrito = this.voluntariosInscritos.add(voluntarioId);
            if (inscrito) {
                this.vagasOcupadas++;
            }
            return inscrito;
        }
        return false;
    }

    public boolean cancelarInscricao(UUID voluntarioId) {
        boolean removido = this.voluntariosInscritos.remove(voluntarioId);
        if (removido && this.vagasOcupadas > 0) {
            this.vagasOcupadas--;
        }
        return removido;
    }

    public void confirmarParticipacao(UUID voluntarioId) {
        if (this.voluntariosInscritos.contains(voluntarioId)) {
            this.voluntariosParticiparam.add(voluntarioId);
        }
    }

    public void alterarDataHora(LocalDateTime novaDataInicio, LocalDateTime novaDataFim) {
        if (this.status == StatusEvento.PLANEJADO || this.status == StatusEvento.ABERTO_INSCRICOES) {
            this.dataHoraInicio = novaDataInicio;
            this.dataHoraFim = novaDataFim;
        } else {
            throw new IllegalStateException("Não é possível alterar data de evento em andamento ou finalizado");
        }
    }

    public void abrirInscricoes() {
        if (this.status == StatusEvento.PLANEJADO) {
            this.status = StatusEvento.ABERTO_INSCRICOES;
        }
    }

    public void fecharInscricoes() {
        if (this.status == StatusEvento.ABERTO_INSCRICOES) {
            this.status = StatusEvento.INSCRICOES_FECHADAS;
        }
    }

    public void iniciarEvento() {
        if (this.status == StatusEvento.INSCRICOES_FECHADAS) {
            this.status = StatusEvento.EM_ANDAMENTO;
        }
    }

    public void finalizarEvento() {
        if (this.status == StatusEvento.EM_ANDAMENTO) {
            this.status = StatusEvento.FINALIZADO;
        }
    }

    public void cancelarEvento() {
        if (this.status != StatusEvento.FINALIZADO) {
            this.status = StatusEvento.CANCELADO;
        }
    }

    public boolean podeReceberInscricoes() {
        return this.status == StatusEvento.ABERTO_INSCRICOES;
    }

    public boolean temVagasDisponiveis() {
        return this.vagasOcupadas < this.vagasDisponiveis;
    }

    public int getVagasRestantes() {
        return this.vagasDisponiveis - this.vagasOcupadas;
    }

    public boolean voluntarioEstaInscrito(UUID voluntarioId) {
        return this.voluntariosInscritos.contains(voluntarioId);
    }

    public boolean voluntarioParticipou(UUID voluntarioId) {
        return this.voluntariosParticiparam.contains(voluntarioId);
    }

    public void adicionarAreaRelacionada(AreaInteresse area) {
        if (area != null) {
            this.areasRelacionadas.add(area);
        }
    }

    // Métodos para verificar status temporal do evento
    public boolean estaNoFuturo() {
        LocalDateTime agora = LocalDateTime.now();
        return this.dataHoraInicio.isAfter(agora);
    }

    public boolean estaEmAndamento() {
        LocalDateTime agora = LocalDateTime.now();
        return !agora.isBefore(this.dataHoraInicio) && !agora.isAfter(this.dataHoraFim);
    }

    public boolean estaFinalizado() {
        LocalDateTime agora = LocalDateTime.now();
        return agora.isAfter(this.dataHoraFim);
    }

    public double getTaxaOcupacao() {
        if (this.vagasDisponiveis == 0) {
            return 0.0;
        }
        return (double) this.vagasOcupadas / this.vagasDisponiveis;
    }

    // Métodos para compatibilidade com testes e funcionalidades específicas
    public void adicionarAreaDeInteresse(AreaInteresse area) {
        adicionarAreaRelacionada(area);
    }

    public Set<AreaInteresse> getAreasDeInteresse() {
        return getAreasRelacionadas();
    }

    public boolean cancelarInscricaoVoluntario(UUID voluntarioId) {
        boolean removido = this.voluntariosInscritos.remove(voluntarioId);
        if (removido && this.vagasOcupadas > 0) {
            this.vagasOcupadas--;
        }
        return removido;
    }

    public boolean marcarPresenca(UUID voluntarioId) {
        if (voluntarioEstaInscrito(voluntarioId)) {
            this.voluntariosParticiparam.add(voluntarioId);
            return true;
        }
        return false;
    }

    public Set<UUID> getVoluntariosPresentes() {
        return this.voluntariosParticiparam;
    }

    // Getters essenciais
    public UUID getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public LocalDateTime getDataHoraInicio() {
        return dataHoraInicio;
    }

    public LocalDateTime getDataHoraFim() {
        return dataHoraFim;
    }

    public UUID getOngResponsavelId() {
        return ongResponsavelId;
    }

    public String getLocal() {
        return local;
    }

    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }

    public Integer getVagasOcupadas() {
        return vagasOcupadas;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public Set<AreaInteresse> getAreasRelacionadas() {
        return areasRelacionadas;
    }

    public Set<UUID> getVoluntariosInscritos() {
        return voluntariosInscritos;
    }

    public Set<UUID> getVoluntariosParticiparam() {
        return voluntariosParticiparam;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public String getImagemCapa() {
        return imagemCapa;
    }

    public void setImagemCapa(String imagemCapa) {
        this.imagemCapa = imagemCapa;
    }
}