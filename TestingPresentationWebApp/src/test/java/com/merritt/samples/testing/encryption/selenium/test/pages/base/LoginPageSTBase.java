package com.merritt.samples.testing.encryption.selenium.test.pages.base;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;

import com.merritt.commons.testing.junit.selenium.ScreenshotTestRule;
import com.merritt.commons.testing.junit.selenium.WebDriverSeleniumTest;
import com.merritt.samples.testing.encryption.selenium.test.pages.ErrorPage;
import com.merritt.samples.testing.encryption.selenium.test.pages.LoginPage;

/**
 * <pre>
 * This class uses the page object pattern: http://code.google.com/p/selenium/wiki/PageObjects
 * 
 * http://code.google.com/p/selenium/wiki/PageFactory
 * 
 * NOTE: Not every "page" needs to actually be a page.  For example if you have common code that is shared 
 *       you can have a class for that and then use it in the "page" classes; such as a menu, header, or 
 *       footer.  What you are trying to achieve is access to the same thing is ONLY in 1 class.
 *       
 * NOTE: You may get weird popups or stuff, like IE wanting to auto-complete for you or store a password.  
 *       If you see something odd try it in the browser and see if you get something like that.
 * 
 * NOTE: Pay very close attention when you want to get the text of a text field. 
 *      You think you want to use the getText() method but that give you the inner html and not the value.
 *      Instead use getAttribute("value").
 * </pre>
 */
public class LoginPageSTBase implements WebDriverSeleniumTest {
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String INDEX_PAGE = "jsp/index.jsp";
    private static final String WRONG_PASSWORD = "wrong";
    private static final String CORRECT_PASSW0RD = "Passw0rd";
    private static final String TEST_USERNAME_WITH_ACCESS = "amerritt";
    private static final String BASE_URL = "http://localhost:8585/TestingPresentationWebApp/";
    private static WebDriver driver;

    @Rule
    //since we want to open a new browser for each test pass true and let it quit the driver after each test
    public ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule(false);

    protected LoginPageSTBase() {
        //this is here so that it isn't tried to run by itself  it is meant to be extended and the driver created and setUp of this called
    }

    public static void setUp(final WebDriver driver) throws Exception {
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //sets the time to look for an element.
        LoginPageSTBase.driver = driver;
    }

    @Test
    public void testSuccessfulRedirectFromRoot() throws Exception {
        driver.get(BASE_URL);
        final LoginPage loginPage = new LoginPage(driver);
        boolean properlyLoaded = loginPage.isProperlyLoaded();
        assertTrue(properlyLoaded);
    }

    @Test
    public void testSuccessfulRedirectFromPage() throws Exception {
        driver.get(BASE_URL + INDEX_PAGE);
        final LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isProperlyLoaded());
    }

    @Test
    public void testUnSuccessfulLogin() throws Exception {
        driver.get(BASE_URL + LOGIN_PAGE);
        final LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isProperlyLoaded());
        final ErrorPage errorPage = loginPage.failLoginAs(TEST_USERNAME_WITH_ACCESS, WRONG_PASSWORD);
        assertTrue(errorPage.isProperlyLoaded());
    }

    @Test
    public void testSuccessfulLogin() throws Exception {
        driver.get(BASE_URL + LOGIN_PAGE);
        final LoginPage loginPage = new LoginPage(driver);
        assertTrue(loginPage.isProperlyLoaded());
        loginPage.loginAs(TEST_USERNAME_WITH_ACCESS, CORRECT_PASSW0RD).logout();
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }
}
