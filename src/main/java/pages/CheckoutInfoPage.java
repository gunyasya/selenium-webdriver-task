package pages;

import lombok.extern.slf4j.Slf4j;
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

    public CheckoutOverviewPage fillForm(String firstName, String lastName, String zipCode) {
        log.info("Filling the form with: {} {} {}", firstName, lastName, zipCode);
        typeText(firstNameField, firstName);
        typeText(lastNameField, lastName);
        typeText(postalCode, zipCode);
        click(continueButton);
        urlContains(PageUrls.CHECKOUT_STEP_TWO);
        return new CheckoutOverviewPage(driver);
    }
}
