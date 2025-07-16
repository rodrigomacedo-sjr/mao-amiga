package com.backend.mao_amiga.services;

import com.backend.mao_amiga.models.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AvaliacaoService {
    
    // Simulação de banco em memória para testes
    private final Map<UUID, Avaliacao> avaliacoes = new HashMap<>();
    private final Map<UUID, Evento> eventos = new HashMap<>();
    private final Map<UUID, Usuario> usuarios = new HashMap<>();

    public Avaliacao voluntarioAvaliarOng(UUID voluntarioId, UUID ongId, UUID eventoId, 
                                        Float nota, String comentario) {
        
        validarDados(nota);
        validarParticipacao(voluntarioId, eventoId);
        validarOrganizacao(ongId, eventoId);
        validarAvaliacaoUnica(voluntarioId, ongId, eventoId);
        
        Avaliacao avaliacao = Avaliacao.criarAvaliacaoVoluntarioParaOng(
            voluntarioId, ongId, nota, comentario, eventoId
        );
        
        return salvarAvaliacao(avaliacao);
    }

    public Avaliacao ongAvaliarVoluntario(UUID ongId, UUID voluntarioId, UUID eventoId, 
                                        Float nota, String comentario) {
        
        validarDados(nota);
        validarOrganizacao(ongId, eventoId);
        validarParticipacao(voluntarioId, eventoId);
        validarAvaliacaoUnica(ongId, voluntarioId, eventoId);
        
        Avaliacao avaliacao = Avaliacao.criarAvaliacaoOngParaVoluntario(
            ongId, voluntarioId, nota, comentario, eventoId
        );
        
        return salvarAvaliacao(avaliacao);
    }

    public void removerAvaliacao(UUID avaliacaoId, UUID usuarioId) {
        Avaliacao avaliacao = buscarAvaliacao(avaliacaoId);
        
        if (!avaliacao.getAvaliadorId().equals(usuarioId)) {
            throw new IllegalArgumentException("Apenas o autor pode remover a avaliação");
        }
        
        avaliacao.desativarAvaliacao();
    }

    public List<Avaliacao> listarAvaliacoesRecebidas(UUID usuarioId, String tipoUsuario) {
        return avaliacoes.values().stream()
            .filter(a -> a.getAvaliadoId().equals(usuarioId))
            .filter(a -> a.getAtiva())
            .toList();
    }

    public List<Avaliacao> listarAvaliacoesFeitas(UUID usuarioId, String tipoUsuario) {
        return avaliacoes.values().stream()
            .filter(a -> a.getAvaliadorId().equals(usuarioId))
            .filter(a -> a.getAtiva())
            .toList();
    }

    public Float calcularNotaMedia(UUID usuarioId, String tipoUsuario) {
        List<Avaliacao> avaliacoesRecebidas = listarAvaliacoesRecebidas(usuarioId, tipoUsuario);
        
        if (avaliacoesRecebidas.isEmpty()) {
            return 0.0f;
        }
        
        double soma = avaliacoesRecebidas.stream()
            .mapToDouble(Avaliacao::getNota)
            .sum();
            
        return (float) (soma / avaliacoesRecebidas.size());
    }

    // Métodos auxiliares
    private void validarDados(Float nota) {
        if (nota == null || nota < 1.0f || nota > 5.0f) {
            throw new IllegalArgumentException("Nota deve estar entre 1.0 e 5.0");
        }
    }

    private void validarParticipacao(UUID voluntarioId, UUID eventoId) {
        Evento evento = eventos.get(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        
        if (!evento.voluntarioParticipou(voluntarioId)) {
            throw new IllegalArgumentException("Voluntário não participou deste evento");
        }
    }

    private void validarOrganizacao(UUID ongId, UUID eventoId) {
        Evento evento = eventos.get(eventoId);
        if (evento == null) {
            throw new IllegalArgumentException("Evento não encontrado");
        }
        
        if (!evento.getOngResponsavelId().equals(ongId)) {
            throw new IllegalArgumentException("ONG não organizou este evento");
        }
    }

    private void validarAvaliacaoUnica(UUID avaliadorId, UUID avaliadoId, UUID eventoId) {
        boolean jaAvaliou = avaliacoes.values().stream()
            .anyMatch(a -> a.getAvaliadorId().equals(avaliadorId) 
                        && a.getAvaliadoId().equals(avaliadoId)
                        && a.getEventoRelacionadoId().equals(eventoId)
                        && a.getAtiva());
                        
        if (jaAvaliou) {
            throw new IllegalArgumentException("Você já avaliou este usuário para este evento");
        }
    }

    private Avaliacao salvarAvaliacao(Avaliacao avaliacao) {
        UUID id = UUID.randomUUID();
        avaliacoes.put(id, avaliacao);
        return avaliacao;
    }

    private Avaliacao buscarAvaliacao(UUID avaliacaoId) {
        Avaliacao avaliacao = avaliacoes.get(avaliacaoId);
        if (avaliacao == null) {
            throw new IllegalArgumentException("Avaliação não encontrada");
        }
        return avaliacao;
    }

    // Métodos para testes - adicionar dados mock
    public void adicionarEvento(Evento evento) {
        eventos.put(evento.getId(), evento);
    }

    public void adicionarUsuario(Usuario usuario) {
        usuarios.put(usuario.getId(), usuario);
    }

    public Map<UUID, Avaliacao> getAvaliacoes() {
        return new HashMap<>(avaliacoes);
    }
}
