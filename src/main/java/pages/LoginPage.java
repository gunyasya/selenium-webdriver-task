package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@Slf4j
public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameField;

    @FindBy(id = "password")
    private WebElement passwordField;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage login(String username, String password) {
        log.info("Logging in as: {}", username);
        enterCredentials(username, password);
        urlContains(PageUrls.INVENTORY);
        return new InventoryPage(driver);
    }

    public LoginPage attemptLogin(String username, String password) {
        log.info("Attempting login as: {}", username);
        enterCredentials(username, password);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public LoginPage open() {
        log.info("Opening login page");
        driver.get(PageUrls.BASE_URL);
        return this;
    }

    private void enterCredentials(String username, String password) {
        typeText(usernameField, username);
        typeText(passwordField, password);
        click(loginButton);
    }
}
