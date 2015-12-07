package com.merritt.commons.testing.junit.selenium;

import java.awt.Rectangle;
import java.awt.Robot;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * This class will take a snapshot of the page and write the source of the page out if a test fails.  
 * 
 * This relies on the driver from your test class so you MUST make sure that your WebDriver is declared as static and 
 * that your test class implements {@link WebDriverSeleniumTest}.
 * 
 * To use add the following to your test class:
 *     @Rule
 *     public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule( true );
 *       
 * If you create this class with true do not invoke quit on the driver as this will handle it. 
 * 
 * OR
 *    
 *     @Rule
 *     public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule( false );
 *     
 * If you create this class with false do not invoke quit on the driver in an @After method or at the end of the test method or it will blow up 
 * as this uses the driver to create the screenshot and get the source code but if it has had quit called on it then it is unable to do that.  
 * Therefore, close it in an @AfterClass method.
 * 
 * If you want to take snapshots on success as well call setTakeSnapshotOnSuccess( true );
 * </pre>
 */
public class ScreenshotTestRule extends TestWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenshotTestRule.class);
    private static final String BASE_DIRECTORY_FORMAT = "target/selenium/%s";
    private static final String FAILED_FILE_NAME_PREFIX = "FAILED-";
    private static final String SUCCESS_FILE_NAME_PREFIX = "SUCCESS-";
    private static final String FILE_NAME_FORMAT = "%s%s%s";
    private static final String BASE_FILE_FORMAT = BASE_DIRECTORY_FORMAT + "/" + FILE_NAME_FORMAT;
    private static final String PNG_FILE_FORMAT = BASE_FILE_FORMAT + ".png";
    private static final String HTML_FILE_FORMAT = BASE_FILE_FORMAT + ".html";

    protected WebDriver driver;
    private boolean quitDriverWhenFinished = true;
    private boolean takeSnapshotOnSuccess = false;
    private String testClassName;
    private String testMethodName;
    private Point windowPosition;
    private Dimension windowSize;

    /**
     * @param quitDriverWhenFinished - whether or not to invoke quit on the driver when the test is finished; 
     *                                 if you pass false be sure you are not calling quit on the driver in an @After method
     */
    public ScreenshotTestRule(final boolean quitDriverWhenFinished) {
        this.quitDriverWhenFinished = quitDriverWhenFinished;
    }

    /**
     * This does an actual screenshot of the browser where the WebDriver one only capture the body.  
     * Use this if you need to see the title bar and/or popups.
     * 
     * @param filenamePrefix - adding to the beginning of the methodName in the naming of the file
     *                         can be null but actually prints null, use StringUtils.EMPTY or "" if you want nothing
     * @param filenamePostfix - adding to the end of the methodName in the naming of the file
     *                          can be null but actually prints null, use StringUtils.EMPTY or "" if you want nothing
     */
    public void captureAwtScreenshot(final String filenamePrefix, final String filenamePostfix) {
        final File destinationFile = new File(String.format(PNG_FILE_FORMAT, testClassName, filenamePrefix, testMethodName, filenamePostfix));
        captureAwtScreenshotTo(destinationFile);
    }

    /**
     * The WebDriver screenshots are only of the body.  If you need to see popups or the title bar then use the awt one.
     * 
     * @param filenamePrefix - adding to the beginning of the methodName in the naming of the file
     *                         can be null but actually prints null, use StringUtils.EMPTY or "" if you want nothing
     * @param filenamePostfix - adding to the end of the methodName in the naming of the file
     *                          can be null but actually prints null, use StringUtils.EMPTY or "" if you want nothing
     */
    public void captureWebDriverScreenshot(final String filenamePrefix, final String filenamePostfix) {
        final File destinationFile = new File(String.format(PNG_FILE_FORMAT, testClassName, filenamePrefix, testMethodName, filenamePostfix));
        captureWebDriverScreenshotTo(destinationFile);
    }

    public boolean isTakeSnapshotOnSuccess() {
        return takeSnapshotOnSuccess;
    }

    public void setTakeSnapshotOnSuccess(final boolean takeSnapshotOnSuccess) {
        this.takeSnapshotOnSuccess = takeSnapshotOnSuccess;
    }

    @Override
    protected void starting(final Description description) {
        initializeData(description);
        cleanUpFilesFromPreviousRunsIfTheyExist();
    }

    @Override
    protected void succeeded(final Description description) {
        if (takeSnapshotOnSuccess) {
            captureAwtScreenshotTo(new File(String.format(PNG_FILE_FORMAT, testClassName, SUCCESS_FILE_NAME_PREFIX, testMethodName, StringUtils.EMPTY)));
        }
    }

    @Override
    protected void failed(final Throwable e, final Description description) {
        if (driver != null && !failedCreatingDriver(e)) {
            captureAwtScreenshotTo(new File(String.format(PNG_FILE_FORMAT, testClassName, FAILED_FILE_NAME_PREFIX, testMethodName, StringUtils.EMPTY)));
            dumpSourceTo(new File(String.format(HTML_FILE_FORMAT, testClassName, FAILED_FILE_NAME_PREFIX, testMethodName, StringUtils.EMPTY)));
        }
    }

    @Override
    protected void finished(final Description description) {
        if (quitDriverWhenFinished && driver != null) {
            driver.quit();
        }
    }

    private void initializeData(final Description description) {
        testClassName = description.getTestClass().getSimpleName();
        testMethodName = description.getMethodName();
        setDriver(description);
        final Window window = driver.manage().window();
        windowPosition = window.getPosition();
        windowSize = window.getSize();
    }

    private void cleanUpFilesFromPreviousRunsIfTheyExist() {
        final File directory = new File(String.format(BASE_DIRECTORY_FORMAT, testClassName));
        directory.mkdirs();
        final WildcardFileFilter fileFilter = new WildcardFileFilter(String.format(FILE_NAME_FORMAT, "*", testMethodName, "*"));
        final Collection<File> fileList = FileUtils.listFiles(directory, fileFilter, null);
        for (final File file : fileList) {
            FileUtils.deleteQuietly(file);
        }
    }

    /**
     * This does an actual screenshot of the browser where the WebDriver one only capture the body.  
     * Use this if you need to see the title bar and/or popups.
     */
    private void captureAwtScreenshotTo(final File destinationFile) {
        try {
            ImageIO.write(new Robot().createScreenCapture(new Rectangle(windowPosition.getX(), windowPosition.getY(), windowSize.getWidth(), windowSize.getHeight())), "png", destinationFile);
            LOGGER.info("Screenshot captured to: " + destinationFile.getAbsolutePath());
        } catch (final Exception e) {
            // No need to crash the tests if the screenshot fails
        }
    }

    /**
     * The WebDriver screenshots are only of the body.  If you need to see popups or the title bar then use the awt one.
     * 
     * @param destinationFile
     */
    private void captureWebDriverScreenshotTo(final File destinationFile) {
        try {
            final File screenshot = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(screenshot, destinationFile);
            LOGGER.info("Screenshot captured to: " + destinationFile.getAbsolutePath());
        } catch (final Exception e) {
            // No need to crash the tests if the screenshot fails
        }
    }

    private void dumpSourceTo(final File destinationFile) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(destinationFile);
            IOUtils.write(driver.getPageSource(), fileWriter);
            LOGGER.info("Source written to: " + destinationFile.getAbsolutePath());
        } catch (final IOException e1) {
            // No need to crash the tests if the source dump fails
        } finally {
            IOUtils.closeQuietly(fileWriter);
        }
    }

    private boolean failedCreatingDriver(final Throwable e) {
        boolean failedCreatingDriver = false;
        final StackTraceElement[] stackTrace = e.getStackTrace();
        for (final StackTraceElement stackTraceElement : stackTrace) {
            if (stackTraceElement.getClassName().endsWith("Driver") && stackTraceElement.getMethodName().equals("<init>")) {
                failedCreatingDriver = true;
                driver = null;
                break;
            }
        }
        return failedCreatingDriver;
    }

    private void setDriver(final Description description) {
        try {
            final Object object = description.getTestClass().newInstance();
            if (object instanceof WebDriverSeleniumTest) {
                driver = ((WebDriverSeleniumTest)object).getWebDriver();
            } else {
                throw new RuntimeException("In order to be able to use ScreenshotTestRule your test class MUST implement WebDriverSeleniumTest.");
            }
        } catch (final IllegalAccessException e1) {
            throw new RuntimeException("Couldn't create an instance of test class: " + description.getTestClass(), e1);
        } catch (final InstantiationException e2) {
            throw new RuntimeException("Couldn't create an instance of test class: " + description.getTestClass(), e2);
        }
    }
}