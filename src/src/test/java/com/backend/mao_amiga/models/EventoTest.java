package com.backend.mao_amiga.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import com.backend.mao_amiga.models.enums.AreaInteresse;
import com.backend.mao_amiga.models.enums.StatusEvento;
import java.time.LocalDateTime;
import java.util.UUID;

class EventoTest {

    private Evento evento;
    private final String TITULO_VALIDO = "Limpeza do Parque Central";
    private final String DESCRICAO_VALIDA = "Atividade de limpeza e preservação ambiental";
    private final LocalDateTime DATA_INICIO = LocalDateTime.now().plusDays(7);
    private final LocalDateTime DATA_FIM = DATA_INICIO.plusHours(4);
    private final UUID ONG_ID = UUID.randomUUID();
    private final String LOCAL_VALIDO = "Parque Central - Londrina";
    private final int VAGAS_TOTAIS = 20;

    @BeforeEach
    void setUp() {
        evento = new Evento(
            TITULO_VALIDO,
            DESCRICAO_VALIDA,
            DATA_INICIO,
            DATA_FIM,
            ONG_ID,
            LOCAL_VALIDO,
            VAGAS_TOTAIS
        );
    }

    @Test
    @DisplayName("Deve criar evento com dados básicos válidos")
    void deveCriarEventoComDadosBasicos() {
        assertNotNull(evento);
        assertNotNull(evento.getId());
        assertEquals(TITULO_VALIDO, evento.getTitulo());
        assertEquals(DESCRICAO_VALIDA, evento.getDescricao());
        assertEquals(DATA_INICIO, evento.getDataHoraInicio());
        assertEquals(DATA_FIM, evento.getDataHoraFim());
        assertEquals(ONG_ID, evento.getOngResponsavelId());
        assertEquals(LOCAL_VALIDO, evento.getLocal());
        assertEquals(VAGAS_TOTAIS, evento.getVagasDisponiveis());
        assertEquals(0, evento.getVagasOcupadas());
        assertEquals(StatusEvento.PLANEJADO, evento.getStatus());
    }

    @Test
    @DisplayName("Deve adicionar área de interesse ao evento")
    void deveAdicionarAreaDeInteresse() {
        evento.adicionarAreaDeInteresse(AreaInteresse.MEIO_AMBIENTE);
        
        assertTrue(evento.getAreasDeInteresse().contains(AreaInteresse.MEIO_AMBIENTE));
        assertEquals(1, evento.getAreasDeInteresse().size());
    }

    @Test
    @DisplayName("Deve inscrever voluntário no evento")
    void deveInscreverVoluntario() {
        UUID idVoluntario = UUID.randomUUID();
        
        boolean sucesso = evento.inscreverVoluntario(idVoluntario);
        
        assertTrue(sucesso);
        assertTrue(evento.getVoluntariosInscritos().contains(idVoluntario));
        assertEquals(1, evento.getVagasOcupadas());
        assertEquals(VAGAS_TOTAIS - 1, evento.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Não deve inscrever voluntário quando não há vagas")
    void naoDeveInscreverQuandoNaoHaVagas() {
        // Preenche todas as vagas
        for (int i = 0; i < VAGAS_TOTAIS; i++) {
            evento.inscreverVoluntario(UUID.randomUUID());
        }
        
        UUID novoVoluntario = UUID.randomUUID();
        boolean sucesso = evento.inscreverVoluntario(novoVoluntario);
        
        assertFalse(sucesso);
        assertFalse(evento.getVoluntariosInscritos().contains(novoVoluntario));
        assertEquals(VAGAS_TOTAIS, evento.getVagasOcupadas());
        assertEquals(0, evento.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Deve cancelar inscrição de voluntário")
    void deveCancelarInscricaoVoluntario() {
        UUID idVoluntario = UUID.randomUUID();
        evento.inscreverVoluntario(idVoluntario);
        
        boolean sucesso = evento.cancelarInscricaoVoluntario(idVoluntario);
        
        assertTrue(sucesso);
        assertFalse(evento.getVoluntariosInscritos().contains(idVoluntario));
        assertEquals(0, evento.getVagasOcupadas());
        assertEquals(VAGAS_TOTAIS, evento.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Deve marcar presença de voluntário")
    void deveMarcarPresencaVoluntario() {
        UUID idVoluntario = UUID.randomUUID();
        evento.inscreverVoluntario(idVoluntario);
        
        boolean sucesso = evento.marcarPresenca(idVoluntario);
        
        assertTrue(sucesso);
        assertTrue(evento.getVoluntariosPresentes().contains(idVoluntario));
    }

    @Test
    @DisplayName("Não deve marcar presença de voluntário não inscrito")
    void naoDeveMarcarPresencaVoluntarioNaoInscrito() {
        UUID idVoluntario = UUID.randomUUID();
        
        boolean sucesso = evento.marcarPresenca(idVoluntario);
        
        assertFalse(sucesso);
        assertFalse(evento.getVoluntariosPresentes().contains(idVoluntario));
    }

    @Test
    @DisplayName("Deve verificar se evento está no futuro")
    void deveVerificarSeEventoEstNoFuturo() {
        assertTrue(evento.estaNoFuturo());
    }

    @Test
    @DisplayName("Deve verificar se evento está em andamento")
    void deveVerificarSeEventoEstaEmAndamento() {
        // Simula evento que começou há 1 hora e termina em 3 horas
        Evento eventoEmAndamento = new Evento(
            "Evento em Andamento",
            "Descrição",
            LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(3),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        assertTrue(eventoEmAndamento.estaEmAndamento());
        assertFalse(evento.estaEmAndamento());
    }

    @Test
    @DisplayName("Deve verificar se evento está finalizado")
    void deveVerificarSeEventoEstaFinalizado() {
        // Simula evento que terminou há 1 hora
        Evento eventoFinalizado = new Evento(
            "Evento Finalizado",
            "Descrição",
            LocalDateTime.now().minusHours(5),
            LocalDateTime.now().minusHours(1),
            UUID.randomUUID(),
            "Local",
            10
        );
        
        assertTrue(eventoFinalizado.estaFinalizado());
        assertFalse(evento.estaFinalizado());
    }

    @Test
    @DisplayName("Deve alterar status do evento")
    void deveAlterarStatusEvento() {
        evento.setStatus(StatusEvento.EM_ANDAMENTO);
        assertEquals(StatusEvento.EM_ANDAMENTO, evento.getStatus());
        
        evento.setStatus(StatusEvento.FINALIZADO);
        assertEquals(StatusEvento.FINALIZADO, evento.getStatus());
    }

    @Test
    @DisplayName("Deve verificar se voluntário está inscrito")
    void deveVerificarSeVoluntarioEstaInscrito() {
        UUID idVoluntario = UUID.randomUUID();
        
        assertFalse(evento.voluntarioEstaInscrito(idVoluntario));
        
        evento.inscreverVoluntario(idVoluntario);
        
        assertTrue(evento.voluntarioEstaInscrito(idVoluntario));
    }

    @Test
    @DisplayName("Deve calcular taxa de ocupação corretamente")
    void deveCalcularTaxaOcupacao() {
        assertEquals(0.0, evento.getTaxaOcupacao(), 0.01);
        
        // Inscreve 10 voluntários (50% das vagas)
        for (int i = 0; i < 10; i++) {
            evento.inscreverVoluntario(UUID.randomUUID());
        }
        
        assertEquals(0.5, evento.getTaxaOcupacao(), 0.01);
    }

    @Test
    @DisplayName("Não deve permitir data de fim anterior à data de início")
    void naoDevePermitirDataFimAnterior() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Evento(
                "Título",
                "Descrição",
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now(), // Data fim anterior ao início
                UUID.randomUUID(),
                "Local",
                10
            );
        });
    }
}
