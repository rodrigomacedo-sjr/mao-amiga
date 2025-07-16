package com.backend.mao_amiga;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

// Testes usando PostgreSQL
@SpringBootTest
@ActiveProfiles("test")
class MaoAmigaApplicationTests {

	@Test
	void contextLoads() {
		// Teste simples para verificar se a aplicação inicializa corretamente
	}

}
