package steps;

import driver.Browser;
import driver.BrowserContext;
import driver.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        BrowserContext.setBrowser(Browser.valueOf(System.getProperty("browser", "chrome").toUpperCase()));
        WebDriverFactory.getDriver();
    }

    @After
    public void tearDown() {
        WebDriverFactory.quit();
    }
}