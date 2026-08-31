package driver;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.openqa.selenium.WebDriver;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WebDriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        if (DRIVER.get() == null) {
            initDriver();
        }
        return DRIVER.get();
    }

    private static void initDriver() {
        Browser browser = BrowserContext.getBrowser();
        WebDriver webDriver = browser.getDriverCreator().createDriver();
        DRIVER.set(webDriver);
    }

    public static void quit() {
        if (DRIVER.get() == null) {
            return;
        }
        try {
            DRIVER.get().quit();
        } finally {
            DRIVER.remove();
            BrowserContext.clear();
        }
    }
}
