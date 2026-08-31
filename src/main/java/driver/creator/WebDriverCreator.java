package driver.creator;

import org.openqa.selenium.WebDriver;

public abstract class WebDriverCreator {

    public abstract WebDriver createDriver();

    protected static boolean isHeadless() {
        String property = System.getProperty("headless");
        if (property != null) {
            return Boolean.parseBoolean(property);
        } else return System.getenv("CI") != null;
    }

    protected static void maximize(WebDriver driver) {
        driver.manage().window().maximize();
    }
}
