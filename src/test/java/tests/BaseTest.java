package tests;

import config.ConfigReader;
import driver.BrowserContext;
import driver.WebDriverFactory;
import model.User;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public abstract class BaseTest {

    protected WebDriver driver;
    protected User defaultUser;

    @BeforeMethod
    public void setUp() {
        BrowserContext.initFromSystemProperty();
        driver = WebDriverFactory.getDriver();
        defaultUser = ConfigReader.getDefaultUser();
    }

    @AfterMethod
    public void tearDown() {
        WebDriverFactory.quit();
    }
}