package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


@Slf4j
public abstract class BasePage {
    private static final Duration DEFAULT_WAIT_DURATION = Duration.ofSeconds(10);

    protected WebDriver driver;
    protected Wait<WebDriver> wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new FluentWait<>(driver)
                .withTimeout(DEFAULT_WAIT_DURATION)
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
        PageFactory.initElements(driver, this);
    }

    protected void typeText(WebElement element, String text) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
        element.sendKeys(text);
        log.debug("Typed '{}' into element: {}", text, element);
    }

    protected void click(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        log.debug("Clicked on {} element", element);
    }

    protected String getText(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
        String text = element.getText();
        log.debug("Read text '{}' from element", text);
        return text;
    }

    protected void urlContains(String urlPart) {
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

}
