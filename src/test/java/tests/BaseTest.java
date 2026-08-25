package tests;

import driver.Browser;
import driver.BrowserContext;
import driver.WebDriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected static final String VALID_USERNAME = "standard_user";
    protected static final String VALID_PASSWORD = "secret_sauce";

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        BrowserContext.setBrowser(Browser.CHROME);
        driver = WebDriverFactory.getDriver();
    }

    @AfterMethod
    public void tearDown() {
        WebDriverFactory.quit();
    }
}