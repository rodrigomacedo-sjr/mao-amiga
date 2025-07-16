package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.*;
import com.backend.mao_amiga.models.enums.AreaInteresse;
import com.backend.mao_amiga.models.enums.PreferenciaTema;
import com.backend.mao_amiga.services.AvaliacaoService;
import com.backend.mao_amiga.services.NotificacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ⚠️  CONTROLLER DE TESTES - PODE SER REMOVIDO ⚠️
 * 
 * Este controller foi usado apenas para testes e demonstrações.
 * Para a API funcional, use os novos controllers criados:
 * - VoluntarioController
 * - OngController  
 * - EventoController
 * - PostController
 * - ComentarioController
 * - NotificacaoController
 * - AvaliacaoController
 * - SistemaController
 * - ExemploController (para referência)
 * 
 * Consulte a documentação em: API_CONTROLLERS_DOCUMENTATION.md
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private AvaliacaoService avaliacaoService;
    
    @Autowired
    private NotificacaoService notificacaoService;

    @GetMapping("/hello")
    public String hello() {
        return "Mão Amiga API está funcionando! 🤝";
    }

    @GetMapping("/voluntario")
    public Voluntario criarVoluntarioTeste() {
        Voluntario voluntario = new Voluntario(
            "joao@email.com", 
            "senha123", 
            "João Silva"
        );
        
        voluntario.setFotoDePerfil("https://exemplo.com/foto.jpg");
        voluntario.setMaisSobre("Gosto de ajudar pessoas e animais!");
        voluntario.adicionarAreaInteresse(AreaInteresse.MEIO_AMBIENTE);
        voluntario.adicionarAreaInteresse(AreaInteresse.ANIMAIS);
        
        return voluntario;
    }

    @GetMapping("/ong")
    public Ong criarOngTeste() {
        Ong ong = new Ong(
            "contato@ongamiga.org",
            "senha456",
            "ONG Amiga",
            "12.345.678/0001-90"
        );
        
        ong.setEndereco("Rua das Flores, 123 - São Paulo/SP");
        ong.setTelefone("(11) 99999-9999");
        ong.setMaisSobre("Trabalhamos com proteção ambiental e animal");
        ong.adicionarAreaAtuacao(AreaInteresse.MEIO_AMBIENTE);
        ong.adicionarAreaAtuacao(AreaInteresse.ANIMAIS);
        
        return ong;
    }

    @GetMapping("/evento")
    public Evento criarEventoTeste() {
        UUID ongId = UUID.randomUUID();
        
        Evento evento = new Evento(
            "Limpeza da Praia",
            "Mutirão de limpeza da praia de Copacabana",
            LocalDateTime.now().plusDays(7),
            LocalDateTime.now().plusDays(7).plusHours(4),
            ongId,
            "Praia de Copacabana - Rio de Janeiro/RJ",
            50
        );
        
        evento.adicionarAreaRelacionada(AreaInteresse.MEIO_AMBIENTE);
        evento.abrirInscricoes();
        
        // Adicionar evento ao service para testes
        avaliacaoService.adicionarEvento(evento);
        
        return evento;
    }

    @GetMapping("/avaliacao")
    public Avaliacao criarAvaliacaoTeste() {
        UUID voluntarioId = UUID.randomUUID();
        UUID ongId = UUID.randomUUID();
        UUID eventoId = UUID.randomUUID();
        
        // Criar evento mock
        Evento evento = new Evento(
            "Evento Teste",
            "Evento para teste de avaliação",
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().minusHours(20),
            ongId,
            "Local Teste",
            10
        );
        
        // Simular participação
        evento.inscreverVoluntario(voluntarioId);
        evento.confirmarParticipacao(voluntarioId);
        
        // Adicionar ao service
        avaliacaoService.adicionarEvento(evento);
        
        // Criar avaliação
        return avaliacaoService.voluntarioAvaliarOng(
            voluntarioId,
            ongId,
            eventoId,
            4.5f,
            "Evento muito bem organizado! Parabéns!"
        );
    }

    @GetMapping("/avaliacao/media/{usuarioId}")
    public Float calcularNotaMedia(@PathVariable UUID usuarioId) {
        return avaliacaoService.calcularNotaMedia(usuarioId, "ONG");
    }

    @GetMapping("/avaliacoes/{usuarioId}")
    public List<Avaliacao> listarAvaliacoes(@PathVariable UUID usuarioId) {
        return avaliacaoService.listarAvaliacoesRecebidas(usuarioId, "ONG");
    }

    @GetMapping("/post")
    public Post criarPostTeste() {
        UUID ongId = UUID.randomUUID();
        
        Post post = Post.criarPostOng(
            ongId,
            "Acabamos de plantar 100 mudas de árvores nativas! 🌱🌳 " +
            "Obrigado a todos os voluntários que participaram!"
        );
        
        // Simular algumas curtidas
        post.adicionarCurtida(UUID.randomUUID());
        post.adicionarCurtida(UUID.randomUUID());
        post.adicionarCurtida(UUID.randomUUID());
        
        return post;
    }

    @GetMapping("/stats")
    public String estatisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("📊 Estatísticas do Sistema:\\n\\n");
        stats.append("🟢 Áreas de Interesse disponíveis: ").append(AreaInteresse.values().length).append("\\n");
        stats.append("🟢 Status de Evento possíveis: ").append(com.backend.mao_amiga.models.enums.StatusEvento.values().length).append("\\n");
        stats.append("🟢 Tipos de Notificação: ").append(com.backend.mao_amiga.models.enums.TipoNotificacao.values().length).append("\\n");
        stats.append("🟢 Temas disponíveis: ").append(PreferenciaTema.values().length).append("\\n");
        stats.append("🟢 Avaliações em memória: ").append(avaliacaoService.getAvaliacoes().size()).append("\\n\\n");
        stats.append("✅ Todas as entidades foram criadas com sucesso!");
        
        return stats.toString();
    }

    @GetMapping("/areas")
    public List<String> listarAreas() {
        List<String> areas = new ArrayList<>();
        for (AreaInteresse area : AreaInteresse.values()) {
            areas.add(area.name() + " - " + area.getDescricao());
        }
        return areas;
    }

    // === ENDPOINTS DE NOTIFICAÇÕES ===

    @GetMapping("/notificacao/avaliacao")
    public Notificacao criarNotificacaoAvaliacaoTeste() {
        UUID ongId = UUID.randomUUID();
        UUID voluntarioId = UUID.randomUUID();
        
        return notificacaoService.criarNotificacaoAvaliacao(
            ongId, "ONG", voluntarioId
        );
    }

    @GetMapping("/notificacao/evento")
    public List<Notificacao> criarNotificacoesEventoTeste() {
        UUID eventoId = UUID.randomUUID();
        UUID ongId = UUID.randomUUID();
        List<UUID> seguidores = List.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        
        return notificacaoService.criarNotificacoesNovoEvento(
            eventoId, ongId, seguidores
        );
    }

    @GetMapping("/notificacao/seguidor")
    public Notificacao criarNotificacaoSeguidorTeste() {
        UUID ongId = UUID.randomUUID();
        UUID voluntarioId = UUID.randomUUID();
        
        return notificacaoService.criarNotificacaoNovoSeguidor(ongId, voluntarioId);
    }

    @GetMapping("/notificacoes/{usuarioId}")
    public List<Notificacao> listarNotificacoesUsuario(@PathVariable UUID usuarioId) {
        return notificacaoService.buscarTodasNotificacoes(usuarioId, 10);
    }

    @GetMapping("/notificacoes/{usuarioId}/nao-lidas")
    public List<Notificacao> listarNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        return notificacaoService.buscarNotificacoesNaoLidas(usuarioId);
    }

    @PostMapping("/notificacoes/{notificacaoId}/marcar-lida/{usuarioId}")
    public String marcarNotificacaoComoLida(
        @PathVariable UUID notificacaoId,
        @PathVariable UUID usuarioId
    ) {
        boolean sucesso = notificacaoService.marcarComoLida(notificacaoId, usuarioId);
        return sucesso ? "✅ Notificação marcada como lida!" : "❌ Notificação não encontrada";
    }

    @PostMapping("/notificacoes/{usuarioId}/marcar-todas-lidas")
    public String marcarTodasNotificacoesComoLidas(@PathVariable UUID usuarioId) {
        int quantidade = notificacaoService.marcarTodasComoLidas(usuarioId);
        return String.format("✅ %d notificações marcadas como lidas!", quantidade);
    }

    @GetMapping("/notificacoes/stats")
    public Map<String, Object> estatisticasNotificacoes() {
        return notificacaoService.obterEstatisticas();
    }

    @GetMapping("/fluxo-notificacoes")
    public String demonstrarFluxoNotificacoes() {
        try {
            // 1. Criar IDs de teste
            UUID ongId = UUID.randomUUID();
            UUID voluntarioId1 = UUID.randomUUID();
            UUID voluntarioId2 = UUID.randomUUID();
            UUID eventoId = UUID.randomUUID();
            
            // 2. Simular novo seguidor
            Notificacao notifSeguidor = notificacaoService.criarNotificacaoNovoSeguidor(ongId, voluntarioId1);
            
            // 3. Simular novo evento para seguidores
            List<UUID> seguidores = List.of(voluntarioId1, voluntarioId2);
            List<Notificacao> notifsEvento = notificacaoService.criarNotificacoesNovoEvento(
                eventoId, ongId, seguidores
            );
            
            // 4. Simular inscrição no evento
            Notificacao notifInscricao = notificacaoService.criarNotificacaoInscricaoEvento(
                voluntarioId1, ongId, eventoId
            );
            
            // 5. Simular avaliação
            Notificacao notifAvaliacao = notificacaoService.criarNotificacaoAvaliacao(
                ongId, "ONG", voluntarioId1
            );
            
            // 6. Obter estatísticas
            Map<String, Object> stats = notificacaoService.obterEstatisticas();
            long totalNaoLidas = notificacaoService.contarNotificacoesNaoLidas(ongId);
            
            return String.format(
                "✅ Fluxo de Notificações Executado!\\n\\n" +
                "📧 Notificações criadas:\\n" +
                "• Novo seguidor: %s\\n" +
                "• Novo evento: %d notificações\\n" +
                "• Inscrição evento: %s\\n" +
                "• Nova avaliação: %s\\n\\n" +
                "📊 Estatísticas gerais:\\n" +
                "• Total: %s\\n" +
                "• Não lidas: %s\\n" +
                "• Não lidas da ONG: %d\\n\\n" +
                "💡 Teste os endpoints:\\n" +
                "📍 /api/test/notificacoes/%s (todas da ONG)\\n" +
                "📍 /api/test/notificacoes/%s/nao-lidas\\n" +
                "📍 /api/test/notificacoes/stats",
                notifSeguidor.getId().toString().substring(0, 8) + "...",
                notifsEvento.size(),
                notifInscricao.getId().toString().substring(0, 8) + "...",
                notifAvaliacao.getId().toString().substring(0, 8) + "...",
                stats.get("totalNotificacoes"),
                stats.get("totalNaoLidas"),
                totalNaoLidas,
                ongId.toString().substring(0, 8) + "...",
                ongId.toString().substring(0, 8) + "..."
            );
            
        } catch (Exception e) {
            return "❌ Erro: " + e.getMessage();
        }
    }

    @GetMapping("/fluxo-completo")
    public String demonstrarFluxoCompleto() {
        try {
            // 1. Criar IDs
            UUID ongId = UUID.randomUUID();
            UUID voluntarioId = UUID.randomUUID();
            UUID eventoId = UUID.randomUUID();
            
            // 2. Criar evento
            Evento evento = new Evento(
                "Plantio de Árvores",
                "Evento de reflorestamento urbano",
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().minusHours(20),
                ongId,
                "Parque da Cidade",
                20
            );
            
            // 3. Simular participação
            evento.abrirInscricoes();
            evento.inscreverVoluntario(voluntarioId);
            
            // 4. Notificar inscrição
            Notificacao notifInscricao = notificacaoService.criarNotificacaoInscricaoEvento(
                voluntarioId, ongId, eventoId
            );
            
            evento.iniciarEvento();
            evento.confirmarParticipacao(voluntarioId);
            evento.finalizarEvento();
            
            // 5. Adicionar ao service
            avaliacaoService.adicionarEvento(evento);
            
            // 6. Criar avaliações
            Avaliacao avaliacaoVoluntario = avaliacaoService.voluntarioAvaliarOng(
                voluntarioId, ongId, eventoId, 4.8f, "Evento excelente! Muito bem organizado."
            );
            
            // 7. Notificar avaliação
            Notificacao notifAvaliacao = notificacaoService.criarNotificacaoAvaliacao(
                ongId, "ONG", voluntarioId
            );
            
            Avaliacao avaliacaoOng = avaliacaoService.ongAvaliarVoluntario(
                ongId, voluntarioId, eventoId, 4.5f, "Voluntário dedicado e pontual!"
            );
            
            // 8. Calcular estatísticas
            Float notaMediaOng = avaliacaoService.calcularNotaMedia(ongId, "ONG");
            Float notaMediaVoluntario = avaliacaoService.calcularNotaMedia(voluntarioId, "VOLUNTARIO");
            long notificacoesNaoLidas = notificacaoService.contarNotificacoesNaoLidas(ongId);
            
            return String.format(
                "✅ Fluxo Completo Executado!\\n\\n" +
                "🎯 Evento: %s\\n" +
                "👥 Participantes: 1 voluntário\\n" +
                "⭐ Avaliação da ONG: %.1f/5.0\\n" +
                "⭐ Avaliação do Voluntário: %.1f/5.0\\n" +
                "📧 Notificações não lidas da ONG: %d\\n" +
                "📊 Total de avaliações: %d\\n\\n" +
                "💡 Endpoints importantes:\\n" +
                "📍 /api/test/avaliacoes/%s\\n" +
                "📍 /api/test/notificacoes/%s/nao-lidas\\n" +
                "📍 /api/test/stats | /api/test/notificacoes/stats",
                evento.getTitulo(),
                avaliacaoVoluntario.getNota(),
                avaliacaoOng.getNota(),
                notificacoesNaoLidas,
                avaliacaoService.getAvaliacoes().size(),
                ongId.toString().substring(0, 8) + "...",
                ongId.toString().substring(0, 8) + "..."
            );
            
        } catch (Exception e) {
            return "❌ Erro: " + e.getMessage();
        }
    }
}
