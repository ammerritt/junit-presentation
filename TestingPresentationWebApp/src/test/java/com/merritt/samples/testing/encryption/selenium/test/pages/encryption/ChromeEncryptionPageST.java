package com.merritt.samples.testing.encryption.selenium.test.pages.encryption;

import org.junit.BeforeClass;
import org.openqa.selenium.chrome.ChromeDriver;

import com.merritt.samples.testing.encryption.selenium.test.pages.base.EncryptionPageSTBase;

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
public class ChromeEncryptionPageST extends EncryptionPageSTBase {

    @BeforeClass
    public static void setUp() throws Exception {
        //NOTE: if you also want a snapshot on every successful test you can do the following:
        //screenshotTestRule.setTakeSnapshotOnSuccess( true );

        //You MUST set this for this to work!
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.home") + "/chromedriver");
        EncryptionPageSTBase.setUp(new ChromeDriver());
    }
}
