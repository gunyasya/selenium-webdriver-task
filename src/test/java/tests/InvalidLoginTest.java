package tests;

import config.ConfigReader;
import model.User;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class InvalidLoginTest extends BaseTest {

    private static final String INVALID_PASSWORD = "wrong_password";
    private static final String EXPECTED_ERROR =
            "Epic sadface: Username and password do not match any user in this service";

    @Test(groups = {"regression"})
    public void shouldRejectInvalidLoginThenAcceptValidCredentials() {
        User invalidUser = new User(defaultUser.username(), INVALID_PASSWORD);
        LoginPage loginPage = new LoginPage(driver)
                .open(ConfigReader.getBaseUrl())
                .attemptLogin(invalidUser);

        Assert.assertEquals(loginPage.getErrorMessage(), EXPECTED_ERROR);
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory"), "Should still be on login page");

        loginPage.login(defaultUser);
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Should be redirected to inventory page");
    }
}