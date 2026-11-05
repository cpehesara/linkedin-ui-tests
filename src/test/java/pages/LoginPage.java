package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ConfigReader;
import utils.ScreenshotUtil;

import java.time.Duration;

public class LoginPage extends BasePage {

    // LinkedIn redesigned the login page (the old id="username" / id="password" no longer match),
    // so these locators do not depend on ids. Each one is an XPath union ( | ): the first part
    // covers the usual attributes, the second part finds the input by the label text we can SEE on the page.
    private final By emailField = By.xpath(
            "//input[@id='username' or @name='session_key' or @autocomplete='username' or @type='email']"
                    + " | //label[contains(normalize-space(.),'Email or phone')]/following::input[1]");

    private final By passwordField = By.xpath(
            "//input[@id='password' or @name='session_password' or @type='password' or @autocomplete='current-password']"
                    + " | //label[normalize-space(.)='Password']/following::input[1]");

    // The main blue button has the exact text "Sign in" ("Sign in with Google" etc. are longer, so they do not match)
    private final By signInButton = By.xpath(
            "//button[normalize-space(.)='Sign in' or @aria-label='Sign in' or @data-litms-control-urn='login-submit']");

    // Error after a wrong password: LinkedIn's error ids / alert role, or the visible message text.
    private final By loginError = By.xpath(
            "//*[@id='error-for-password' or @id='error-for-username' or @role='alert']"
                    + " | //*[contains(normalize-space(text()),'not the right password')"
                    + " or contains(normalize-space(text()),'Wrong email or password')"
                    + " or contains(normalize-space(text()),'incorrect')"
                    + " or contains(normalize-space(text()),'try again')]");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.get("https://www.linkedin.com/login");
        waitVisible(emailField);
    }

    public void login(String email, String password) {
        type(emailField, email);
        type(passwordField, password);
        click(signInButton);
    }

    public boolean isLoginErrorDisplayed() {
        return isVisible(loginError);
    }

    public String getLoginErrorText() {
        return waitVisible(loginError).getText().trim();
    }

    /**
     * After clicking Sign in, LinkedIn either goes to the feed or to a security checkpoint
     * (CAPTCHA, email code, "are you human" puzzle).
     *
     * This method does NOT try to bypass anything. If a checkpoint shows up it takes a screenshot
     * for the report, prints a message, and waits so that YOU can finish the step by hand
     * in the browser window. Once you do, the test carries on.
     */
    public void handleSecurityCheckIfPresent() {
        // 1) wait until we leave the login page (feed, or a checkpoint)
        try {
            new WebDriverWait(driver, Duration.ofSeconds(20)).until((ExpectedCondition<Boolean>) d -> {
                String url = d.getCurrentUrl();
                return url.contains("/feed") || isCheckpoint(url);
            });
        } catch (TimeoutException e) {
            return; // still on /login, probably wrong credentials; the calling test will assert on that
        }

        // 2) if it is a checkpoint, give the human time to solve it
        if (isCheckpoint(driver.getCurrentUrl())) {
            int seconds = ConfigReader.getInt("manual.verification.timeout.seconds", 120);
            ScreenshotUtil.capture(driver, "SECURITY_CHECK_detected");
            System.out.println("\n>>> LinkedIn security check detected.");
            System.out.println(">>> Please complete it manually in the browser window. Waiting up to "
                    + seconds + " seconds...\n");
            new WebDriverWait(driver, Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.urlContains("/feed"));
        }
    }

    private boolean isCheckpoint(String url) {
        return url.contains("checkpoint") || url.contains("challenge") || url.contains("authwall");
    }
}