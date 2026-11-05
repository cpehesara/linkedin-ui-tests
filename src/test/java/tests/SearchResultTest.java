package tests;

import java.time.Duration;

import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;

/** Task 4: click the first search result and check the right page opened. */
public class SearchResultTest extends BaseTest {

    private HomePage home;

    @BeforeMethod(dependsOnMethods = "setUp")
    public void logIn() {
        home = loginAsTestUser();
    }

    @Test(description = "TC04 - First search result opens the matching profile/company page")
    public void firstResultOpensCorrectPage() {
        Assert.assertTrue(home.isLoaded(), "Not logged in, cannot search");

        SearchResultsPage results = home.search(ConfigReader.get("search.company"));
        Assert.assertTrue(results.isDisplayed(), "Results page did not load");

        // remember where the first result points BEFORE clicking it
        String expectedHref = results.getFirstResultHref();
        String expectedPath = SearchResultsPage.pathOf(expectedHref);   // e.g. /company/microsoft

        results.clickFirstResult();

        // wait until the browser is actually on that page
        new WebDriverWait(driver, Duration.ofSeconds(20))
            .until((ExpectedCondition<Boolean>) d -> d.getCurrentUrl().toLowerCase().contains(expectedPath));

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains(expectedPath),
            "Expected URL to contain " + expectedPath + " but was " + driver.getCurrentUrl());
        Assert.assertTrue(
            driver.getCurrentUrl().matches("(?i).*/(in|company|school)/.*"),
            "Opened page is not a profile, company or school page");
        Assert.assertTrue(results.isHeadingVisible(), "Page heading (h1) is not visible");
        System.out.println("Opened page heading: " + results.getHeadingText());
    }
}
