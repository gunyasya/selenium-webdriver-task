package tests;

import config.ConfigReader;
import model.CheckoutInfo;
import model.Product;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutOverviewPage;
import pages.InventoryPage;
import pages.LoginPage;

import java.time.Duration;
import java.util.List;

public class SingleItemCheckoutTest extends BaseTest {

    private static final Product PRODUCT = new Product("Sauce Labs Backpack", 29.99);

    @Test(groups = {"smoke", "regression"})
    public void shouldCheckoutSingleItem() {
        InventoryPage inventoryPage = new LoginPage(driver)
                .open(ConfigReader.getBaseUrl())
                .login(defaultUser);

        // implicit wait, scoped to just this step: give the product grid time to
        // finish rendering before we interact with it, then reset immediately so
        // it doesn't stack with the explicit waits used everywhere else
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        inventoryPage.addToCart(PRODUCT);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        CartPage cartPage = inventoryPage.goToCart();
        List<String> itemNames = cartPage.getItemNames();
        Assert.assertTrue(itemNames.contains(PRODUCT.name()), "Cart should contain " + PRODUCT.name());

        CheckoutOverviewPage overviewPage = cartPage.proceedToCheckout()
                .fillForm(new CheckoutInfo("John", "Doe", "12345"));

        CheckoutCompletePage completePage = overviewPage.finish();
        Assert.assertEquals(completePage.getSuccessMessage(), "Thank you for your order!");
    }
}