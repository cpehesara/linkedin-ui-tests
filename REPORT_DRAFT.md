---
title: "IT3153 Software Quality Assurance: Assignment 04"
subtitle: "Automated UI Test Suite for LinkedIn (Java, Selenium WebDriver, TestNG)"
author: "KMCP Munasinghe | D/BIT/24/0011 | Intake 41"
---

> **How to finish this report:** every line marked **[FILL]** or **[EDIT]** needs your real results from running the tests yourself. Delete this note and keep the final report to 2 to 4 pages. Paste the screenshots in section 3, then export to PDF.

## 1. Test design

The suite automates three LinkedIn flows (login, search, open a search result) with Selenium WebDriver and TestNG, written in Java 17 and built with Maven. I used the **Page Object Model**: each page has its own class that holds its locators and actions, so the tests read like plain steps and a broken locator only has to be fixed in one place.

- `BaseTest` starts a fresh Chrome before every test (`@BeforeMethod`) and closes it afterwards (`@AfterMethod`). Selenium Manager finds ChromeDriver automatically.
- `BasePage` holds all the waiting logic: `WebDriverWait` with `ExpectedConditions`, plus one `FluentWait` helper. There is no `Thread.sleep()`.
- `TestListener` (a TestNG `ITestListener`) takes a screenshot automatically on pass and on failure.
- Credentials live in a git-ignored `config.properties`, never in the code.
- TestNG features used: `@Test(priority, description)`, `@BeforeMethod`, `@AfterMethod(alwaysRun)`, `@Listeners`, `Assert`, and a `testng.xml` suite file.

## 2. Test cases

| ID | Description | Steps | Expected result | Actual result | Pass/Fail |
|----|-------------|-------|-----------------|---------------|-----------|
| TC01 | Valid login | 1. Open /login 2. Enter valid email and password 3. Click Sign in | URL contains `/feed` and the search box is visible | **[FILL]** | **[FILL]** |
| TC02 | Invalid password | 1. Open /login 2. Enter valid email, wrong password 3. Click Sign in | An error message is displayed and the user stays on the login page | **[FILL]** | **[FILL]** |
| TC03 | Search | 1. Log in 2. Click the search box 3. Type "software testing" 4. Press Enter | URL contains `/search/results` and at least one result is visible | **[FILL]** | **[FILL]** |
| TC04 | Open first result | 1. Log in 2. Search "Microsoft" 3. Read the first result's link 4. Click it | URL contains the clicked link's path (e.g. `/company/microsoft`) and the page `h1` is visible | **[FILL]** | **[FILL]** |

## 3. Screenshots

Insert here (arrange them small so the report stays within 4 pages):

1. **[FILL]** Eclipse TestNG results view (or the `mvn test` console) showing all tests and the pass/fail count.
2. **[FILL]** TestNG HTML report (`test-output/index.html`).
3. **[FILL]** A screenshot captured automatically by the code from the `screenshots/` folder (for example `FAIL_...png`, produced by deliberately breaking one assertion for a single run, or a `PASS_...png`). Say which one it is.
4. **[FILL, if it happened]** The security checkpoint screenshot (`SECURITY_CHECK_detected_...png`).

Blur your name, email and profile photo in every screenshot.

## 4. Locators chosen and why

| Element | Locator | Why |
|---------|---------|-----|
| Email field | `By.id("username")` | An id is unique and the most stable kind of locator |
| Password field | `By.id("password")` | Same reason |
| Sign-in button | CSS `form.login__form button[type='submit']` | No id, but it is the only submit button in the login form |
| Login error | CSS `#error-for-password, #error-for-username, [role='alert']` | The id is LinkedIn's own error element; the extra options cover the unknown-email case |
| Search box | CSS `input.search-global-typeahead__input, input[placeholder='Search']` | No id; the placeholder is a backup if the class name changes |
| First result | XPath `(//main//a[contains(@href,'/in/') or contains(@href,'/company/') or contains(@href,'/school/')])[1]` | LinkedIn's CSS class names are obfuscated and change often, but the link's `href` pattern is stable |
| Page heading | `By.tagName("h1")` | Profile and company pages both have one main heading |

**[EDIT]** If you had to change any locator because LinkedIn's page was different, list it here with the old value, the new value and the date.

## 5. Challenges and how I handled them

- **Dynamic and obfuscated elements.** LinkedIn's class names are auto-generated and change. I avoided them and used ids, attributes and `href` patterns instead.
- **Timing.** Pages load in stages and re-render, so clicking straight away fails. I used explicit waits (visible, clickable, URL contains) and a `FluentWait` with polling that ignores stale-element errors.
- **Bot detection and CAPTCHA.** I did not try to bypass it. When a checkpoint appears the test saves a screenshot, prints a message and waits so I can complete it by hand, then continues. **[EDIT: say whether a checkpoint actually appeared during your runs, and what you saw.]**
- **Login security.** Credentials are in a git-ignored properties file, not in the source, and the screen recording hides personal information. I used my own account only.
- **Unstable page structure.** **[EDIT: describe any locator that broke, or write that none did.]**

## 6. Reflection: limits of UI automation on a site you do not control

Testing a site I do not own means my tests depend on things I cannot see or change. LinkedIn can change its HTML at any time, so locators can break without warning and the tests then fail even though the product still works. Bot detection and CAPTCHAs can block the run, and the terms of use restrict automated access, so the suite cannot safely run often or in CI. Results also depend on the account (its feed, its search history, rate limits), so the same test can behave differently on different days. There is no test data or environment of my own to reset. For real quality work, a test environment, stable test hooks, or an official API would be far more reliable; UI automation against a live third-party site is best treated as a learning exercise or a small smoke check.
