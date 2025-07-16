package com.backend.mao_amiga.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import com.backend.mao_amiga.models.enums.AreaInteresse;
import java.util.UUID;

class VoluntarioTest {

    private Voluntario voluntario;
    private final String EMAIL_VALIDO = "voluntario@exemplo.com";
    private final String SENHA_VALIDA = "senha123";
    private final String NOME_VALIDO = "João Silva";

    @BeforeEach
    void setUp() {
        voluntario = new Voluntario(EMAIL_VALIDO, SENHA_VALIDA, NOME_VALIDO);
    }

    @Test
    @DisplayName("Deve criar voluntário com dados básicos válidos")
    void deveCriarVoluntarioComDadosBasicos() {
        assertNotNull(voluntario);
        assertNotNull(voluntario.getId());
        assertEquals(EMAIL_VALIDO, voluntario.getEmail());
        assertEquals(NOME_VALIDO, voluntario.getNomeCompleto());
        assertTrue(voluntario.getAreasDeInteresse().isEmpty());
        assertTrue(voluntario.getHistoricoDeEventos().isEmpty());
        assertTrue(voluntario.getOngsSeguidas().isEmpty());
    }

    @Test
    @DisplayName("Deve adicionar área de interesse")
    void deveAdicionarAreaDeInteresse() {
        voluntario.adicionarAreaDeInteresse(AreaInteresse.EDUCACAO);
        
        assertTrue(voluntario.getAreasDeInteresse().contains(AreaInteresse.EDUCACAO));
        assertEquals(1, voluntario.getAreasDeInteresse().size());
    }

    @Test
    @DisplayName("Deve remover área de interesse")
    void deveRemoverAreaDeInteresse() {
        voluntario.adicionarAreaDeInteresse(AreaInteresse.EDUCACAO);
        voluntario.adicionarAreaDeInteresse(AreaInteresse.MEIO_AMBIENTE);
        
        voluntario.removerAreaDeInteresse(AreaInteresse.EDUCACAO);
        
        assertFalse(voluntario.getAreasDeInteresse().contains(AreaInteresse.EDUCACAO));
        assertTrue(voluntario.getAreasDeInteresse().contains(AreaInteresse.MEIO_AMBIENTE));
        assertEquals(1, voluntario.getAreasDeInteresse().size());
    }

    @Test
    @DisplayName("Deve seguir uma ONG")
    void deveSeguirOng() {
        UUID idOng = UUID.randomUUID();
        
        voluntario.seguirOng(idOng);
        
        assertTrue(voluntario.getOngsSeguidas().contains(idOng));
        assertEquals(1, voluntario.getOngsSeguidas().size());
    }

    @Test
    @DisplayName("Deve deixar de seguir uma ONG")
    void deveDeixarDeSeguirOng() {
        UUID idOng = UUID.randomUUID();
        voluntario.seguirOng(idOng);
        
        voluntario.deixarDeSeguirOng(idOng);
        
        assertFalse(voluntario.getOngsSeguidas().contains(idOng));
        assertEquals(0, voluntario.getOngsSeguidas().size());
    }

    @Test
    @DisplayName("Deve adicionar evento ao histórico")
    void deveAdicionarEventoAoHistorico() {
        UUID idEvento = UUID.randomUUID();
        
        voluntario.adicionarEventoAoHistorico(idEvento);
        
        assertTrue(voluntario.getHistoricoDeEventos().contains(idEvento));
        assertEquals(1, voluntario.getHistoricoDeEventos().size());
    }

    @Test
    @DisplayName("Deve favoritar um evento")
    void deveFavoritarEvento() {
        UUID idEvento = UUID.randomUUID();
        
        voluntario.favoritarEvento(idEvento);
        
        assertTrue(voluntario.getEventosFavoritos().contains(idEvento));
        assertEquals(1, voluntario.getEventosFavoritos().size());
    }

    @Test
    @DisplayName("Deve remover evento dos favoritos")
    void deveRemoverEventoDosFavoritos() {
        UUID idEvento = UUID.randomUUID();
        voluntario.favoritarEvento(idEvento);
        
        voluntario.desfavoritarEvento(idEvento);
        
        assertFalse(voluntario.getEventosFavoritos().contains(idEvento));
        assertEquals(0, voluntario.getEventosFavoritos().size());
    }

    @Test
    @DisplayName("Deve verificar se tem interesse em área específica")
    void deveVerificarInteresseEmArea() {
        voluntario.adicionarAreaDeInteresse(AreaInteresse.SAUDE);
        
        assertTrue(voluntario.temInteresseEm(AreaInteresse.SAUDE));
        assertFalse(voluntario.temInteresseEm(AreaInteresse.TECNOLOGIA));
    }

    @Test
    @DisplayName("Deve verificar se já participou de evento")
    void deveVerificarParticipacaoEmEvento() {
        UUID idEvento = UUID.randomUUID();
        voluntario.adicionarEventoAoHistorico(idEvento);
        
        assertTrue(voluntario.jaParticipouDoEvento(idEvento));
        assertFalse(voluntario.jaParticipouDoEvento(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve verificar se evento está favoritado")
    void deveVerificarSeEventoEstaFavoritado() {
        UUID idEvento = UUID.randomUUID();
        voluntario.favoritarEvento(idEvento);
        
        assertTrue(voluntario.eventoEstaFavoritado(idEvento));
        assertFalse(voluntario.eventoEstaFavoritado(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Deve contar quantidade de eventos participados")
    void deveContarEventosParticipados() {
        assertEquals(0, voluntario.getQuantidadeEventosParticipados());
        
        voluntario.adicionarEventoAoHistorico(UUID.randomUUID());
        voluntario.adicionarEventoAoHistorico(UUID.randomUUID());
        
        assertEquals(2, voluntario.getQuantidadeEventosParticipados());
    }
}
