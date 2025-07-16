package com.backend.mao_amiga;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

/**
 * Suíte de testes completa para o projeto Mão Amiga
 * Executa todos os testes de modelos e controllers
 */
@Suite
@SuiteDisplayName("Mão Amiga - Testes Funcionais")
@SelectPackages({
    "com.backend.mao_amiga.models",
    "com.backend.mao_amiga.controllers"
})
class MaoAmigaTestSuite {
    
    // Esta classe serve apenas como organizadora da suíte de testes
    // Os testes são executados automaticamente pelos pacotes selecionados
}
