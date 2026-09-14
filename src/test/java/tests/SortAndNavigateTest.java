package tests;

import config.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailPage;

import java.util.List;

public class SortAndNavigateTest extends BaseTest {

    @Test(groups = {"regression"})
    public void shouldSortViewDetailsAndNavigateBack() {
        InventoryPage inventoryPage = new LoginPage(driver)
                .open(ConfigReader.getBaseUrl())
                .login(defaultUser)
                .sortBy("Price (low to high)");

        List<Double> prices = inventoryPage.getItemPrices();
        List<Double> sortedPrices = prices.stream().sorted().toList();
        Assert.assertEquals(prices, sortedPrices, "Prices should be sorted ascending");

        String expectedName = inventoryPage.getFirstProductName();
        Double expectedPrice = prices.get(0);

        ProductDetailPage detailPage = inventoryPage.viewFirstProduct();
        Assert.assertEquals(detailPage.getProductName(), expectedName);
        Assert.assertEquals(detailPage.getProductPrice(), expectedPrice);

        driver.navigate().back();
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Should be back on inventory page");
    }
}