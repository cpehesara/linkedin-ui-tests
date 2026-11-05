package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.net.URI;

/** The search results page (URL contains /search/results/). */
public class SearchResultsPage extends BasePage {

    // Any visible result link (person, company or school) inside the main content area.
    // LinkedIn's CSS class names are obfuscated and change often, so we anchor on the stable part: the link's href.
    private final By firstResultLink = By.xpath(
            "(//main//a[contains(@href,'/in/') or contains(@href,'/company/') or contains(@href,'/school/')])[1]");

    // Page heading on the opened profile / company page.
    // A plain <h1> was NOT visible on LinkedIn's current layout (TC04 failed on it in the first run),
    // so we accept an h1, an h2, or anything marked role="heading", whichever is visible first.
    private final By pageHeading = By.xpath("//h1 | //h2 | //*[@role='heading']");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    /** Task 3 check: URL is a results URL and at least one result element is visible. */
    public boolean isDisplayed() {
        wait.until(ExpectedConditions.urlContains("/search/results"));
        return isVisible(firstResultLink);
    }

    /** The href of the first result, read BEFORE clicking so the test knows what to expect. */
    public String getFirstResultHref() {
        return waitVisible(firstResultLink).getAttribute("href");
    }

    /** Only the path part of a URL, lower-case, no query string and no trailing slash. e.g. /company/microsoft */
    public static String pathOf(String href) {
        String path = URI.create(href).getPath().toLowerCase();
        return path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
    }

    public void clickFirstResult() {
        WebElement first = waitClickable(firstResultLink);
        scrollIntoView(first);
        try {
            first.click();
        } catch (ElementClickInterceptedException e) {
            // something (a sticky header, a popup) is covering it; fall back to a JS click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first);
        }
    }

    public boolean isHeadingVisible() {
        return isVisible(pageHeading);
    }

    public String getHeadingText() {
        return waitVisible(pageHeading).getText().trim();
    }
}