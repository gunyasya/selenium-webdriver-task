package pages;

import lombok.extern.slf4j.Slf4j;
import model.CheckoutInfo;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class CheckoutInfoPage extends BasePage {

    @FindBy(css = "[data-test='firstName']")
    private WebElement firstNameField;

    @FindBy(css = "[data-test='lastName']")
    private WebElement lastNameField;

    @FindBy(css = "[data-test='postalCode']")
    private WebElement postalCode;

    @FindBy(css = "[data-test='continue']")
    private WebElement continueButton;

    public CheckoutInfoPage(WebDriver driver) {
        super(driver);
    }

    public CheckoutOverviewPage fillForm(CheckoutInfo checkoutInfo) {
        log.info("Filling the form with: {} {} {}", checkoutInfo.firstName(), checkoutInfo.lastName(), checkoutInfo.zipCode());
        typeText(firstNameField, checkoutInfo.firstName());
        typeText(lastNameField, checkoutInfo.lastName());
        typeText(postalCode, checkoutInfo.zipCode());
        click(continueButton);
        urlContains(PageUrls.CHECKOUT_STEP_TWO);
        return new CheckoutOverviewPage(driver);
    }
}
