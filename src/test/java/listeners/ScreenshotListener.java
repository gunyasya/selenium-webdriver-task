package listeners;

import driver.WebDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver;
        try {
            driver = WebDriverFactory.getDriver();
        } catch (IllegalStateException e) {
            log.warn("Skipping screenshot, browser not available: {}", result.getName());
            return;
        }
        TakesScreenshot ts = (TakesScreenshot) driver;
        File screenshot = ts.getScreenshotAs(OutputType.FILE);
        Date d = new Date();
        String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".png";
        Path destination = Paths.get("screenshots", result.getName() + "_" + fileName);
        try {
            Path parent = Files.createDirectories(destination.getParent());
            Files.copy(screenshot.toPath(), destination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.error("Screenshot on failure info: {}, {}", result.getName(), destination);
    }
}
