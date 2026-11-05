package listeners;

import org.testng.ITestListener;
import org.testng.ITestResult;

import base.BaseTest;
import utils.ScreenshotUtil;

/**
 * TestNG listener. Takes a screenshot automatically when a test fails
 * (FAIL_...) and also when it passes (PASS_...), so you have evidence for the report.
 * Attached through @Listeners on BaseTest.
 */
public class TestListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[START] " + result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[PASS ] " + result.getMethod().getDescription());
        capture(result, "PASS");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.out.println("[FAIL ] " + result.getMethod().getDescription()
            + " -> " + result.getThrowable());
        capture(result, "FAIL");
    }

    private void capture(ITestResult result, String prefix) {
        Object instance = result.getInstance();
        if (instance instanceof BaseTest bt) {
            ScreenshotUtil.capture(bt.getDriver(), prefix + "_" + result.getName());
        }
    }
}
