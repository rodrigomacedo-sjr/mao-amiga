package com.backend.mao_amiga.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuração de segurança mínima para a aplicação Mão Amiga.
 * 
 * Esta configuração permite todas as requisições HTTP sem autenticação,
 * adequada para desenvolvimento e testes iniciais.
 * 
 * ATENÇÃO: Em produção, implemente autenticação e autorização adequadas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura o filtro de segurança para permitir todas as requisições.
     * 
     * @param http objeto HttpSecurity para configuração
     * @return SecurityFilterChain configurado
     * @throws Exception em caso de erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita CSRF para facilitar testes com APIs REST
            .csrf(csrf -> csrf.disable())
            
            // Permite todas as requisições sem autenticação
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            
            // Desabilita autenticação básica HTTP
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // Desabilita login form padrão do Spring Security
            .formLogin(form -> form.disable());

        return http.build();
    }
}
