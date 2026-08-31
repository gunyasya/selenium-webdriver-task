package driver.options;

import org.openqa.selenium.chrome.ChromeOptions;

public class IncognitoOptionsDecorator implements ChromeOptionsProvider {

    private final ChromeOptionsProvider provider;

    public IncognitoOptionsDecorator(ChromeOptionsProvider provider) {
        this.provider = provider;
    }

    @Override
    public ChromeOptions getOptions() {
        return provider.getOptions().addArguments("--incognito");
    }
}
