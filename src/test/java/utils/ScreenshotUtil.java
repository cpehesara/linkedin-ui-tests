package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/** Saves a PNG of the current browser window into the screenshots/ folder. */
public final class ScreenshotUtil {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ScreenshotUtil() { }

    public static Path capture(WebDriver driver, String name) {
        if (driver == null) return null;
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", name + "_" + LocalDateTime.now().format(STAMP) + ".png");
            Files.createDirectories(dest.getParent());
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("[screenshot] " + dest.toAbsolutePath());
            return dest;
        } catch (Exception e) {
            System.out.println("[screenshot] could not capture: " + e.getMessage());
            return null;
        }
    }
}
