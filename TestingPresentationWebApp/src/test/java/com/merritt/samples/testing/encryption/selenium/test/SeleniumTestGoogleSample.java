package com.merritt.samples.testing.encryption.selenium.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.merritt.commons.testing.junit.selenium.ScreenshotTestRule;
import com.merritt.commons.testing.junit.selenium.WebDriverSeleniumTest;

//http://seleniumhq.org/docs/03_webdriver.html#how-does-webdriver-drive-the-browser-compared-to-selenium-rc
//http://seleniumhq.org/docs/04_webdriver_advanced.html
public class SeleniumTestGoogleSample implements WebDriverSeleniumTest {
    private static WebDriver driver;
    private final String baseUrl = "http://www.google.com";

    @Rule
    public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule(true);

    @BeforeClass
    public static void createAndStartService() throws IOException {
    }

    @AfterClass
    public static void createAndStopService() {
    }

    //also iPhoneDriver but requires SDK and provisioning profile: http://code.google.com/p/selenium/wiki/IPhoneDriver

    private static boolean isSupportedSafariPlatform() {
        final Platform current = Platform.getCurrent();
        return Platform.MAC.is(current) || Platform.WINDOWS.is(current);
    }

    @Test
    @Ignore
    public void testGoogleIe() throws Exception {
        //http://code.google.com/p/selenium/wiki/InternetExplorerDriver
        driver = new InternetExplorerDriver();
        testGoogle(driver);
    }

    @Test
    public void testGoogleChrome() throws Exception {
        System.out.println("System.getProperty( \"user.home\" ): " + System.getProperty("env.workspace_loc"));
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.home") + "/AppData/Local/Google/Chrome/Application/chromedriver.exe");
        //http://code.google.com/p/selenium/wiki/ChromeDriver
        driver = new ChromeDriver();
        testGoogle(driver);
    }

    @Test
    public void testGoogleFirefox() throws Exception {
        //http://code.google.com/p/selenium/wiki/FirefoxDriver
        driver = new FirefoxDriver();
        testGoogle(driver);
    }

    @Test
    public void testGoogleSafari() throws Exception {
        //assume causes the execution of the method to stop if not met but still passes
        assumeTrue(isSupportedSafariPlatform());
        //http://code.google.com/p/selenium/wiki/SafariDriver
        driver = new SafariDriver();
        testGoogle(driver);
    }

    public void testGoogle(final WebDriver driver) throws Exception {
        driver.navigate().to(baseUrl);

        final WebElement element = driver.findElement(By.name("q"));
        element.sendKeys("webdriver");
        element.submit();

        assertEquals("Google", driver.getTitle());

        // Google's search is rendered dynamically with JavaScript.
        // Wait for the page to load, timeout after 10 seconds
        //2 ways to do roughly the same thing...
        new WebDriverWait(driver, 10).until(ExpectedConditions.titleContains("webdriver"));

        new WebDriverWait(driver, 10).until(new ExpectedCondition<Boolean>() {
            public Boolean apply(final WebDriver d) {
                return d.getTitle().toLowerCase().startsWith("webdriver");
            }
        });
        assertEquals("webdriver - Google Search", driver.getTitle());
    }

    @After
    public void tearDown() throws Exception {
        //driver.quit();
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    //NOTES...
    //        final DesiredCapabilities cap = new DesiredCapabilities();
    //        
    //        final File profileDir = new File( "./test_profile" );
    //        if( !profileDir.exists() )
    //        {
    //            profileDir.mkdirs();
    //        }
    //        final FirefoxProfile profile = new FirefoxProfile( profileDir );
    //        profile.setPreference( "toolkit.telemetry.prompted", false );
    //        cap.setCapability( FirefoxDriver.PROFILE, profile );

    //        final DesiredCapabilities browser =
    //                DesiredCapabilities.chrome();
    //        
    //        driver = new HtmlUnitDriver( browser );
    //        final DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    //        capabilities.setCapability( "chrome.binary", "C:/Users/amerritt/AppData/Local/Google/Chrome/Application/chrome.exe" );
    //        
    //        driver = new ChromeDriver( capabilities );
    //        
    //        driver.manage().timeouts().implicitlyWait( 30, TimeUnit.SECONDS );
}
