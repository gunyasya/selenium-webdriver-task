package driver;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum Browser {

    CHROME(
            () ->

            {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                if (isHeadless()) {
                    options.addArguments("--headless=new");
                }
                options.addArguments("--incognito");
                return new ChromeDriver(options);
            }
    ),

    FIREFOX(
            () ->

            {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions options = new FirefoxOptions();
                if (isHeadless()) {
                    options.addArguments("-headless");
                }
                options.addArguments("-private");
                return new FirefoxDriver(options);
            }
    );

    private final Supplier<WebDriver> webDriverSupplier;

    private static boolean isHeadless() {
        String property = System.getProperty("headless");
        if (property != null) {
            return Boolean.parseBoolean(property);
        } else return System.getenv("CI") != null;
    }
}
