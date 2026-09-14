# Selenium WebDriver + TestNG — SauceDemo

## System Under Test

[SauceDemo](https://www.saucedemo.com/) — public demo e-commerce web app.

## Tech Stack

| Tool                 | Version |
|----------------------|---------|
| Java                 | 17      |
| Selenium WebDriver   | 4.27.0  |
| TestNG               | 7.9.0   |
| WebDriverManager     | 5.7.0   |
| Logback              | 1.4.14  |
| Lombok               | 1.18.44 |
| Cucumber (java/testng) | 7.20.1 |

## Framework Architecture

```
src/main/java/
├── driver/
│   ├── Browser.java             # enum; Singleton (JVM enum-constant guarantee), selects a WebDriverCreator
│   ├── BrowserContext.java      # ThreadLocal<Browser> — which browser this thread runs
│   ├── WebDriverFactory.java    # ThreadLocal<WebDriver> — lazy create / quit+cleanup (lifecycle only)
│   ├── creator/                 # Factory Method
│   │   ├── WebDriverCreator.java       # abstract creator; shared isHeadless()/maximize() helpers
│   │   ├── ChromeDriverCreator.java    # builds the Chrome options-decorator chain below
│   │   └── FirefoxDriverCreator.java
│   └── options/                 # Decorator (Chrome only — see PATTERNS_AND_SOLID.md)
│       ├── ChromeOptionsProvider.java        # component interface: getOptions()
│       ├── BaseChromeOptionsProvider.java    # concrete component: plain ChromeOptions
│       ├── HeadlessOptionsDecorator.java     # adds --headless=new/--no-sandbox/--disable-dev-shm-usage
│       └── IncognitoOptionsDecorator.java    # adds --incognito
├── model/
│   ├── User.java                # record: username, password
│   ├── Product.java              # record: name, price
│   └── CheckoutInfo.java         # record: firstName, lastName, zipCode
├── utils/
│   └── PriceParser.java         # parsePrice(String) — extracted out of BasePage (SOLID/SRP fix)
└── pages/
    ├── BasePage.java             # abstract; PageFactory init, explicit-wait helpers, DEBUG logging
    ├── PageUrls.java             # URL path fragments (base URL comes from ConfigReader instead)
    ├── LoginPage.java
    ├── InventoryPage.java
    ├── CartPage.java
    ├── CheckoutInfoPage.java
    ├── CheckoutOverviewPage.java
    ├── CheckoutCompletePage.java
    └── ProductDetailPage.java

src/test/java/
├── config/
│   └── ConfigReader.java         # loads config/<env>.properties, selected via -Denv
├── listeners/
│   └── ScreenshotListener.java   # ITestListener; on failure, saves a screenshot + logs its path
├── tests/
│   ├── BaseTest.java             # per-method browser lifecycle, reads -Dbrowser
│   ├── SingleItemCheckoutTest.java   # @Test(groups = {"smoke", "regression"})
│   ├── InvalidLoginTest.java         # @Test(groups = {"regression"})
│   └── SortAndNavigateTest.java      # @Test(groups = {"regression"}) — plain TestNG version, kept alongside the BDD one below
├── steps/
│   ├── Hooks.java                # Cucumber @Before(order=0)/@After — browser lifecycle, mirrors BaseTest
│   └── SortAndNavigateSteps.java # step defs for SortAndNavigate.feature, regex-based (@Given/@When/@Then)
└── runner/
    └── CucumberTestRunner.java   # extends AbstractTestNGCucumberTests; bridges Cucumber into TestNG/Surefire

src/test/resources/
├── config/
│   ├── qa.properties             # base.url, user.username=standard_user, user.password
│   └── staging.properties        # same base.url, user.username=performance_glitch_user
├── features/
│   └── SortAndNavigate.feature   # Background + Scenario Outline + Examples (BDD version of Scenario 3)
└── logback.xml                   # console + daily-rotating file appender

smoke.xml                          # runs just the smoke-tagged class
regression.xml                     # runs all 3 (Surefire default, see pom.xml)
.gitlab-ci.yml                     # CI pipeline — see "CI Pipeline" below
```

Page Object Model with PageFactory (`@FindBy`). `BasePage` is abstract; every page extends it
and exposes only public action/assertion-support methods — locators and raw Selenium calls stay
private/protected, never touched directly by tests.

Browser lifecycle is per test method: each `@Test` gets a brand-new `WebDriver` session
(`@BeforeMethod`/`@AfterMethod` in `BaseTest`), so there's no shared state (cookies, login
session, implicit wait setting) to accidentally leak between scenarios.

## Business Model

`model.User`, `model.Product`, `model.CheckoutInfo` are Java records used as method params
across the page objects (`LoginPage.login(User)`, `InventoryPage.addToCart(Product)`,
`CheckoutInfoPage.fillForm(CheckoutInfo)`) instead of hardcoded strings.

## Design Patterns & SOLID

Singleton (`Browser` enum), Factory Method (`driver/creator/`), and Decorator (`driver/options/`)
are implemented in the driver-creation layer, plus 3 SOLID fixes applied to existing classes —
see [`PATTERNS_AND_SOLID.md`](PATTERNS_AND_SOLID.md) for the full write-up with reasoning and
proof each pattern is actually exercised at runtime.

## Environments

`ConfigReader` loads `config/<env>.properties` from the classpath, selected via `-Denv`
(default `qa`). The two environments share the same base URL, but log in as different test accounts —
`qa` uses `standard_user`, `staging`uses `performance_glitch_user`, SauceDemo's built-in "slow env" account.
This gives a real behavioral difference between environments rather than duplicated property values.

## Locator Strategy

Different strategies are chosen per element based on what's most stable:

- `id` — `LoginPage` (stable native ids: `user-name`, `password`, `login-button`)
- `className` — `InventoryPage` (`inventory_item`, `shopping_cart_link`)
- `xpath` — `CheckoutOverviewPage` (attribute-based lookup CSS can't express as cleanly)
- `css` (via `data-test` attributes) — everywhere else.

## Waits

- **Explicit** — `BasePage` wraps every interaction (`click`, `typeText`, `getText`, `urlContains`)
  in `WebDriverWait` + `ExpectedConditions`. This is the default across all three scenarios.
- **Implicit** — scoped tightly to one step in `SingleItemCheckoutTest`: set right before `addToCart`
  (giving the product grid time to finish rendering after login), reset to `Duration.ZERO` immediately after.
  Not set globally, to avoid the known implicit+explicit poll-stacking issue on a shared `WebDriver` instance.

## Logging

`logback.xml` writes to both console and a daily-rotating file (`logs/automation.<yyyy-MM-dd>.log`)
by default — no flag needed. Three levels are in active use:

- **DEBUG** (`BasePage`) — low-level actions: which element was clicked/typed into, what text was read
- **INFO** (page classes) — business-level actions: "Logging in as: ...", "Proceeding to checkout"
- **ERROR** (`ScreenshotListener`) — on test failure, logs the saved screenshot's path

## Screenshot on Failure

`ScreenshotListener` (`org.testng.ITestListener`) fires on `onTestFailure`, before `@AfterMethod`
tears driver down — captures a screenshot, saves it to `screenshots/<testName>_<timestamp>.png`,
and logs the path at ERROR level. Registered via `<listeners>` in `smoke.xml` and `regression.xml`.

## Suites

- `smoke.xml` — the core happy-path scenario only (`SingleItemCheckoutTest`)
- `regression.xml` — all 3 scenarios; this is Surefire's default (configured in `pom.xml`)

## Scenarios

### Scenario 1 — Checkout with one item

1. Open login page
2. Login as `standard_user`
3. Add "Sauce Labs Backpack" to cart
4. Go to cart
5. Assert "Sauce Labs Backpack" is present in the cart
6. Proceed to checkout
7. Fill checkout info form (first name, last name, zip)
8. Finish the order
9. Assert success message is "Thank you for your order!"

### Scenario 2 — Invalid login, then recover with valid credentials

1. Open login page
2. Enter invalid username/password
3. Click login
4. Assert error message is displayed
5. Assert error message text is correct
6. Assert still on login page (URL unchanged)
7. Enter valid credentials
8. Click login again
9. Assert redirected to inventory page

### Scenario 3 — Sort products, view detail, navigate back

1. Open login page
2. Login as `standard_user`
3. Open the product sort dropdown and select "Price (low to high)"
4. Read all displayed product prices
5. Assert prices are in ascending order
6. Click the first (cheapest) product's name
7. Assert the product detail page shows the matching name and price
8. Navigate back to the inventory page (`driver.navigate().back()`)
9. Assert the inventory page is displayed again

## BDD with Cucumber

`SortAndNavigate.feature` re-expresses Scenario 3 as Gherkin, driven by Cucumber-JVM on top of existing TestNG setup
(no JUnit runner — project's architecture is TestNG-based, so `cucumber-testng` + `AbstractTestNGCucumberTests`
was used instead of `cucumber-junit`).

- **`Background`** — login step (`Given user is logged as a "standard_user"`)
runs once before each row in `Examples`, instead of being repeated in scenario body.
- **`Scenario Outline` + `Examples`** — one scenario definition, run once per row:

  | sortOption           |
  |-----------------------|
  | Price (low to high)  |
  | Price (high to low)  |
  | Name (A to Z)         |
  | Name (Z to A)         |

  `Then` step branches on sort option's content (`Price`/`Name`, ASC/DESC)
  to assert correct order — one step definition covers all four rows.
- **Regex step definitions** — step defs use Java regex (`^...$`), not Cucumber Expressions, e.g.
  `@When("^user sorts products by \"([^\"]+)\"$")`, per task's filtering/parametrization requirement.
- **Wiring** — `Hooks` (`@Before(order=0)`/`@After`) owns browser create/quit, mirroring `BaseTest`'s
  lifecycle but via Cucumber's own hooks. `SortAndNavigateSteps` picks up the same `WebDriver` via
  `WebDriverFactory`'s `ThreadLocal` in its own `@Before(order=1)`.
- `CucumberTestRunner` registered in `regression.xml` alongside plain TestNG tests, so `mvn test`
  runs both the original test classes and all four Cucumber Examples rows in one go.

## Running the Tests

Run the regression suite (default, all 3 scenarios):
```bash
mvn test
```

Run just the smoke suite:
```bash
mvn test -Dsurefire.suiteXmlFiles=smoke.xml
```

Run a single test class:
```bash
mvn test -Dtest=InvalidLoginTest
```

Choose browser, environment, and headless mode:
```bash
mvn test -Dbrowser=firefox -Denv=staging -Dheadless=true
```

- `-Dbrowser` — `chrome` (default) or `firefox`
- `-Denv` — `qa` (default) or `staging`
- `-Dheadless` — `true`/`false`; if omitted, defaults to `true` only when running in CI
  (detected via the `CI` environment variable), `false` locally

## CI Pipeline

`.gitlab-ci.yml` runs the regression suite automatically on every push (headless Chrome — the
`CI=true` variable GitLab sets on every job is picked up by `WebDriverCreator.isHeadless()`), and offers
the smoke suite as a manual-trigger job. Both jobs:

- publish `target/surefire-reports/*.xml` as a JUnit report, so pass/fail shows up directly on
  the pipeline and merge-request UI
- archive `screenshots/` and `logs/` as artifacts, `when: always`, so failure screenshots survive
  after the job container is destroyed

Both jobs pass on a real GitLab pipeline run — Chrome installs, the test suite executes, and the
JUnit report + `screenshots/`/`logs/` artifacts upload successfully.

`before_script` was first validated locally in a Docker container matching the pipeline's image
and architecture (`--platform linux/amd64`, matching GitLab.com's shared runners) before ever
pushing. That local check wasn't the full story, though — three separate issues only surfaced
on the actual GitLab runner and had to be fixed there:

- `MAVEN_CLI_OPTS` originally included Maven's offline flag (`-o`), which fails immediately on
  a runner's first-ever build, since the dependency cache starts out empty
- `logback.xml`'s root logger at `DEBUG` also swept up a noisy third-party HTTP client logger
  (used internally when `WebDriverManager` downloads chromedriver), which blew past GitLab's
  4MB per-job log cap on a cold cache — fixed by scoping that one logger back to `INFO`
- Headless Chrome refused to start inside the runner's container at all
  (`SessionNotCreated: Chrome instance exited`) until `--no-sandbox` and
  `--disable-dev-shm-usage` were added — both needed specifically because Chrome's sandbox
  requires kernel privileges an unprivileged Docker container doesn't grant by default

Firefox is not currently exercised in CI — only Chrome runs there (the project's default
browser); Firefox works locally but has no pipeline coverage.