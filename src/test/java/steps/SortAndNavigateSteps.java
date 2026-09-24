package steps;

import config.ConfigReader;
import driver.WebDriverFactory;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailPage;

import java.util.Comparator;
import java.util.List;

public class SortAndNavigateSteps {

    private WebDriver driver;
    private InventoryPage inventoryPage;
    private ProductDetailPage detailPage;
    private String expectedName;
    private Double expectedPrice;

    @Before(order = 1)
    public void setDriver() {
        driver = WebDriverFactory.getDriver();
    }

    @Given("^user is logged in$")
    public void userIsLoggedIn() {
        inventoryPage = new LoginPage(driver)
                .open(ConfigReader.getBaseUrl())
                .login(ConfigReader.getDefaultUser());
    }

    @When("^user sorts products by \"([^\"]+)\"$")
    public void userSortsProductsBy(String sortOption) {
        inventoryPage.sortBy(sortOption);
    }

    @Then("^products should be displayed in \"([^\"]+)\" sorted order$")
    public void productsShouldBeSorted(String sortOption) {
        if (sortOption.contains("Price")) {
            List<Double> prices = inventoryPage.getItemPrices();
            List<Double> expected = sortOption.contains("low to high")
                    ? prices.stream().sorted().toList()
                    : prices.stream().sorted(Comparator.reverseOrder()).toList();
            Assert.assertEquals(prices, expected);
        } else if (sortOption.contains("Name")) {
            List<String> names = inventoryPage.getItemNames();
            List<String> expected = sortOption.contains("A to Z")
                    ? names.stream().sorted().toList()
                    : names.stream().sorted(Comparator.reverseOrder()).toList();
            Assert.assertEquals(names, expected);
        } else {
            throw new IllegalArgumentException("Unknown sort option: " + sortOption);
        }
    }

    @When("^user views details for the first product$")
    public void userViewsDetailsForFirstProduct() {
        expectedName = inventoryPage.getFirstProductName();
        expectedPrice = inventoryPage.getItemPrices().get(0);
        detailPage = inventoryPage.viewFirstProduct();
    }

    @Then("^product detail page should display matching name and price$")
    public void productDetailPageShouldMatch() {
        Assert.assertEquals(detailPage.getProductName(), expectedName);
        Assert.assertEquals(detailPage.getProductPrice(), expectedPrice);
    }

    @When("^user navigates back to product inventory page$")
    public void userNavigatesBack() {
        inventoryPage = detailPage.backToInventory();
    }

    @Then("^product inventory page should be displayed again$")
    public void productInventoryPageShouldBeDisplayed() {
        Assert.assertTrue(inventoryPage.isDisplayed());
    }
}