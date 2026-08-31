package driver.options;

import org.openqa.selenium.chrome.ChromeOptions;

public class BaseChromeOptionsProvider implements ChromeOptionsProvider {
    @Override
    public ChromeOptions getOptions() {
        return new ChromeOptions();
    }
}
