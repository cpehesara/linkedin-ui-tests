package base;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import listeners.TestListener;
import pages.HomePage;
import pages.LoginPage;
import utils.ConfigReader;

/**
 * Parent of every test class.
 * - @BeforeMethod starts a fresh Chrome before each test
 * - @AfterMethod closes it afterwards, even if the test failed
 * Selenium Manager (built into Selenium 4) finds / downloads the matching ChromeDriver by itself.
 */
@Listeners(TestListener.class)
public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        options.addArguments("--lang=en-US");
        driver = new ChromeDriver(options);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }

    /** Logs in with the credentials from config.properties and returns the feed page object. */
    protected HomePage loginAsTestUser() {
        LoginPage login = new LoginPage(driver);
        login.open();
        login.login(ConfigReader.get("linkedin.email"), ConfigReader.get("linkedin.password"));
        login.handleSecurityCheckIfPresent();
        return new HomePage(driver);
    }
}
