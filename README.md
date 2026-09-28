# SauceDemo – Automação E2E com Selenium

Automação do fluxo de **login → filtro/busca de produto → adição ao carrinho → validação no checkout** do e-commerce de testes [saucedemo.com](https://www.saucedemo.com), desenvolvida como parte do teste técnico para Analista de Testes (Seção 2).

## Stack

| Item | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Automação | Selenium WebDriver 4 (driver resolvido automaticamente pelo **Selenium Manager**) |
| Test runner | JUnit 5 (testes parametrizados) |
| Asserções | AssertJ (mensagens descritivas com `.as(...)`) |
| Build | Maven |
| Padrão | **Page Objects** + Component (cabeçalho) |
| Infra | Docker Compose (Selenium Standalone Chrome + Maven) |
| CI | GitHub Actions (headless + upload de evidências) |

## Estrutura do projeto

```
src/test/java/com/saucedemo
├── config/      Config.java                  -> URL, navegador, headless, timeouts (sobrescrevíveis por -D ou variável de ambiente)
├── driver/      DriverFactory / DriverManager -> criação do WebDriver local ou remoto (Grid) e ThreadLocal
├── data/        Product, Money, TestData      -> modelo, conversão de preço e massa de dados
├── pages/       BasePage + Page Objects       -> Login, Inventory, Cart, CheckoutInformation, CheckoutOverview, CheckoutComplete
│   └── components/HeaderComponent             -> contador e link do carrinho (compartilhado entre páginas)
├── support/     EvidenceOnFailureExtension    -> screenshot + HTML da página quando um teste falha
└── tests/       BaseTest, LoginTest, ProductSortTest, CheckoutFlowTest
```

## Cenários automatizados

| Classe | Cenário | Principais validações |
|---|---|---|
| `CheckoutFlowTest` | **Fluxo principal** (parametrizado com 2 produtos): login, filtro por preço, adicionar ao carrinho, checkout e finalização | título de cada página, filtro aplicado, produto presente na vitrine, botão muda para *Remove*, badge = 1, item/preço/quantidade no carrinho, **item/preço/quantidade no checkout**, *Item total* = preço do produto, mensagem de confirmação, carrinho zerado |
| `ProductSortTest` | Ordenação por preço (menor → maior) e por nome (Z → A) | opção ativa no filtro e ordem real da lista |
| `LoginTest` | Login com usuário padrão / usuário bloqueado | URL e título pós-login / mensagem de erro exata |

> O SauceDemo não possui campo de busca; a etapa "filtrar ou buscar um produto" é atendida pelo **filtro de ordenação** + localização do produto **pelo nome** (nunca por posição), o que mantém o teste estável mesmo se a ordem mudar.

## Pré-requisitos

- **JDK 17+**
- **Maven 3.8+** (ou o Maven embutido do IntelliJ)
- **Google Chrome** instalado (Firefox/Edge opcionais)
- **Docker Desktop** (apenas para a execução via Docker)

Não é necessário baixar o ChromeDriver: o Selenium Manager faz isso automaticamente na primeira execução.

## Como executar

### 1. Pelo IntelliJ IDEA
1. `File > Open` e selecione a pasta do projeto (o IntelliJ reconhece o `pom.xml`).
2. Aguarde o Maven baixar as dependências (ícone *Load Maven Changes* se necessário).
3. Clique com o botão direito em `src/test/java` → **Run 'All Tests'**, ou rode uma classe específica (ex.: `CheckoutFlowTest`).

### 2. Pela linha de comando
```bash
# Todos os testes, com navegador visível
mvn clean test

# Modo headless
mvn clean test -Dheadless=true

# Apenas o fluxo principal
mvn clean test -Dtest=CheckoutFlowTest

# Outro navegador
mvn clean test -Dbrowser=firefox
```

### 3. Via Docker (sem precisar de Java/Chrome na máquina)
```bash
docker compose up --abort-on-container-exit --exit-code-from tests
docker compose down
```
- O serviço `selenium` sobe um **Selenium Standalone Chrome** com *healthcheck*.
- O serviço `tests` (Maven + JDK 17) só inicia quando o Selenium está pronto e executa a suíte apontando para `http://selenium:4444`.
- Acompanhe a execução ao vivo em **http://localhost:7900** (senha: `secret`).
- Em Mac com chip Apple Silicon: `SELENIUM_IMAGE=selenium/standalone-chromium:latest docker compose up ...`

## Configurações disponíveis

Cada chave pode ser passada como `-Dchave=valor` ou como variável de ambiente (maiúsculo, `.` → `_`).

| Chave | Variável de ambiente | Padrão | Descrição |
|---|---|---|---|
| `base.url` | `BASE_URL` | `https://www.saucedemo.com` | URL da aplicação |
| `browser` | `BROWSER` | `chrome` | `chrome`, `firefox` ou `edge` |
| `headless` | `HEADLESS` | `false` | Executa sem interface gráfica |
| `selenium.remote.url` | `SELENIUM_REMOTE_URL` | *(vazio)* | Se preenchida, usa `RemoteWebDriver` (Grid/Docker) |
| `explicit.wait.seconds` | `EXPLICIT_WAIT_SECONDS` | `10` | Tempo máximo das esperas explícitas |
| `evidence.dir` | `EVIDENCE_DIR` | `target/evidencias` | Pasta das evidências de falha |

## Evidências de falha

Quando um teste falha, a extensão `EvidenceOnFailureExtension` salva automaticamente em `target/evidencias/`:
- `*.png` – screenshot do momento da falha;
- `*.html` – HTML da página + URL (útil para depurar seletores).

Relatórios do JUnit ficam em `target/surefire-reports/`. No GitHub Actions, as evidências são publicadas como *artifact* do job quando há falha.

> Para ver a captura funcionando, altere temporariamente uma asserção (ex.: `isEqualTo("Products")` → `isEqualTo("Produtos")`) e execute o teste.

## Decisões técnicas

- **Page Objects com navegação fluente**: cada ação que muda de página retorna o próximo Page Object (`cart.checkout()` → `CheckoutInformationPage`), deixando o teste legível como o fluxo de negócio.
- **Asserções somente nos testes**: Page Objects expõem estado (títulos, itens, totais); a decisão de passar/falhar fica no teste, com mensagens descritivas.
- **Sincronização apenas com esperas explícitas** (`WebDriverWait` + `ExpectedConditions`), sem `Thread.sleep` nem implicit wait, reduzindo *flakiness*.
- **Seletores estáveis**: `id` e classes semânticas da aplicação; produtos localizados pelo nome, não por índice.
- **Um navegador por teste** para isolamento total dos cenários; `DriverManager` com `ThreadLocal` já prepara a suíte para execução paralela.
- **Evidência capturada antes do `@AfterEach`**: a extensão usa `AfterTestExecutionCallback`, que executa antes do encerramento do navegador (com `TestWatcher` o driver já estaria fechado).
- **Pop-up de senha do Chrome desativado** via *prefs*, evitando interferência do alerta de "senha vazada" com as credenciais públicas do site.
