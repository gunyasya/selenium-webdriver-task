package driver.options;

import org.openqa.selenium.chrome.ChromeOptions;

public class HeadlessOptionsDecorator implements ChromeOptionsProvider {

    private final ChromeOptionsProvider provider;

    public HeadlessOptionsDecorator(ChromeOptionsProvider provider) {
        this.provider = provider;
    }

    @Override
    public ChromeOptions getOptions() {
        return provider.getOptions().addArguments("--headless=new").addArguments("--no-sandbox").addArguments("--disable-dev-shm-usage");
    }
}
