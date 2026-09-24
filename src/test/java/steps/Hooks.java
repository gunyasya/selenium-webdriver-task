package steps;

import com.epam.reportportal.listeners.LogLevel;
import driver.Browser;
import driver.BrowserContext;
import driver.WebDriverFactory;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import listeners.ScreenshotReporter;
import org.openqa.selenium.WebDriver;

public class Hooks {

    @Before(order = 0)
    public void setUp() {
        BrowserContext.setBrowser(Browser.valueOf(System.getProperty("browser", "chrome").toUpperCase()));
        WebDriverFactory.getDriver();
    }

    @After
    public void tearDown(Scenario scenario) {
        WebDriver driver = WebDriverFactory.getDriver();
        ScreenshotReporter.attach(driver, scenario.getName(), scenario.isFailed() ? LogLevel.ERROR : LogLevel.INFO);
        WebDriverFactory.quit();
    }
}