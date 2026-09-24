package listeners;

import com.epam.reportportal.listeners.LogLevel;
import driver.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;


@Slf4j
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        attachScreenshot(result, LogLevel.INFO);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshot(result, LogLevel.ERROR);
    }

    private void attachScreenshot(ITestResult result, LogLevel level) {
        WebDriver driver;
        try {
            driver = WebDriverFactory.getDriver();
        } catch (IllegalStateException e) {
            log.warn("Skipping screenshot, browser not available: {}", result.getName());
            return;
        }
        ScreenshotReporter.attach(driver, result.getName(), level);
    }
}
