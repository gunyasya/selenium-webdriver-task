package driver.creator;

import driver.options.BaseChromeOptionsProvider;
import driver.options.ChromeOptionsProvider;
import driver.options.HeadlessOptionsDecorator;
import driver.options.IncognitoOptionsDecorator;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeDriverCreator extends WebDriverCreator {
    @Override
    public WebDriver createDriver() {
        ChromeOptionsProvider provider = new BaseChromeOptionsProvider();
        WebDriverManager.chromedriver().setup();
        if (isHeadless()) {
            provider = new HeadlessOptionsDecorator(provider);
        }
        provider = new IncognitoOptionsDecorator(provider);
        ChromeOptions options = provider.getOptions();
        WebDriver driver = new ChromeDriver(options);
        maximize(driver);
        return driver;
    }
}
