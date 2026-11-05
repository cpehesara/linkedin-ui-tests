# LinkedIn UI Test Suite (IT3153 Software Quality Assurance, Assignment 04)

Automated UI tests for LinkedIn written in **Java 17**, **Selenium WebDriver 4** and **TestNG**, using the **Page Object Model**.
It automates three flows: logging in, searching, and opening a search result.

| | |
|---|---|
| Student | KMCP Munasinghe (D/BIT/24/0011) |
| Module | IT3153 Software Quality Assurance |
| Language / tools | Java 17+, Selenium 4.25, TestNG 7.10, Maven, Chrome |

## What is tested

| ID | Test class | What it checks |
|----|-----------|----------------|
| TC01 | `LoginTest.validLogin` | Valid login reaches `/feed` and the search box is visible |
| TC02 | `LoginTest.invalidPasswordShowsError` | Wrong password shows an error and stays on the login page |
| TC03 | `SearchTest.searchShowsResults` | Searching shows a `/search/results` URL and at least one visible result |
| TC04 | `SearchResultTest.firstResultOpensCorrectPage` | Clicking the first result opens the page that link pointed to (URL path + visible `h1`) |

## Project structure

```
linkedin-ui-tests
├── pom.xml                         dependencies (Selenium, TestNG) + surefire config
├── testng.xml                      the suite: LoginTest, SearchTest, SearchResultTest
├── config.properties.example       template for your credentials (committed)
├── config.properties               YOUR credentials (git-ignored, you create it)
├── REPORT_DRAFT.md                 draft of the test report (turn into the final PDF)
└── src/test/java
    ├── base/BaseTest.java          starts Chrome before each test, quits after it
    ├── pages/                      Page Objects: BasePage (waits), LoginPage, HomePage, SearchResultsPage
    ├── tests/                      LoginTest, SearchTest, SearchResultTest
    ├── listeners/TestListener.java auto screenshot on pass and on failure
    └── utils/                      ConfigReader, ScreenshotUtil
```

## Prerequisites

1. **JDK 17 or later** (`java -version`)
2. **Google Chrome** (latest). You do NOT need to download ChromeDriver: Selenium Manager (built into Selenium 4) fetches the matching one automatically.
3. **Maven** (or Eclipse / IntelliJ, which bundle it)
4. A **LinkedIn account you own** (a dedicated test account is best)

## Configure credentials

1. Copy `config.properties.example` to a new file named `config.properties` in the project root (next to `pom.xml`).
2. Fill in your own details:

```properties
linkedin.email=your_test_account_email
linkedin.password=your_test_account_password
search.term=software testing
search.company=Microsoft
manual.verification.timeout.seconds=120
```

`config.properties` is listed in `.gitignore`, so your password is never pushed to GitHub.
You can also use environment variables instead (`LINKEDIN_EMAIL`, `LINKEDIN_PASSWORD`) or `-Dlinkedin.email=... -Dlinkedin.password=...`.

## Run the tests

**Eclipse:** *File > Import > Maven > Existing Maven Projects*, right-click the project, *Maven > Update Project*, then right-click `testng.xml` and choose *Run As > TestNG Suite*.
(Install the TestNG plugin first from *Help > Eclipse Marketplace* if it is missing.)

**Command line:**

```bash
mvn test
```

To run a single class: `mvn test -Dtest=LoginTest` (this ignores `testng.xml`).

## Output

- `screenshots/` : PNG taken automatically by `TestListener` (`PASS_...` after a passing test, `FAIL_...` when a test fails, `SECURITY_CHECK_detected_...` when LinkedIn shows a checkpoint)
- `target/surefire-reports/` and `test-output/` : TestNG HTML and XML reports

## CAPTCHA, verification and security checks

LinkedIn's terms restrict automated access and the site uses bot detection. This project **does not try to bypass or hide automation**.

- If LinkedIn shows a CAPTCHA, email code or other checkpoint after sign-in, the test takes a screenshot, prints a message in the console, and **waits (default 120 s)** so you can complete the step by hand in the open Chrome window. Then it carries on.
- Use only your own account. Do not run the suite repeatedly in a short time: every test logs in from scratch, and many logins in a row (especially wrong-password attempts) can trigger a temporary restriction.
- If your account gets restricted, stop, and document it in the report as a limitation.

## Waits used (no `Thread.sleep`)

- `WebDriverWait` with `ExpectedConditions` (visibility, clickable, URL contains) in `BasePage`
- `FluentWait` with custom polling and ignored exceptions (`BasePage.fluentWaitFor`)
- No implicit wait, so explicit waits are not mixed with it.

## Troubleshooting

| Problem | Fix |
|---|---|
| `config.properties not found` | Create it in the project root (see above), not inside `src` |
| A locator fails after a LinkedIn update | Open DevTools (F12), find the new locator, update it in the page class, and note the change in your report |
| Test times out on `/feed` | A security check probably appeared; solve it manually within the timeout, or increase `manual.verification.timeout.seconds` |
| TC02 passes/fails oddly after many tries | Wait a while; LinkedIn may be rate-limiting login attempts |
| `mvn` cannot download dependencies | Check your internet / proxy; the first run downloads Selenium, TestNG and ChromeDriver |
