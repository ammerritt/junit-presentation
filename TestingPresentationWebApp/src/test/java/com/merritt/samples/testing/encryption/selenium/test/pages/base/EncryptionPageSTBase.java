package com.merritt.samples.testing.encryption.selenium.test.pages.base;

import static com.merritt.commons.testing.junit.selenium.matcher.DimensionSizeRange.withinRange;
import static com.merritt.commons.testing.junit.selenium.matcher.IsWithinRange.withinRange;
import static com.merritt.commons.testing.junit.selenium.matcher.PointLocationRange.withinRange;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Range;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

import com.merritt.commons.testing.junit.selenium.ScreenshotTestRule;
import com.merritt.commons.testing.junit.selenium.WebDriverSeleniumTest;
import com.merritt.samples.testing.encryption.selenium.test.pages.EncryptionPage;
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
public class EncryptionPageSTBase implements WebDriverSeleniumTest {
    private static final String VALID_PASSW0RD = "Passw0rd";
    private static final String VALID_USERNAME = "amerritt";
    private static final String LOGIN_PAGE = "login.jsp";
    private static WebDriver driver;
    private static EncryptionPage encryptionPage;
    private static final String BASE_URL = "http://localhost:8585/TestingPresentationWebApp/";

    @Rule
    //since we want to use the same browser the whole time pass false and quit the driver in the AfterClass method
    public static ScreenshotTestRule screenshotTestRule = new ScreenshotTestRule(false);

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    protected EncryptionPageSTBase() {
        //this is here so that it isn't tried to run by itself  it is meant to be extended and the driver created and setUp of this called
    }

    public static void setUp(final WebDriver driver) throws Exception {
        //NOTE: if you also want a snapshot on every successful test you can do the following:
        //screenshotTestRule.setTakeSnapshotOnSuccess( true );
        //NOTE: if you want a screenshot no matter what you can do one of the following where ever you want in your test methods:
        // screenshotTestRule.captureAwtScreenshot( "myprefix-", StringUtils.EMPTY );
        // screenshotTestRule.captureWebDriverScreenshot( "myprefix-", "-1" );

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS); //sets the time to look for an element.
        driver.get(BASE_URL + LOGIN_PAGE);
        final LoginPage loginPage = new LoginPage(driver);
        encryptionPage = loginPage.loginAs(VALID_USERNAME, VALID_PASSW0RD);
        EncryptionPageSTBase.driver = driver;
    }

    /**
     * This is one approach to testing that your page is rendered correctly. 
     * It is easier to maintain BUT very very hard to tell exactly which piece broke when it did break.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testPageSetup1() throws Exception {
        assertTrue(encryptionPage.isProperlyLoaded());
    }

    /**
     * This is another approach to verify the page is rendered correctly. 
     * You have more code to write, but it is far easy to tell what broke when it did break.
     * The problem with using asserts is that it stops processing when the first one fails.
     * @throws Exception
     */
    @Test
    @Ignore
    public void testPageSetup2() throws Exception {
        assertTrue("The encrypt form should be present.", encryptionPage.encryptFormPresent());
        assertTrue("The encrypt form should have a password field.", encryptionPage.encryptFormPasswordFieldPresent());
        assertEquals("The encrypt form password field is not the correct size.", new Dimension(207, 22), encryptionPage.getEncryptFormPasswordFieldSize());
        assertEquals("The encrypt form password field is not in the correct location.", new Point(77, 92), encryptionPage.getEncryptFormPasswordFieldLocation());
        assertTrue("The encrypt form should have a field for the encrypted password.", encryptionPage.encryptFormEncPasswordFieldPresent());
        assertTrue("The encrypt form should have a submit button.", encryptionPage.encryptFormButtonPresent());

        assertTrue("The decrypt form should be present.", encryptionPage.decryptFormPresent());
        assertTrue("The decrypt form should have a password field.", encryptionPage.decryptFormPasswordFieldPresent());
        assertEquals("The decrypt form password field is not the correct size.", new Dimension(207, 22), encryptionPage.getDecryptFormPasswordFieldSize());
        assertEquals("The decrypt form password field is not in the correct location.", new Point(77, 264), encryptionPage.getDecryptFormPasswordFieldLocation());
        assertTrue("The decrypt form should have a field for the encrypted password.", encryptionPage.decryptFormEncPasswordFieldPresent());
        assertTrue("The decrypt form should have a submit button.", encryptionPage.decryptFormButtonPresent());
    }

    /**
     * This is a twist on approach 2 so that it checks everything instead of failing on the first failure as assert does.
     * @throws Exception
     */
    @Test
    public void testPageSetup2andAHalf() throws Exception {
        //This could be broken into multiple tests as well; say 1 for each form, or even further...
        collector.checkThat("The encrypt form should be present.", encryptionPage.encryptFormPresent(), is(true));
        collector.checkThat("The encrypt form should have a password field.", encryptionPage.encryptFormPasswordFieldPresent(), is(true));
        collector.checkThat("The encrypt form password field is not the correct size.", encryptionPage.getEncryptFormPasswordFieldSize(), is(new Dimension(191, 19)));
        collector.checkThat("The encrypt form password field is not in the correct location.", encryptionPage.getEncryptFormPasswordFieldLocation(), is(new Point(77, 80)));
        collector.checkThat("The encrypt form should have a field for the encrypted password.", encryptionPage.encryptFormEncPasswordFieldPresent(), is(true));
        collector.checkThat("The encrypt form should have a submit button.", encryptionPage.encryptFormButtonPresent(), is(true));

        collector.checkThat("The decrypt form should be present.", encryptionPage.decryptFormPresent(), is(true));
        collector.checkThat("The decrypt form should have a password field.", encryptionPage.decryptFormPasswordFieldPresent(), is(true));
        collector.checkThat("The decrypt form password field is not the correct size.", encryptionPage.getDecryptFormPasswordFieldSize(), is(new Dimension(191, 19)));
        collector.checkThat("The decrypt form password field is not in the correct location.", encryptionPage.getDecryptFormPasswordFieldLocation(), is(new Point(77, 225)));
        collector.checkThat("The decrypt form should have a field for the encrypted password.", encryptionPage.decryptFormEncPasswordFieldPresent(), is(true));
        collector.checkThat("The decrypt form should have a submit button.", encryptionPage.decryptFormButtonPresent(), is(true));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testPageSetup2andAHalf_Passes() throws Exception {
        //This could be broken into multiple tests as well; say 1 for each form, or even further...
        collector.checkThat("The encrypt form should be present.", encryptionPage.encryptFormPresent(), is(true));
        collector.checkThat("The encrypt form should have a password field.", encryptionPage.encryptFormPasswordFieldPresent(), is(true));
        collector.checkThat("The encrypt form password field is not the correct size.", encryptionPage.getEncryptFormPasswordFieldSize(), withinRange(new Dimension(175, 19), new Dimension(195, 22)));
        collector.checkThat("The encrypt form password field is not the correct size.", encryptionPage.getEncryptFormPasswordFieldSize(), withinRange(Range.between(175, 195), Range.between(19, 22)));
        collector.checkThat("The encrypt form password field is not in the correct location.", encryptionPage.getEncryptFormPasswordFieldLocation(), is(withinRange(Range.between(77, 78), Range.between(80, 85))));
        collector.checkThat("The encrypt form should have a field for the encrypted password.", encryptionPage.encryptFormEncPasswordFieldPresent(), is(true));
        collector.checkThat("The encrypt form should have a submit button.", encryptionPage.encryptFormButtonPresent(), is(true));

        collector.checkThat("The decrypt form should be present.", encryptionPage.decryptFormPresent(), is(true));
        collector.checkThat("The decrypt form should have a password field.", encryptionPage.decryptFormPasswordFieldPresent(), is(true));
        collector.checkThat("The decrypt form password field is not the correct size.", encryptionPage.getDecryptFormPasswordFieldSize(), anyOf(equalTo(new Dimension(179, 22)), equalTo(new Dimension(191, 19)), equalTo(new Dimension(215, 22))));
        collector.checkThat("The decrypt form password field is not in the correct location.", encryptionPage.getDecryptFormPasswordFieldLocation(), withinRange(new Point(77, 225), new Point(78, 245)));
        collector.checkThat("The decrypt form should have a field for the encrypted password.", encryptionPage.decryptFormEncPasswordFieldPresent(), is(true));
        collector.checkThat("The decrypt form should have a submit button.", encryptionPage.decryptFormButtonPresent(), is(true));
    }

    @Test
    public void testSuccessfulEncrypt() throws Exception {
        assertEquals("gnitset", encryptionPage.encryptPassword("testing"));
    }

    @Test
    public void testUnsuccessfulEncrypt_PasswordTooShort() throws Exception {
        assertEquals("Password can not be less than 5 characters.", encryptionPage.encryptPasswordFail("test"));
    }

    @Test
    public void testSuccessfulDecrypt() throws Exception {
        assertEquals("testing", encryptionPage.encryptPassword("gnitset"));
    }

    @Test
    public void testLogout() throws Exception {
        assertNotNull(encryptionPage.logout());
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
    }

    public WebDriver getWebDriver() {
        return driver;
    }
}
