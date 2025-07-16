package com.backend.mao_amiga.controllers;

import com.backend.mao_amiga.models.Ong;
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
class OngControllerTest {

    private final OngController controller = new OngController();

    @Test
    @DisplayName("Deve criar ONG com dados válidos")
    void deveCriarOngComDadosValidos() {
        Map<String, String> dados = criarDadosOngValidos();
        
        ResponseEntity<Ong> response = controller.criarOng(dados);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("ong@teste.com", response.getBody().getEmail());
        assertEquals("ONG Teste", response.getBody().getNomeCompleto());
        assertEquals("12.345.678/0001-90", response.getBody().getCnpj());
    }

    @Test
    @DisplayName("Deve buscar ONG por ID existente")
    void deveBuscarOngPorIdExistente() {
        // Primeiro cria uma ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        
        ResponseEntity<Ong> response = controller.buscarOng(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(id, response.getBody().getId());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar ONG inexistente")
    void deveRetornar404AoBuscarOngInexistente() {
        UUID idInexistente = UUID.randomUUID();
        
        ResponseEntity<Ong> response = controller.buscarOng(idInexistente);
        
        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve listar todas as ONGs")
    void deveListarTodasAsOngs() {
        // Cria duas ONGs
        controller.criarOng(criarDadosOngValidos());
        
        Map<String, String> segundaOng = criarDadosOngValidos();
        segundaOng.put("email", "segunda@ong.com");
        segundaOng.put("cnpj", "98.765.432/0001-10");
        controller.criarOng(segundaOng);
        
        ResponseEntity<?> response = controller.listarOngs();
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve atualizar dados da ONG")
    void deveAtualizarDadosDaOng() {
        // Cria ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        
        // Dados para atualização
        Map<String, String> dadosAtualizacao = new HashMap<>();
        dadosAtualizacao.put("endereco", "Nova Rua, 456");
        dadosAtualizacao.put("telefone", "(43) 88888-8888");
        
        ResponseEntity<Ong> response = controller.atualizarOng(id, dadosAtualizacao);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Nova Rua, 456", response.getBody().getEndereco());
        assertEquals("(43) 88888-8888", response.getBody().getTelefone());
    }

    @Test
    @DisplayName("Deve adicionar área de atuação à ONG")
    void deveAdicionarAreaAtuacao() {
        // Cria ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        
        ResponseEntity<?> response = controller.adicionarAreaAtuacao(id, "EDUCACAO");
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve adicionar seguidor à ONG")
    void deveAdicionarSeguidorOng() {
        // Cria ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID idOng = ongCriada.getBody().getId();
        UUID idVoluntario = UUID.randomUUID();
        
        ResponseEntity<?> response = controller.adicionarSeguidor(idOng, idVoluntario);
        
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    @DisplayName("Deve retornar erro ao criar ONG com dados inválidos")
    void deveRetornarErroAoCriarOngComDadosInvalidos() {
        Map<String, String> dadosInvalidos = new HashMap<>();
        dadosInvalidos.put("email", "email-invalido");
        // Falta dados obrigatórios
        
        ResponseEntity<Ong> response = controller.criarOng(dadosInvalidos);
        
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    @Test
    @DisplayName("Deve buscar ONGs por área de atuação")
    void deveBuscarOngsPorAreaAtuacao() {
        // Cria ONG e adiciona área
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        controller.adicionarAreaAtuacao(id, "EDUCACAO");
        
        ResponseEntity<?> response = controller.buscarOngsPorArea("EDUCACAO");
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve verificar ONG")
    void deveVerificarOng() {
        // Cria ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        
        ResponseEntity<Ong> response = controller.verificarOng(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getVerificada());
    }

    @Test
    @DisplayName("Deve obter estatísticas da ONG")
    void deveObterEstatisticasOng() {
        // Cria ONG
        Map<String, String> dados = criarDadosOngValidos();
        ResponseEntity<Ong> ongCriada = controller.criarOng(dados);
        UUID id = ongCriada.getBody().getId();
        
        ResponseEntity<?> response = controller.obterEstatisticas(id);
        
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
    }

    private Map<String, String> criarDadosOngValidos() {
        Map<String, String> dados = new HashMap<>();
        dados.put("email", "ong@teste.com");
        dados.put("senha", "senha123");
        dados.put("nomeCompleto", "ONG Teste");
        dados.put("cnpj", "12.345.678/0001-90");
        dados.put("endereco", "Rua Teste, 123");
        dados.put("telefone", "(43) 99999-9999");
        dados.put("maisSobre", "ONG dedicada a ajudar a comunidade");
        return dados;
    }
}
