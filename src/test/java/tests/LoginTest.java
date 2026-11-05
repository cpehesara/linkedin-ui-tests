package tests;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.HomePage;
import pages.LoginPage;
import utils.ConfigReader;

/** Task 2: login automation (one positive and one negative test). */
public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "TC01 - Valid login lands on the feed")
    public void validLogin() {
        HomePage home = loginAsTestUser();

        Assert.assertTrue(home.isLoaded(), "Feed URL / search box not found after login");
        Assert.assertTrue(driver.getCurrentUrl().contains("/feed"), "URL does not contain /feed");
    }

    @Test(priority = 2, description = "TC02 - Invalid password shows an error message")
    public void invalidPasswordShowsError() {
        LoginPage login = new LoginPage(driver);
        login.open();
        login.login(ConfigReader.get("linkedin.email"), "Wrong#Password123");

        Assert.assertTrue(login.isLoginErrorDisplayed(), "No error message displayed for a wrong password");
        Assert.assertTrue(driver.getCurrentUrl().contains("login"), "Should still be on the login page");
    }
}
