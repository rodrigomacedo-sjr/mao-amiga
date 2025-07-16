package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Evento;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class EventoControllerTest {

    private final EventoController controller = new EventoController();

    @Test
    @DisplayName("Deve criar evento com dados válidos")
    void deveCriarEventoComDadosValidos() {
        Map<String, Object> dados = criarDadosEventoValidos();
        
        ResponseEntity<Evento> response = controller.criarEvento(dados);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Limpeza do Parque", response.getBody().getTitulo());
        assertEquals("Atividade de preservação ambiental", response.getBody().getDescricao());
        assertEquals(20, response.getBody().getVagasDisponiveis());
    }

    @Test
    @DisplayName("Deve buscar evento por ID existente")
    void deveBuscarEventoPorIdExistente() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        ResponseEntity<Evento> response = controller.buscarEvento(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar evento inexistente")
    void deveRetornar404AoBuscarEventoInexistente() {
        UUID idInexistente = UUID.randomUUID();
        
        ResponseEntity<Evento> response = controller.buscarEvento(idInexistente);
        
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve inscrever voluntário no evento")
    void deveInscreverVoluntarioNoEvento() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID idEvento = eventoCriado.getBody().getId();
        UUID idVoluntario = UUID.randomUUID();
        
        ResponseEntity<?> response = controller.inscreverVoluntario(idEvento, idVoluntario);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve cancelar inscrição de voluntário")
    void deveCancelarInscricaoVoluntario() {
        // Cria evento e inscreve voluntário
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID idEvento = eventoCriado.getBody().getId();
        UUID idVoluntario = UUID.randomUUID();
        controller.inscreverVoluntario(idEvento, idVoluntario);
        
        ResponseEntity<?> response = controller.cancelarInscricao(idEvento, idVoluntario);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve marcar presença de voluntário")
    void deveMarcarPresencaVoluntario() {
        // Cria evento e inscreve voluntário
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID idEvento = eventoCriado.getBody().getId();
        UUID idVoluntario = UUID.randomUUID();
        controller.inscreverVoluntario(idEvento, idVoluntario);
        
        ResponseEntity<?> response = controller.marcarPresenca(idEvento, idVoluntario);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve listar eventos futuros")
    void deveListarEventosFuturos() {
        // Cria evento futuro
        Map<String, Object> dados = criarDadosEventoValidos();
        controller.criarEvento(dados);
        
        ResponseEntity<?> response = controller.listarEventosFuturos();
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve buscar eventos por área de interesse")
    void deveBuscarEventosPorAreaInteresse() {
        // Cria evento e adiciona área
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        controller.adicionarAreaInteresse(id, "MEIO_AMBIENTE");
        
        ResponseEntity<?> response = controller.buscarEventosPorArea("MEIO_AMBIENTE");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve buscar eventos por ONG")
    void deveBuscarEventosPorOng() {
        UUID idOng = UUID.randomUUID();
        
        // Cria evento para a ONG
        Map<String, Object> dados = criarDadosEventoValidos();
        dados.put("ongResponsavelId", idOng.toString());
        controller.criarEvento(dados);
        
        ResponseEntity<?> response = controller.buscarEventosPorOng(idOng);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve atualizar evento")
    void deveAtualizarEvento() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        // Dados para atualização
        Map<String, Object> dadosAtualizacao = new HashMap<>();
        dadosAtualizacao.put("titulo", "Limpeza do Parque - Atualizado");
        dadosAtualizacao.put("vagasDisponiveis", 30);
        
        ResponseEntity<Evento> response = controller.atualizarEvento(id, dadosAtualizacao);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve obter voluntários inscritos")
    void deveObterVoluntariosInscritos() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.obterVoluntariosInscritos(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve obter estatísticas do evento")
    void deveObterEstatisticasEvento() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.obterEstatisticas(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve cancelar evento")
    void deveCancelarEvento() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.cancelarEvento(id);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve finalizar evento")
    void deveFinalizarEvento() {
        // Cria evento
        Map<String, Object> dados = criarDadosEventoValidos();
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID id = eventoCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.finalizarEvento(id);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve retornar erro ao criar evento com dados inválidos")
    void deveRetornarErroAoCriarEventoComDadosInvalidos() {
        Map<String, Object> dadosInvalidos = new HashMap<>();
        dadosInvalidos.put("titulo", ""); // Título vazio
        // Falta dados obrigatórios
        
        ResponseEntity<Evento> response = controller.criarEvento(dadosInvalidos);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Não deve inscrever voluntário em evento lotado")
    void naoDeveInscreverVoluntarioEmEventoLotado() {
        // Cria evento com 1 vaga só
        Map<String, Object> dados = criarDadosEventoValidos();
        dados.put("vagasDisponiveis", 1);
        ResponseEntity<Evento> eventoCriado = controller.criarEvento(dados);
        UUID idEvento = eventoCriado.getBody().getId();
        
        // Inscreve primeiro voluntário (ocupa a única vaga)
        controller.inscreverVoluntario(idEvento, UUID.randomUUID());
        
        // Tenta inscrever segundo voluntário
        ResponseEntity<?> response = controller.inscreverVoluntario(idEvento, UUID.randomUUID());
        
        assertEquals(400, response.getStatusCodeValue());
    }

    private Map<String, Object> criarDadosEventoValidos() {
        Map<String, Object> dados = new HashMap<>();
        dados.put("titulo", "Limpeza do Parque");
        dados.put("descricao", "Atividade de preservação ambiental");
        dados.put("dataHoraInicio", LocalDateTime.now().plusDays(7).toString());
        dados.put("dataHoraFim", LocalDateTime.now().plusDays(7).plusHours(4).toString());
        dados.put("ongResponsavelId", UUID.randomUUID().toString());
        dados.put("local", "Parque Central - Londrina");
        dados.put("vagasDisponiveis", 20);
        return dados;
    }
}
