package pages;

import lombok.extern.slf4j.Slf4j;
import model.User;
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

    public InventoryPage login(User user) {
        log.info("Logging in as: {}", user.username());
        enterCredentials(user);
        urlContains(PageUrls.INVENTORY);
        return new InventoryPage(driver);
    }

    public LoginPage attemptLogin(User user) {
        log.info("Attempting login as: {}", user.username());
        enterCredentials(user);
        return this;
    }

    public String getErrorMessage() {
        return getText(errorMessage);
    }

    public LoginPage open(String baseUrl) {
        log.info("Opening login page: {}", baseUrl);
        driver.get(baseUrl);
        return this;
    }

    private void enterCredentials(User user) {
        typeText(usernameField, user.username());
        typeText(passwordField, user.password());
        click(loginButton);
    }
}
