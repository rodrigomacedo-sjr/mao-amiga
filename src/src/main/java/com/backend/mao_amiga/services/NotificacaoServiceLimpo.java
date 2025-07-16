package com.backend.mao_amiga.services;

import com.backend.mao_amiga.models.Notificacao;
import com.backend.mao_amiga.models.enums.TipoNotificacao;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// Service para gerenciar notificações - implementação em memória
@Service
public class NotificacaoService {
    
    private final Map<UUID, Notificacao> notificacoes = new ConcurrentHashMap<>();

    // Cria notificação de avaliação recebida
    public Notificacao criarNotificacaoAvaliacao(UUID usuarioDestinoId, String tipoDestino, UUID avaliadorId) {
        validarParametros(usuarioDestinoId, avaliadorId);
        
        String mensagem = construirMensagemAvaliacao(tipoDestino);
        
        Notificacao notificacao = new Notificacao(
            TipoNotificacao.NOVA_AVALIACAO, 
            mensagem, 
            usuarioDestinoId
        );
        notificacao.setUsuarioOrigemId(avaliadorId);
        
        return salvarNotificacao(notificacao);
    }

    // Cria notificações para novo evento
    public List<Notificacao> criarNotificacoesNovoEvento(UUID eventoId, UUID ongId, List<UUID> seguidoresIds) {
        validarParametros(eventoId, ongId);
        if (seguidoresIds == null || seguidoresIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        String mensagem = "Uma ONG que você segue criou um novo evento!";
        List<Notificacao> notificacoesCriadas = new ArrayList<>();
        
        for (UUID seguidorId : seguidoresIds) {
            if (seguidorId != null) {
                Notificacao notificacao = new Notificacao(
                    TipoNotificacao.EVENTO_CRIADO,
                    mensagem,
                    seguidorId
                );
                notificacao.setUsuarioOrigemId(ongId);
                notificacao.setEventoRelacionadoId(eventoId);
                
                notificacoesCriadas.add(salvarNotificacao(notificacao));
            }
        }
        
        return notificacoesCriadas;
    }

    // Cria notificação de inscrição em evento
    public Notificacao criarNotificacaoInscricaoEvento(UUID voluntarioId, UUID ongId, UUID eventoId) {
        validarParametros(voluntarioId, ongId, eventoId);
        
        Notificacao notificacao = new Notificacao(
            TipoNotificacao.INSCRICAO_EVENTO,
            "Um voluntário se inscreveu no seu evento!",
            ongId
        );
        notificacao.setUsuarioOrigemId(voluntarioId);
        notificacao.setEventoRelacionadoId(eventoId);
        
        return salvarNotificacao(notificacao);
    }

    // Cria notificações para cancelamento de evento
    public List<Notificacao> criarNotificacoesCancelamentoEvento(UUID eventoId, List<UUID> voluntariosInscritos) {
        validarParametros(eventoId);
        if (voluntariosInscritos == null || voluntariosInscritos.isEmpty()) {
            return new ArrayList<>();
        }
        
        String mensagem = "Um evento em que você estava inscrito foi cancelado.";
        List<Notificacao> notificacoesCriadas = new ArrayList<>();
        
        for (UUID voluntarioId : voluntariosInscritos) {
            if (voluntarioId != null) {
                Notificacao notificacao = new Notificacao(
                    TipoNotificacao.EVENTO_CANCELADO,
                    mensagem,
                    voluntarioId
                );
                notificacao.setEventoRelacionadoId(eventoId);
                
                notificacoesCriadas.add(salvarNotificacao(notificacao));
            }
        }
        
        return notificacoesCriadas;
    }

    // Cria notificação de novo seguidor
    public Notificacao criarNotificacaoNovoSeguidor(UUID ongId, UUID voluntarioId) {
        validarParametros(ongId, voluntarioId);
        
        Notificacao notificacao = new Notificacao(
            TipoNotificacao.NOVO_SEGUIDOR,
            "Um novo voluntário começou a seguir sua ONG!",
            ongId
        );
        notificacao.setUsuarioOrigemId(voluntarioId);
        
        return salvarNotificacao(notificacao);
    }

    // Cria notificações para novo post
    public List<Notificacao> criarNotificacoesNovoPost(UUID postId, UUID autorId, List<UUID> seguidoresIds) {
        validarParametros(postId, autorId);
        if (seguidoresIds == null || seguidoresIds.isEmpty()) {
            return new ArrayList<>();
        }
        
        String mensagem = "Uma ONG que você segue fez uma nova publicação!";
        List<Notificacao> notificacoesCriadas = new ArrayList<>();
        
        for (UUID seguidorId : seguidoresIds) {
            if (seguidorId != null && !seguidorId.equals(autorId)) {
                Notificacao notificacao = new Notificacao(
                    TipoNotificacao.NOVO_POST,
                    mensagem,
                    seguidorId
                );
                notificacao.setUsuarioOrigemId(autorId);
                
                notificacoesCriadas.add(salvarNotificacao(notificacao));
            }
        }
        
        return notificacoesCriadas;
    }

    // Lista notificações não lidas de um usuário
    public List<Notificacao> buscarNotificacoesNaoLidas(UUID usuarioId) {
        validarParametros(usuarioId);
        
        return notificacoes.values().stream()
            .filter(notificacao -> notificacao.getUsuarioDestinoId().equals(usuarioId))
            .filter(notificacao -> !notificacao.isLida())
            .sorted((n1, n2) -> n2.getCriadaEm().compareTo(n1.getCriadaEm()))
            .collect(Collectors.toList());
    }

    // Lista todas as notificações de um usuário
    public List<Notificacao> buscarTodasNotificacoes(UUID usuarioId, int limite) {
        validarParametros(usuarioId);
        
        return notificacoes.values().stream()
            .filter(notificacao -> notificacao.getUsuarioDestinoId().equals(usuarioId))
            .sorted((n1, n2) -> n2.getCriadaEm().compareTo(n1.getCriadaEm()))
            .limit(Math.max(1, limite))
            .collect(Collectors.toList());
    }

    // Marca todas as notificações como lidas para um usuário
    public int marcarTodasComoLidas(UUID usuarioId) {
        validarParametros(usuarioId);
        
        List<Notificacao> notificacoesNaoLidas = buscarNotificacoesNaoLidas(usuarioId);
        notificacoesNaoLidas.forEach(Notificacao::marcarComoLida);
        
        return notificacoesNaoLidas.size();
    }

    // Marca uma notificação específica como lida
    public boolean marcarComoLida(UUID notificacaoId, UUID usuarioId) {
        validarParametros(notificacaoId, usuarioId);
        
        Notificacao notificacao = notificacoes.get(notificacaoId);
        if (notificacao == null) {
            return false;
        }
        
        if (!notificacao.getUsuarioDestinoId().equals(usuarioId)) {
            throw new IllegalArgumentException("Usuário não autorizado");
        }
        
        notificacao.marcarComoLida();
        return true;
    }

    // Remove notificações antigas (mais de 30 dias)
    public int limparNotificacoesAntigas() {
        LocalDateTime dataLimite = LocalDateTime.now().minusDays(30);
        
        List<UUID> idsParaRemover = notificacoes.values().stream()
            .filter(notificacao -> notificacao.isLida())
            .filter(notificacao -> notificacao.getCriadaEm().isBefore(dataLimite))
            .map(Notificacao::getId)
            .collect(Collectors.toList());
        
        idsParaRemover.forEach(notificacoes::remove);
        return idsParaRemover.size();
    }

    // Conta notificações não lidas de um usuário
    public long contarNotificacoesNaoLidas(UUID usuarioId) {
        validarParametros(usuarioId);
        
        return notificacoes.values().stream()
            .filter(notificacao -> notificacao.getUsuarioDestinoId().equals(usuarioId))
            .filter(notificacao -> !notificacao.isLida())
            .count();
    }

    // Busca uma notificação específica
    public Optional<Notificacao> buscarPorId(UUID notificacaoId) {
        validarParametros(notificacaoId);
        return Optional.ofNullable(notificacoes.get(notificacaoId));
    }

    // Remove uma notificação
    public boolean removerNotificacao(UUID notificacaoId, UUID usuarioId) {
        validarParametros(notificacaoId, usuarioId);
        
        Notificacao notificacao = notificacoes.get(notificacaoId);
        if (notificacao == null) {
            return false;
        }
        
        if (!notificacao.getUsuarioDestinoId().equals(usuarioId)) {
            throw new IllegalArgumentException("Usuário não autorizado");
        }
        
        notificacoes.remove(notificacaoId);
        return true;
    }

    // Obtém estatísticas das notificações
    public Map<String, Object> obterEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalNotificacoes = notificacoes.size();
        long totalNaoLidas = notificacoes.values().stream()
            .filter(notificacao -> !notificacao.isLida())
            .count();
        
        Map<TipoNotificacao, Long> porTipo = notificacoes.values().stream()
            .collect(Collectors.groupingBy(
                Notificacao::getTipo,
                Collectors.counting()
            ));
        
        stats.put("totalNotificacoes", totalNotificacoes);
        stats.put("totalNaoLidas", totalNaoLidas);
        stats.put("totalLidas", totalNotificacoes - totalNaoLidas);
        stats.put("porTipo", porTipo);
        
        return stats;
    }

    // Métodos auxiliares privados
    
    private Notificacao salvarNotificacao(Notificacao notificacao) {
        notificacoes.put(notificacao.getId(), notificacao);
        return notificacao;
    }

    private String construirMensagemAvaliacao(String tipoDestino) {
        return "ONG".equalsIgnoreCase(tipoDestino) ? 
            "Você recebeu uma nova avaliação de um voluntário!" :
            "Você recebeu uma nova avaliação de uma ONG!";
    }

    private void validarParametros(UUID... ids) {
        for (UUID id : ids) {
            if (id == null) {
                throw new IllegalArgumentException("ID não pode ser nulo");
            }
        }
    }
}
