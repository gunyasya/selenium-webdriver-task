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

## Framework Architecture

```
src/main/java/
├── driver/
│   ├── Browser.java            # enum, WebDriver supplier per browser (Chrome/Firefox)
│   ├── BrowserContext.java     # ThreadLocal<Browser> — which browser this thread runs
│   └── WebDriverFactory.java   # ThreadLocal<WebDriver> — lazy create / quit+cleanup
└── pages/
    ├── BasePage.java             # abstract; PageFactory init, explicit-wait helpers
    ├── PageUrls.java             # URL fragment constants
    ├── LoginPage.java
    ├── InventoryPage.java
    ├── CartPage.java
    ├── CheckoutInfoPage.java
    ├── CheckoutOverviewPage.java
    ├── CheckoutCompletePage.java
    └── ProductDetailPage.java

src/test/java/tests/
├── BaseTest.java                # per-method browser lifecycle (@BeforeMethod/@AfterMethod)
├── SingleItemCheckoutTest.java
├── InvalidLoginTest.java
└── SortAndNavigateTest.java

testng.xml                       # suite wiring all 3 scenario classes
```

POM with PageFactory. `BasePage` is abstract; each page extends it and exposes only public action/assertion-support methods —
locators and raw Selenium calls stay private/protected, never touched directly by tests.

Browser lifecycle is per test method: each `@Test` gets a brand-new `WebDriver` session
(`@BeforeMethod`/`@AfterMethod` in `BaseTest`), so there's no shared state
(cookies, login session, implicit wait setting) to accidentally leak between scenarios.

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

## Running the Tests

Run the whole suite (all 3 scenarios):
```bash
mvn test
```

Run a single scenario class:
```bash
mvn test -Dtest=InvalidLoginTest
```
