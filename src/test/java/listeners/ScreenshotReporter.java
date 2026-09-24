package listeners;

import com.epam.reportportal.listeners.LogLevel;
import com.epam.reportportal.service.ReportPortal;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import utils.ScreenshotUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Slf4j
public class ScreenshotReporter {
    public static void attach(WebDriver driver, String name, LogLevel level) {
        byte[] screenshot = ScreenshotUtils.capture(driver);
        Date d = new Date();
        String fileName = d.toString().replace(":", "_").replace(" ", "_") + ".png";
        Path destination = Paths.get("screenshots", name + "_" + fileName);
        try {
            Files.createDirectories(destination.getParent());
            Files.write(destination, screenshot);
            ReportPortal.emitLog(name, level.name(), new Date(), destination.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (level == LogLevel.ERROR) {
            log.error("Test failed, screenshot saved: {}", destination);
        } else {
            log.info("Screenshot saved: {}", destination);
        }
    }
}
