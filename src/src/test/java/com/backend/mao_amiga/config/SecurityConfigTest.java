package com.backend.mao_amiga.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste para verificar se a configuração de segurança está funcionando corretamente.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private SecurityFilterChain securityFilterChain;

    /**
     * Verifica se o bean SecurityFilterChain foi criado corretamente.
     */
    @Test
    void shouldCreateSecurityFilterChainBean() {
        // Verifica se o bean foi injetado corretamente
        assertThat(securityFilterChain).isNotNull();
    }
}
