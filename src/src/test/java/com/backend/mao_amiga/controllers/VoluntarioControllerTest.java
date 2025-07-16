package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Voluntario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
class VoluntarioControllerTest {

    private final VoluntarioController controller = new VoluntarioController();

    @Test
    @DisplayName("Deve criar voluntário com dados válidos")
    void deveCriarVoluntarioComDadosValidos() {
        Map<String, String> dados = criarDadosVoluntarioValidos();
        
        ResponseEntity<Voluntario> response = controller.criarVoluntario(dados);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("voluntario@teste.com", response.getBody().getEmail());
        assertEquals("João Silva", response.getBody().getNomeCompleto());
    }

    @Test
    @DisplayName("Deve buscar voluntário por ID existente")
    void deveBuscarVoluntarioPorIdExistente() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        ResponseEntity<Voluntario> response = controller.buscarVoluntario(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar voluntário inexistente")
    void deveRetornar404AoBuscarVoluntarioInexistente() {
        UUID idInexistente = UUID.randomUUID();
        
        ResponseEntity<Voluntario> response = controller.buscarVoluntario(idInexistente);
        
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve adicionar área de interesse")
    void deveAdicionarAreaInteresse() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.adicionarAreaInteresse(id, "EDUCACAO");
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve remover área de interesse")
    void deveRemoverAreaInteresse() {
        // Cria voluntário e adiciona área
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        controller.adicionarAreaInteresse(id, "EDUCACAO");
        
        ResponseEntity<?> response = controller.removerAreaInteresse(id, "EDUCACAO");
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve seguir uma ONG")
    void deveSeguirOng() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID idVoluntario = voluntarioCriado.getBody().getId();
        UUID idOng = UUID.randomUUID();
        
        ResponseEntity<?> response = controller.seguirOng(idVoluntario, idOng);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve deixar de seguir ONG")
    void deveDeixarDeSeguirOng() {
        // Cria voluntário e faz seguir ONG
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID idVoluntario = voluntarioCriado.getBody().getId();
        UUID idOng = UUID.randomUUID();
        controller.seguirOng(idVoluntario, idOng);
        
        ResponseEntity<?> response = controller.deixarDeSeguirOng(idVoluntario, idOng);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve favoritar evento")
    void deveFavoritarEvento() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID idVoluntario = voluntarioCriado.getBody().getId();
        UUID idEvento = UUID.randomUUID();
        
        ResponseEntity<?> response = controller.favoritarEvento(idVoluntario, idEvento);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve desfavoritar evento")
    void deveDesfavoritarEvento() {
        // Cria voluntário e favorita evento
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID idVoluntario = voluntarioCriado.getBody().getId();
        UUID idEvento = UUID.randomUUID();
        controller.favoritarEvento(idVoluntario, idEvento);
        
        ResponseEntity<?> response = controller.desfavoritarEvento(idVoluntario, idEvento);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve obter histórico de eventos")
    void deveObterHistoricoEventos() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.obterHistoricoEventos(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve obter ONGs seguidas")
    void deveObterOngsSeguidas() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.obterOngsSeguidas(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve atualizar perfil do voluntário")
    void deveAtualizarPerfilVoluntario() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        // Dados para atualização
        Map<String, String> dadosAtualizacao = new HashMap<>();
        dadosAtualizacao.put("maisSobre", "Voluntário dedicado e experiente");
        
        ResponseEntity<Voluntario> response = controller.atualizarVoluntario(id, dadosAtualizacao);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve buscar voluntários por área de interesse")
    void deveBuscarVoluntariosPorAreaInteresse() {
        // Cria voluntário e adiciona área
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        controller.adicionarAreaInteresse(id, "SAUDE");
        
        ResponseEntity<?> response = controller.buscarVoluntariosPorArea("SAUDE");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve obter estatísticas do voluntário")
    void deveObterEstatisticasVoluntario() {
        // Cria voluntário
        Map<String, String> dados = criarDadosVoluntarioValidos();
        ResponseEntity<Voluntario> voluntarioCriado = controller.criarVoluntario(dados);
        UUID id = voluntarioCriado.getBody().getId();
        
        ResponseEntity<?> response = controller.obterEstatisticas(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar erro ao criar voluntário com dados inválidos")
    void deveRetornarErroAoCriarVoluntarioComDadosInvalidos() {
        Map<String, String> dadosInvalidos = new HashMap<>();
        dadosInvalidos.put("email", "email-invalido");
        // Falta dados obrigatórios
        
        ResponseEntity<Voluntario> response = controller.criarVoluntario(dadosInvalidos);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    private Map<String, String> criarDadosVoluntarioValidos() {
        Map<String, String> dados = new HashMap<>();
        dados.put("email", "voluntario@teste.com");
        dados.put("senha", "senha123");
        dados.put("nomeCompleto", "João Silva");
        dados.put("maisSobre", "Voluntário entusiasta");
        return dados;
    }
}
