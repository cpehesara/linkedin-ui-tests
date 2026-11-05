package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;

/** The LinkedIn feed (home) page that you land on after logging in. */
public class HomePage extends BasePage {

    // The global search box. No id, so a CSS selector: first the BEM class, then the placeholder as a backup.
    private final By searchBox = By.xpath(
            "//input[contains(@class,'search-global-typeahead__input') or @placeholder='Search'"
                    + " or @aria-label='Search' or @role='combobox']");

    // The top navigation bar (used as a second "login worked" signal)
    private final By navBar = By.cssSelector("#global-nav, nav.global-nav, header[role='banner'], nav[aria-label='Primary']");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** Login succeeded if we are on /feed and the search box is visible. */
    public boolean isLoaded() {
        waitForUrlContains("/feed");
        return isVisible(searchBox);
    }

    public boolean isNavBarVisible() {
        return isVisible(navBar);
    }

    /** Task 3, steps 1 to 3: click the search box, type the term, press Enter. */
    public SearchResultsPage search(String term) {
        click(searchBox);
        type(searchBox, term);
        waitVisible(searchBox).sendKeys(Keys.ENTER);
        return new SearchResultsPage(driver);
    }
}