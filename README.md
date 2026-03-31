# <img src="https://files.readme.io/f36a7d7-small-picture_1.jpeg" alt="Sicredi Logo" width="40" height="40"> Sicredi Challenge — QA Automation

Projeto de automação de testes de API desenvolvido como desafio técnico Sicredi.  

A API alvo é a [DummyJSON](https://dummyjson.com), o qual simula uma aplicação de gerenciamento de produtos eletrônicos.

---

## 📋 1. Informações Mínimas do Projeto

**Objetivo:** Validar o funcionamento correto dos endpoints da API de acordo com a documentação do desafio, identificando comportamentos incorretos e propondo melhorias.

**Escopo Real:**
- 8 endpoints testados
- 81 métodos de teste anotados com `@Test` ou `@ParameterizedTest`
- 100 execuções reais (alguns testes parametrizados expandem para múltiplas execuções)
- **3 testes com falha esperada** (contrato vs bugs conhecidos em `@BugFound`)
- Cobertura de fluxos: sucesso, erro, validação, edge cases, regressão de bugs

**Stack Técnico:**

| Tecnologia | Versão | Propósito |
|---|---|---|
| Java | 18 | Linguagem |
| RestAssured | 5.4.0 | Client HTTP para testes de API |
| JUnit 5 | 5.9.2 | Framework de testes |
| AssertJ | 3.24.1 | Assertions fluentes |
| Allure | 2.27.0 | Geração de relatórios |
| Lombok | latest | Redução de boilerplate em POJOs |
| Maven | 3.9.6 | Build e gerenciamento de dependências |

---

## 🏗️ 2. Arquitetura e Componentes

### 2.1 Estrutura do Projeto

```
src/test/java/org/sicredi/challenge/
├── annotations/
│   └── BugFound.java          ← Annotation customizada para rastreabilidade
├── model/                      ← DTOs (POJOs com Lombok)
│   ├── Dimensions.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── Product.java
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   ├── Review.java
│   └── UserResponse.java
├── tests/                      ← Classes de teste (87 métodos)
│   ├── BaseTest.java           ← Setup compartilhado
│   ├── HealthTest.java
│   ├── AuthLoginTest.java
│   ├── AuthTest.java
│   ├── AuthSecurityTest.java
│   ├── GetAuthProductsTest.java
│   ├── CreateProductTest.java  ← Consolida cenários de /products/add (antes em AddProductTest)
│   ├── GetProductByIdTest.java
│   ├── ProductsTest.java
│   └── UsersTest.java
└── utils/                      ← Utilitários de suporte
    ├── ApiHelper.java          ← Abstração de requisições HTTP
    ├── AssertionsHelper.java   ← Validações reutilizáveis
    ├── TestConstants.java      ← Constantes e configuração
    ├── TestDataBuilder.java    ← Factory de dados de teste
    └── TokenManager.java       ← Gerenciamento de JWT
```

---

## 🏆 3 Plano de Testes e Estratégia

### Abordagem Geral

O projeto segue uma estratégia de cobertura **multinível** para garantir confiabilidade:

| Nível | Objetivo | Exemplos |
|-------|----------|----------|
| **Fluxo Feliz** | Validar caminho correto | Criar produto com dados válidos (201) |
| **Validação de Entrada** | Detectar rejeição de dados inválidos | Campos vazios, tipos incorretos (400) |
| **Edge Cases** | Testar limites e extremos | IDs 0, -1, 999999 (404) |
| **Autenticação** | Garantir acesso controlado | Token válido/inválido/ausente (401) |
| **Segurança JWT** | Validar integridade de tokens | Algoritmo, claims, expiração |
| **Bugs Conhecidos** | Documentar comportamentos divergentes | @BugFound com estratégia dupla |

### Tipos de Testes Implementados

```
Total de testes:        82 métodos @Test
├── Testes de Sucesso        (fluxo esperado correto)        → ~45 testes
├── Testes Negativos         (comportamento esperado erro)   → ~35 testes
├── Testes de Edge Case      (IDs 0, -1, 999999, strings)   → ~8 testes
├── Testes de Bug @BugFound  (divergência documentada)       → 3 testes falhando
└── Testes de Regressão      (comportamento atual conhecido) → 3 testes passando
```

### Estratégia de Cobertura por Endpoint

**1. Autenticação (`/auth/login`):**
- ✅ Fluxo de sucesso com credenciais válidas
- ✅ Rejeição com credenciais inválidas (400)
- ✅ Validação de campos ausentes
- ✅ Body não-JSON (4xx)
- ✅ Tokens JWT retornados corretamente

**2. Produtos Autenticados (`/auth/products`):**
- ✅ Acesso com token válido (200)
- ✅ Rejeição com token inválido (401)
- ⚠️ Bug #1: acesso sem token (202) retorna 200 em vez de 401

**3. Produtos Públicos (`/products`, `/products/{id}`, `/products/add`):**
- ✅ Listagem com filtros e paginação
- ✅ Busca por ID válido (200)
- ✅ Rejeição de ID inválido/não encontrado (404)
- ✅ Edge cases: IDs 0, -1, 999999
- ✅ Criação com sucesso (201)
- ⚠️ Bug #2: criação com campos vazios retorna 201 em vez de 400

**4. Segurança JWT (`/auth/security`):**
- ✅ Validação de formato Bearer
- ✅ Decodificação de claims
- ✅ Verificação de algoritmo (HS256)
- ✅ Validação de expiração

### Estratégia para Bugs Conhecidos

Cada bug tem **dois testes** para rastreabilidade:

```java
// 1. Teste de CONTRATO (valida o que DEVERIA acontecer)
@Test
@BugFound("BUG #X: ...")
void deveRetornar401Sem() {
    ApiHelper.getRaw(requestSpec, "/endpoint", 401);
    // Falha enquanto o bug existir — isso é a INFORMAÇÃO ÚTIL
}

// 2. Teste de COMPORTAMENTO (documenta o que acontece HOJE)
@Test
void deveDocumentarComportamentoAtualSem() {
    ApiHelper.get(requestSpec, "/endpoint", 200, Response.class);
    // Passa, rastreando mudanças/regressões futuras
}
```

**Resultado:** 3 testes falhando no contrato + 3 rastreando regressão = Honestidade 100%

### Decisões de Projeto

| Decisão | Justificativa |
|---------|---------------|
| `@BugFound` com falha intencional | Torna bugs VISÍVEIS, não ocultos |
| User DTO com 2 campos apenas | Foco em "campos importantes para negócio" |
| TestDataBuilder centralizado | Evita dispersão de dados mock |
| Assertions descritivas com `.as()` | Relatórios claros em caso de falha |
| Edge cases documentados em TestConstants | Reutilização e manutenção |

---

## 🚀 4. Como Executar

### Pré-requisitos
- Java 18+
- Maven 3.9.6+

### Opção 1: Maven (padrão)

```bash
# Executar todos os testes
mvn test

# Executar uma classe específica
mvn test -Dtest=AuthTest

# Executar testes que casem com um padrão
mvn test -Dtest=*Auth*
```

### Opção 2: Shell Script Local (execução simples)

Executar testes localmente com Maven e gerar relatório Allure automaticamente:

```bash
# Dar permissão de execução (primeira vez)
chmod +x run-tests-local.sh

# Executar
./run-tests-local.sh
```

**O que o script faz:**
- ✅ Verifica se Java 18+ está instalado
- ✅ Verifica se Maven 3.9.6+ está instalado
- ✅ Executa `mvn clean test allure:report`
- ✅ Inicia servidor Allure automaticamente na porta 4040
- ✅ Abre relatório no navegador padrão

**Requisitos:**
- Java 18+
- Maven 3.9.6+

---

### Opção 3: Shell Script com Docker (execução isolada)

Executar testes dentro de container Docker isolado:

```bash
# Dar permissão de execução (primeira vez)
chmod +x run-tests-docker.sh

# Executar
./run-tests-docker.sh
```

**O que o script faz:**
- ✅ Verifica se Docker está instalado
- ✅ Verifica se Docker Compose está instalado
- ✅ Faz build da imagem (`sicredi-challenge:latest`)
- ✅ Inicia container com docker-compose
- ✅ Aguarda testes completarem
- ✅ Mostra status do container
- ✅ Relatório disponível em `./target/site/allure-maven-plugin/index.html`

**Requisitos:**
- Docker (qualquer versão recente)
- Docker Compose

**Parar o container:**
```bash
docker-compose down
```

---
### Gerar Relatório Allure

```bash
mvn allure:serve
# Abre automaticamente http://localhost:4040
```

---

## 🐳 4.1 Configuração Docker

### Dockerfile

O projeto inclui `Dockerfile` otimizado para executar testes em container:

```dockerfile
FROM maven:3.9-eclipse-temurin-21
WORKDIR /app

# Cache de dependências Maven
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copiar fonte e configurações
COPY src ./src
COPY etc ./etc

# Executar testes e gerar relatório
RUN mvn clean test allure:report -q

# Volumes para resultados
VOLUME ["/app/target/allure-results", "/app/target/site"]
```

**Características:**
- ✅ Maven 3.9 + Eclipse Temurin JDK 21
- ✅ Cache de dependências (build mais rápido)
- ✅ Testes e relatório Allure incluídos
- ✅ Volumes para resultados persistentes

### docker-compose.yml

Orquestração simplificada com Docker Compose:

```yaml
services:
  sicredi-tests:
    build:
      context: .
      dockerfile: Dockerfile
    image: sicredi-challenge:latest
    container_name: sicredi-challenge-app
    volumes:
      - ./target/allure-results:/app/target/allure-results
      - ./target/site:/app/target/site
    environment:
      JAVA_OPTS: "-Xmx512m"
    restart: unless-stopped
```

**O que oferece:**
- ✅ Build automático da imagem
- ✅ Volume compartilhado para resultados (`./target`)
- ✅ Limite de memória (512MB)
- ✅ Container nomeado: `sicredi-challenge-app`

---

## 🚀 4.2 Gerar Relatório Allure

```bash
mvn allure:serve
# Abre automaticamente http://localhost:4040
```

---

## 📊 5. Cobertura de Endpoints e Cenários

### Endpoints Testados

| Endpoint | Método | Cenários | Descrição |
|---|---|---|---|
| `/test` | GET | 1 | Health check — validação básica |
| `/auth/login` | POST | 9 | Autenticação: sucesso, erro, validações, body não-JSON |
| `/auth/products` | GET | 4 | Busca produtos autenticada com token válido/inválido, sem token (contrato vs comportamento) |
| `/auth/security` | POST | 12 | Validação de JWT (formato, algoritmo, claims) |
| `/products` | GET | 18 | Listagem: filtros, paginação, limites |
| `/products/{id}` | GET | 16 | Busca individual: IDs válidos, inválidos, extremos, 404 com mensagem de erro |
| `/products/add` | POST | 18 | Criação: sucesso, validações, edge cases, contrato vs comportamento (BUG #2) |
| `/users` | GET | 11 | Busca de usuários: username/password críticos, unicidade, paginação |
| **TOTAL** | | **100** | **100% dos endpoints cobertos** |

### Tipos de Cenários Testados

- ✅ **Fluxo de Sucesso** — Dados válidos, resposta 200/201
- ✅ **Validação de Entrada** — Campos vazios, tipos incorretos, ranges inválidos
- ✅ **Autenticação** — Tokens válidos, inválidos, ausentes
- ✅ **Edge Cases** — IDs negativos, zero, valores muito grandes, strings muito longas
- ✅ **Estrutura de Resposta** — Validação de campos obrigatórios, tipos esperados
- ✅ **Paginação** — Limites, skips, totais
- ✅ **Segurança** — Decodificação JWT, validação de algoritmo, expiração
- ✅ **Erros HTTP adicionais** — `404` para recurso inexistente, body malformado/não-JSON em login, validação de mensagem de erro (`message`/`error`)

---

## 🐛 6. Bugs Identificados

Cada bug foi identificado através de execução de teste e validação contra a documentação oficial da API.

**Estratégia de Rastreabilidade:**
- Testes marcados com `@BugFound` validam o **contrato documentado** — podem falhar enquanto o bug existir
- Testes de **comportamento atual** rastreiam o que a API faz hoje — permitem detectar mudanças ou regressões
- Resultado: 6 testes relacionados a bugs (3 contrato @BugFound + 3 comportamento), mas apenas 3 falham conforme esperado

### BUG #1: Falta de Validação de Autenticação em /auth/products

**Severidade:** 🔴 CRÍTICO  
**Endpoint:** `GET /auth/products`  
**Comportamento:** Retorna 200 OK com lista de produtos **sem exigir autenticação**  
**Esperado:** Retornar 401 Unauthorized quando `Authorization` header está ausente  

**Testes (estratégia dupla):**
```java
// 1) Contrato documentado (deve falhar enquanto o bug existir)
// src/test/java/org/sicredi/challenge/tests/GetAuthProductsTest.java
@Test
@BugFound
void deveRetornar401AoBuscarProdutosSemToken() {
    ApiHelper.getRaw(requestSpec, "/auth/products", 401);
}

// 2) Comportamento atual (rastreamento de regressão)
@Test
void deveDocumentarComportamentoAtualSemToken() {
    ApiHelper.get(requestSpec, "/auth/products", 200, ProductResponse.class);
}
```

**Reprodução:**
```bash
curl -X GET "https://dummyjson.com/auth/products"
# Resposta: 200 OK com produtos (INCORRETO)
# Esperado: 401 Unauthorized
```

**Impacto:** Vazamento de dados que deveriam ser protegidos por autenticação.

---

### BUG #2: Validação Insuficiente em POST /products/add

**Severidade:** 🟡 ALTO  
**Endpoint:** `POST /products/add`  
**Comportamento:** Aceita payloads com campos obrigatórios vazios ou nulos  
**Esperado:** Retornar 400 Bad Request quando campos obrigatórios estão inválidos  

**Testes (estratégia dupla):**
```java
// 1) Contrato documentado (deve falhar enquanto o bug existir)
@Test
@BugFound
void deveRetornar400AoCriarProdutoComTituloVazio() {
    ApiHelper.postRaw(requestSpec, "/products/add", requestComTituloVazio, 400);
}

// 2) Comportamento atual (rastreamento de regressão)
@Test
void deveDocumentarComportamentoAtualTituloVazio() {
    ApiHelper.post(requestSpec, "/products/add", requestComTituloVazio, 201, Product.class);
}
```

**Impacto:** Produtos com dados inválidos/vazios podem ser salvos, corrompendo o banco de dados.

---

### BUG #3: Campo de Resposta Não Documentado

**Severidade:** 🟠 MÉDIO  
**Endpoint:** `POST /auth/login`  
**Observação:** A resposta retorna `"accessToken"` (confirmado na documentação oficial)  

O README inicial mencionava isso como bug, mas a documentação da API confirma que o campo correto é `accessToken`, não `token`. Este é um falso positivo. Removido da lista de bugs reais.

---

## 💡 7. Melhorias Sugeridas

Com base na análise dos endpoints e padrões observados, as seguintes melhorias são recomendadas para robustez e segurança:

### 1. Validação Rigorosa de Entrada (🔴 CRÍTICO)

**Situação Atual:** /products/add aceita title="" e price=null  
**Recomendação:**
- Validar campos obrigatórios: title, price, category
- Validar ranges: price > 0, title.length() > 0
- Validar tipos: price deve ser numérico, não string

### 2. Autenticação Forte em Rotas Protegidas (🔴 CRÍTICO)

**Situação Atual:** /auth/products não valida Authorization header  
**Recomendação:**
- Implementar validação de presença de Authorization header
- Retornar 401 se ausente
- Validar formato do token ("Bearer <token>")
- Validar expiração do JWT

### 3. Tratamento de Erros Padronizado (🟡 ALTO)

**Situação Atual:** Respostas de erro não são consistentes  
**Recomendação:**
- Retornar JSON estruturado: `{ "error": "...", "code": "...", "details": {...} }`
- Incluir mensagens claras por campo
- Evitar stack traces em erro 5xx

---



## 🔍 8. Notas sobre Implementação

- **Lombok:** Reduz boilerplate em DTOs — apenas `@Data`, `@Builder`, `@AllArgsConstructor`
- **AssertJ:** Assertions fluentes com mensagens descritivas via `as("...")`
- **RestAssured:** Sintaxe fluente para construção de requisições
- **Allure:** Annotations `@Feature`, `@Description` para relatórios estruturados

---

## 📌 Referências

- **API:** https://dummyjson.com/docs
  - **RestAssured:** https://rest-assur                                                     ed.io/
    - **Allure:** https://docs.qameta.io/allure/                                                                                                                                                            
- **JUnit 5:** https://junit.org/junit5/docs/current/user-guide/
