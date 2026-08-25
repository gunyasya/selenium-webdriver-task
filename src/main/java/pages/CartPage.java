package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

@Slf4j
public class CartPage extends BasePage {
    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> cartItemNames;

    @FindBy(css = "[data-test='checkout']")
    private WebElement checkoutButton;

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getItemNames() {
        log.info("Getting items names");
        wait.until(ExpectedConditions.visibilityOfAllElements(cartItemNames));
        return cartItemNames.stream()
                .map(this::getText)
                .toList();
    }

    public CheckoutInfoPage proceedToCheckout() {
        log.info("Proceeding to checkout");
        click(checkoutButton);
        urlContains(PageUrls.CHECKOUT_STEP_ONE);
        return new CheckoutInfoPage(driver);
    }
}
