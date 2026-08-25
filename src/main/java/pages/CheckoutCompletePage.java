package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class CheckoutCompletePage extends BasePage {

    @FindBy(css = "[data-test='complete-header']")
    private WebElement successMessage;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
    }

    public String getSuccessMessage() {
        String message = getText(successMessage);
        log.info("Success message: {}", message);
        return message;
    }
}
