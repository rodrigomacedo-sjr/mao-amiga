# Testes Funcionais - Mão Amiga

Este documento descreve os testes funcionais implementados para o projeto Mão Amiga.

## 📁 Estrutura dos Testes

```
src/test/java/com/backend/mao_amiga/
├── models/
│   ├── OngTest.java           # Testes da entidade ONG
│   ├── VoluntarioTest.java    # Testes da entidade Voluntário
│   └── EventoTest.java        # Testes da entidade Evento
├── controllers/
│   ├── OngControllerTest.java       # Testes do controller de ONG
│   ├── VoluntarioControllerTest.java # Testes do controller de Voluntário
│   └── EventoControllerTest.java    # Testes do controller de Evento
├── MaoAmigaTestSuite.java     # Suíte completa de testes
└── MaoAmigaApplicationTests.java # Teste base da aplicação
```

## 🧪 Tipos de Testes

### Testes de Modelo (Unit Tests)
- **OngTest**: Validação de criação, áreas de atuação, seguidores e eventos
- **VoluntarioTest**: Verificação de interesses, participação em eventos e relacionamentos
- **EventoTest**: Teste de inscrições, vagas, datas e status

### Testes de Controller (Integration Tests)
- **OngControllerTest**: Endpoints REST para operações CRUD de ONGs
- **VoluntarioControllerTest**: APIs de gerenciamento de perfil e participação
- **EventoControllerTest**: Funcionalidades de criação e gestão de eventos

## 🚀 Como Executar os Testes

### Executar Todos os Testes
```bash
./mvnw test
```

### Executar Testes Específicos

#### Apenas testes de modelos:
```bash
./mvnw test -Dtest="com.backend.mao_amiga.models.*Test"
```

#### Apenas testes de controllers:
```bash
./mvnw test -Dtest="com.backend.mao_amiga.controllers.*Test"
```

#### Teste específico:
```bash
./mvnw test -Dtest="OngTest"
./mvnw test -Dtest="EventoControllerTest"
```

### Executar com perfil de teste:
```bash
./mvnw test -Dspring.profiles.active=test
```

## 📊 Cobertura dos Testes

### ONG (Ong.java)
- ✅ Criação com dados válidos
- ✅ Validação de CNPJ obrigatório
- ✅ Gerenciamento de áreas de atuação
- ✅ Sistema de seguidores
- ✅ Eventos organizados
- ✅ Status de verificação

### Voluntário (Voluntario.java)
- ✅ Criação de perfil
- ✅ Áreas de interesse
- ✅ Seguir/deixar de seguir ONGs
- ✅ Histórico de eventos
- ✅ Sistema de favoritos
- ✅ Contadores e estatísticas

### Evento (Evento.java)
- ✅ Criação com validação de datas
- ✅ Sistema de inscrições
- ✅ Gerenciamento de vagas
- ✅ Controle de presença
- ✅ Status e ciclo de vida
- ✅ Cálculos de ocupação

## 🏗️ Princípios Clean Code Aplicados

### 1. **Nomes Descritivos**
```java
@Test
@DisplayName("Deve criar ONG com dados básicos válidos")
void deveCriarOngComDadosBasicos()
```

### 2. **Métodos Pequenos e Focados**
Cada teste verifica apenas uma funcionalidade específica.

### 3. **Organização Clara**
- Setup padronizado com `@BeforeEach`
- Dados de teste em métodos auxiliares
- Testes agrupados por funcionalidade

### 4. **Assertions Expressivas**
```java
assertTrue(ong.getAreasDeAtuacao().contains(AreaInteresse.EDUCACAO));
assertEquals(1, ong.getAreasDeAtuacao().size());
```

### 5. **Testes Independentes**
Cada teste pode ser executado isoladamente sem dependências.

## 📋 Cenários de Teste Cobertos

### Cenários Positivos
- Criação bem-sucedida de entidades
- Operações CRUD funcionais
- Relacionamentos entre entidades
- Validações de regras de negócio

### Cenários Negativos
- Dados inválidos ou incompletos
- Violações de regras de negócio
- Tentativas de operações não permitidas
- Busca por recursos inexistentes

### Cenários Limítrofes
- Eventos com capacidade máxima
- Datas no limite de validade
- Operações em sequência

## 🔧 Configuração do Ambiente de Teste

Os testes utilizam:
- **Spring Boot Test**: Framework de testes integrados
- **JUnit 5**: Engine de testes moderno
- **Profile "test"**: Configuração específica para testes
- **Assertions**: Validações expressivas e claras

## 📈 Relatórios

Para gerar relatórios de cobertura:
```bash
./mvnw test jacoco:report
```

Os relatórios são gerados em: `target/site/jacoco/index.html`

## 🎯 Benefícios dos Testes

1. **Confiabilidade**: Garantem que o código funciona conforme esperado
2. **Refatoração Segura**: Permitem mudanças com confiança
3. **Documentação Viva**: Servem como exemplos de uso das APIs
4. **Detecção Precoce**: Identificam problemas antes da produção
5. **Qualidade**: Mantêm padrões de código elevados

---

**Nota**: Todos os testes foram desenvolvidos seguindo princípios de clean code, sendo minimalistas, legíveis e focados em funcionalidades específicas.
