package com.backend.mao_amiga.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import com.backend.mao_amiga.models.enums.AreaInteresse;
import java.util.UUID;

class OngTest {

    private Ong ong;
    private final String EMAIL_VALIDO = "ong@exemplo.com";
    private final String SENHA_VALIDA = "senha123";
    private final String NOME_VALIDO = "ONG Ajuda Mútua";
    private final String CNPJ_VALIDO = "12.345.678/0001-90";

    @BeforeEach
    void setUp() {
        ong = new Ong(EMAIL_VALIDO, SENHA_VALIDA, NOME_VALIDO, CNPJ_VALIDO);
    }

    @Test
    @DisplayName("Deve criar ONG com dados básicos válidos")
    void deveCriarOngComDadosBasicos() {
        assertNotNull(ong);
        assertNotNull(ong.getId());
        assertEquals(EMAIL_VALIDO, ong.getEmail());
        assertEquals(NOME_VALIDO, ong.getNomeCompleto());
        assertEquals(CNPJ_VALIDO, ong.getCnpj());
        assertFalse(ong.getVerificada());
    }

    @Test
    @DisplayName("Deve adicionar área de atuação à ONG")
    void deveAdicionarAreaDeAtuacao() {
        ong.adicionarAreaDeAtuacao(AreaInteresse.EDUCACAO);
        
        assertTrue(ong.getAreasDeAtuacao().contains(AreaInteresse.EDUCACAO));
        assertEquals(1, ong.getAreasDeAtuacao().size());
    }

    @Test
    @DisplayName("Deve remover área de atuação da ONG")
    void deveRemoverAreaDeAtuacao() {
        ong.adicionarAreaDeAtuacao(AreaInteresse.EDUCACAO);
        ong.adicionarAreaDeAtuacao(AreaInteresse.SAUDE);
        
        ong.removerAreaDeAtuacao(AreaInteresse.EDUCACAO);
        
        assertFalse(ong.getAreasDeAtuacao().contains(AreaInteresse.EDUCACAO));
        assertTrue(ong.getAreasDeAtuacao().contains(AreaInteresse.SAUDE));
        assertEquals(1, ong.getAreasDeAtuacao().size());
    }

    @Test
    @DisplayName("Deve adicionar seguidor à ONG")
    void deveAdicionarSeguidor() {
        UUID idVoluntario = UUID.randomUUID();
        
        ong.adicionarSeguidor(idVoluntario);
        
        assertTrue(ong.getSeguidores().contains(idVoluntario));
        assertEquals(1, ong.getQuantidadeSeguidores());
    }

    @Test
    @DisplayName("Deve remover seguidor da ONG")
    void deveRemoverSeguidor() {
        UUID idVoluntario = UUID.randomUUID();
        ong.adicionarSeguidor(idVoluntario);
        
        ong.removerSeguidor(idVoluntario);
        
        assertFalse(ong.getSeguidores().contains(idVoluntario));
        assertEquals(0, ong.getQuantidadeSeguidores());
    }

    @Test
    @DisplayName("Deve adicionar evento organizado")
    void deveAdicionarEventoOrganizado() {
        UUID idEvento = UUID.randomUUID();
        
        ong.adicionarEventoOrganizado(idEvento);
        
        assertTrue(ong.getEventosOrganizados().contains(idEvento));
        assertEquals(1, ong.getEventosOrganizados().size());
    }

    @Test
    @DisplayName("Deve definir informações de contato")
    void deveDefinirInformacoesDeContato() {
        String endereco = "Rua das Flores, 123";
        String telefone = "(43) 99999-9999";
        
        ong.setEndereco(endereco);
        ong.setTelefone(telefone);
        
        assertEquals(endereco, ong.getEndereco());
        assertEquals(telefone, ong.getTelefone());
    }

    @Test
    @DisplayName("Deve verificar se ONG está verificada")
    void deveVerificarStatusVerificacao() {
        assertFalse(ong.getVerificada());
        
        ong.setVerificada(true);
        
        assertTrue(ong.getVerificada());
    }

    @Test
    @DisplayName("Não deve permitir CNPJ nulo no construtor")
    void naoDevePermitirCnpjNulo() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ong(EMAIL_VALIDO, SENHA_VALIDA, NOME_VALIDO, null);
        });
    }

    @Test
    @DisplayName("Não deve permitir CNPJ vazio no construtor")
    void naoDevePermitirCnpjVazio() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ong(EMAIL_VALIDO, SENHA_VALIDA, NOME_VALIDO, "");
        });
    }
}
