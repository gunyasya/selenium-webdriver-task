package tests;

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

    private static final String PRODUCT = "Sauce Labs Backpack";

    @Test
    public void shouldCheckoutSingleItem() {
        InventoryPage inventoryPage = new LoginPage(driver)
                .open()
                .login(VALID_USERNAME, VALID_PASSWORD);

        // implicit wait, scoped to just this step: give the product grid time to
        // finish rendering before we interact with it, then reset immediately so
        // it doesn't stack with the explicit waits used everywhere else
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
        inventoryPage.addToCart(PRODUCT);
        driver.manage().timeouts().implicitlyWait(Duration.ZERO);

        CartPage cartPage = inventoryPage.goToCart();
        List<String> itemNames = cartPage.getItemNames();
        Assert.assertTrue(itemNames.contains(PRODUCT), "Cart should contain " + PRODUCT);

        CheckoutOverviewPage overviewPage = cartPage.proceedToCheckout()
                .fillForm("John", "Doe", "12345");

        CheckoutCompletePage completePage = overviewPage.finish();
        Assert.assertEquals(completePage.getSuccessMessage(), "Thank you for your order!");
    }
}