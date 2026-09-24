package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import utils.PriceParser;

import java.util.List;

@Slf4j
public class CheckoutOverviewPage extends BasePage {

    @FindBy(className = "inventory_item_price")
    private List<WebElement> cartItemPrices;

    @FindBy(xpath = "//div[@data-test='subtotal-label']")
    private WebElement subTotalPrice;

    @FindBy(css = "[data-test='finish']")
    private WebElement finishButton;

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
    }

    public List<Double> getItemPrices() {
        log.info("Getting item prices");
        return readPrices(cartItemPrices);
    }


    public Double getSubTotal() {
        log.info("Getting items subtotal price");
        return PriceParser.parsePrice(getText(subTotalPrice));
    }

    public CheckoutCompletePage finish() {
        log.info("Finishing checkout");
        click(finishButton);
        urlContains(PageUrls.CHECKOUT_COMPLETE);
        return new CheckoutCompletePage(driver);
    }
}