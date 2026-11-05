package tests;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import base.BaseTest;
import pages.HomePage;
import pages.SearchResultsPage;
import utils.ConfigReader;

/** Task 3: search automation. */
public class SearchTest extends BaseTest {

    private HomePage home;

    // runs after BaseTest.setUp(), so the browser already exists
    @BeforeMethod(dependsOnMethods = "setUp")
    public void logIn() {
        home = loginAsTestUser();
    }

    @Test(description = "TC03 - Searching shows a results page with visible results")
    public void searchShowsResults() {
        Assert.assertTrue(home.isLoaded(), "Not logged in, cannot search");

        SearchResultsPage results = home.search(ConfigReader.get("search.term"));

        Assert.assertTrue(results.isDisplayed(), "Results page did not show a visible result element");
        Assert.assertTrue(driver.getCurrentUrl().contains("/search/results"),
            "URL is not a search results URL: " + driver.getCurrentUrl());
    }
}
