package pages;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
public class InventoryPage extends BasePage {

    private static final By INVENTORY_ITEM_NAME = By.cssSelector("[data-test='inventory-item-name']");
    private static final By ADD_TO_CART_BUTTON = By.cssSelector("[data-test^='add-to-cart']");

    @FindBy(className = "inventory_item")
    private List<WebElement> inventoryItems;

    @FindBy(className = "shopping_cart_link")
    private WebElement cartButton;

    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdown;

    @FindBy(css = "[data-test='inventory-item-price']")
    private List<WebElement> itemPrices;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    public InventoryPage addToCart(String productName) {
        log.info("Adding product to cart: {}", productName);
        WebElement inventoryItem = visibleItems().stream()
                .filter(it -> productName.equals(getText(it.findElement(INVENTORY_ITEM_NAME))))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Product not found: " + productName));

        click(inventoryItem.findElement(ADD_TO_CART_BUTTON));
        return this;
    }

    public CartPage goToCart() {
        log.info("Navigating to cart");
        click(cartButton);
        urlContains(PageUrls.CART);
        return new CartPage(driver);
    }

    public InventoryPage sortByPriceLowToHigh() {
        log.info("Sorting products by price: low to high");
        wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        new Select(sortDropdown).selectByVisibleText("Price (low to high)");
        return this;
    }

    public List<Double> getItemPrices() {
        log.info("Getting item prices");
        wait.until(ExpectedConditions.visibilityOfAllElements(itemPrices));
        return itemPrices.stream()
                .map(this::getText)
                .map(this::parsePrice)
                .toList();
    }

    public ProductDetailPage viewFirstProduct() {
        log.info("Viewing first listed product");
        click(firstItemName());
        return new ProductDetailPage(driver);
    }

    public String getFirstProductName() {
        return getText(firstItemName());
    }

    private List<WebElement> visibleItems() {
        wait.until(ExpectedConditions.visibilityOfAllElements(inventoryItems));
        return inventoryItems;
    }

    private WebElement firstItemName() {
        return visibleItems().get(0).findElement(INVENTORY_ITEM_NAME);
    }
}
