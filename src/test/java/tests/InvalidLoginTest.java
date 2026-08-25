package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.LoginPage;

public class InvalidLoginTest extends BaseTest {

    private static final String INVALID_PASSWORD = "wrong_password";
    private static final String EXPECTED_ERROR =
            "Epic sadface: Username and password do not match any user in this service";

    @Test
    public void shouldRejectInvalidLoginThenAcceptValidCredentials() {
        LoginPage loginPage = new LoginPage(driver)
                .open()
                .attemptLogin(VALID_USERNAME, INVALID_PASSWORD);

        Assert.assertEquals(loginPage.getErrorMessage(), EXPECTED_ERROR);
        Assert.assertFalse(driver.getCurrentUrl().contains("inventory"), "Should still be on login page");

        loginPage.login(VALID_USERNAME, VALID_PASSWORD);
        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"), "Should be redirected to inventory page");
    }
}