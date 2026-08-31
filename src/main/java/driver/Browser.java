package driver;

import driver.creator.ChromeDriverCreator;
import driver.creator.FirefoxDriverCreator;
import driver.creator.WebDriverCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Browser {

    CHROME(new ChromeDriverCreator()),
    FIREFOX(new FirefoxDriverCreator());

    private final WebDriverCreator driverCreator;
}
