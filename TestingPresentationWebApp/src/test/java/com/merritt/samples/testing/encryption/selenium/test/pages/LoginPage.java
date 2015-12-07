package com.merritt.samples.testing.encryption.selenium.test.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.merritt.commons.testing.junit.selenium.TitlePageCheckPage;

/**
 * <pre>
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
public class LoginPage extends TitlePageCheckPage {
    private static final String LOGIN_PAGE_TITLE = "Login Page for Examples";

    private final WebDriver driver;

    //FindBy will look up the element each time it is accessed. 
    //If it won't be changing, then you can annotate it with @CacheLookup so it will only look it up once
    //however, if the page refreshes during your tests then it will technically change and you will get a
    //StaleElementReferenceException
    @FindBy(name = "j_username")
    private WebElement usernameFieldElement;

    @FindBy(name = "j_password")
    private WebElement passwordFieldElement;

    @FindBy(css = "input[type=\"submit\"]")
    private WebElement submitButtonElement;

    @FindBy(tagName = "th")
    private List<WebElement> thElements;

    public LoginPage(final WebDriver driver) {
        super(driver, LOGIN_PAGE_TITLE, 15);
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isProperlyLoaded() {
        return thElements.size() == 2 && "Username:".equals(thElements.get(0).getText()) && "Password:".equals(thElements.get(1).getText());
    }

    // Conceptually, the login page offers the user the service of being able to "log into"
    // the application using a user name and password. 
    public EncryptionPage loginAs(final String username, final String password) {
        executeLogin(username, password);

        // Return a new page object representing the destination. Should the login page ever
        // go somewhere else (for example, a legal disclaimer) then changing the method signature
        // for this method will mean that all tests that rely on this behavior won't compile.
        return new EncryptionPage(driver);
    }

    public ErrorPage failLoginAs(final String username, final String password) {
        executeLogin(username, password);
        return new ErrorPage(driver);
    }

    private void executeLogin(final String username, final String password) {
        // This is the only place in the test code that "knows" how to enter these details
        usernameFieldElement.clear();
        usernameFieldElement.sendKeys(username);
        passwordFieldElement.clear();
        passwordFieldElement.sendKeys(password);
        submitButtonElement.click();
    }
}
