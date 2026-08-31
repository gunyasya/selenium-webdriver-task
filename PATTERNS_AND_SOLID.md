# Design Patterns + SOLID Review

## Design Patterns

### Singleton — `Browser` enum (`src/main/java/driver/Browser.java`)

`Browser` is a Java enum. JVM guarantees enum constants to have exactly one instance each —
`Browser.CHROME` can never exist as more than one object, for the lifetime of application. 
This is a deliberate choice: single-element/enum-based Singleton, since JVM enforces
single-instance guarantee for free (thread-safe, reflection-safe).

**Invoked during test runs:** every test, via `BaseTest.setUp()` → `BrowserContext.setBrowser(...)`
→ `WebDriverFactory.getDriver()` → `browser.getDriverCreator()`: all operating on same
enum-guaranteed singleton instance.

### Factory Method — driver creation (`src/main/java/driver/creator/`)

- `WebDriverCreator` — abstract creator, declares `abstract WebDriver createDriver()`
- `ChromeDriverCreator` / `FirefoxDriverCreator` — concrete creators, each overriding
  `createDriver()` with browser-specific construction logic

`Browser` selects which creator will be used; creators decide how to actually build driver.
Adding a new browser → adding new `WebDriverCreator` subclass, not modifying existing code.

**Invoked during test runs:** every test's `@BeforeMethod` calls `WebDriverFactory.getDriver()`,
which calls `browser.getDriverCreator().createDriver()` — confirmed against real Chrome and
Firefox processes.

### Decorator — Chrome options building (`src/main/java/driver/options/`)

- `ChromeOptionsProvider` — component interface, one method: `getOptions()`
- `BaseChromeOptionsProvider` — concrete component, returns a plain `new ChromeOptions()`
- `HeadlessOptionsDecorator` / `IncognitoOptionsDecorator` — concrete decorators, each wraps
  `ChromeOptionsProvider` and adds its own arguments on top of the wrapped result.

`ChromeDriverCreator` builds a chain — `HeadlessOptionsDecorator` wraps base provider only if
`isHeadless()` is true, `IncognitoOptionsDecorator` always wraps on top — replacing method
full of conditional `addArguments(...)` calls.

Chrome only: `ChromeOptions` and `FirefoxOptions` don't share enough of a common API shape
to decorate generically without fighting Selenium's type hierarchy. Firefox only has two flags —
not enough complexity to justify the same restructuring.

**Invoked during test runs:** every Chrome test run builds decorator chain inside
`ChromeDriverCreator.createDriver()` — confirmed via `ps` output showing all four flags
present on the actual launched Chrome process.

## SOLID Fixes

**Class: `BasePage`**
Problem: SRP broken — `parsePrice(String)` was a text-formatting utility without
page interacting, but lived in shared page base class because pages needed it.
Solution: Extracted to `utils.PriceParser` class (`public static Double parsePrice(String)`);
pages call it directly (`PriceParser.parsePrice(...)`/`PriceParser::parsePrice`) instead of inheriting.

**Class: `Browser`**
Problem: OCP broken — adding new browser required *modifying* enum's driver-creation logic.
Solution: Fixed via Factory Method pattern: new browser now means *adding* new `WebDriverCreator`
subclass, with no existing code touched.

**Class: `WebDriverFactory`**
Problem: SRP broken — mixed driver *lifecycle* management (`getDriver()`/`quit()`) with browser
*config* (`webDriver.manage().window().maximize()`) → unrelated to lifecycle.
Solution: Moved `maximize()` into shared helper on `WebDriverCreator`, called by each creator
right before returning its driver — `WebDriverFactory` now only handles lifecycle.

All 3 fixes are implemented in code and verified passing via full test suite after each change.