package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Shared helpers for every page object. All waiting happens here, using explicit waits
 * (WebDriverWait / FluentWait). There is no Thread.sleep and no implicit wait anywhere.
 */
public class BasePage {

    protected static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(20);

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
    }

    /**
     * Explicit wait: polls until at least one element matching the locator is visible and returns
     * the first visible one. (Looking at ALL matches, not only the first, means a hidden duplicate
     * earlier in the page cannot block us.)
     */
    protected WebElement waitVisible(By locator) {
        return new WebDriverWait(driver, DEFAULT_TIMEOUT)
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Timed out waiting for a visible element: " + locator)
                .until((ExpectedCondition<WebElement>) d -> firstDisplayed(d, locator));
    }

    private static WebElement firstDisplayed(WebDriver d, By locator) {
        for (WebElement el : d.findElements(locator)) {
            try {
                if (el.isDisplayed()) {
                    return el;
                }
            } catch (StaleElementReferenceException ignored) {
                // page re-rendered while we looked; the next poll will try again
            }
        }
        return null;   // null tells the wait to keep polling
    }

    protected WebElement waitClickable(By locator) {
        WebElement el = waitVisible(locator);
        return wait.until(ExpectedConditions.elementToBeClickable(el));
    }

    protected void click(By locator) {
        waitClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    /** True if an element becomes visible within the timeout, false otherwise (never throws). */
    protected boolean isVisible(By locator) {
        try {
            return waitVisible(locator).isDisplayed();
        } catch (TimeoutException | NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    protected void waitForUrlContains(String fragment) {
        wait.until(ExpectedConditions.urlContains(fragment));
    }

    /**
     * FluentWait example: custom timeout, custom polling interval, and exceptions to ignore.
     * Useful for LinkedIn because its pages re-render and elements can go stale mid-wait.
     */
    protected WebElement fluentWaitFor(By locator, Duration timeout) {
        FluentWait<WebDriver> fluent = new FluentWait<>(driver)
                .withTimeout(timeout)
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        Function<WebDriver, WebElement> visibleElement = d -> firstDisplayed(d, locator);
        return fluent.until(visibleElement);
    }

    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }
}