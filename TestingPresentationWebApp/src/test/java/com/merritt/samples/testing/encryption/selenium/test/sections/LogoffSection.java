package com.merritt.samples.testing.encryption.selenium.test.sections;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.merritt.samples.testing.encryption.selenium.test.pages.LoginPage;

/**
 * <pre>
 * This represents the logoff link that should be on every page.  In real world this would probably be some sort
 * of menu or something...
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
public class LogoffSection
{
    private final WebDriver driver;
    
    //FindBy will look up the element each time it is accessed.  
    //If it won't be changing, then you can annotate it with @CacheLookup so it will only look it up once
    //however, if the page refreshes during your tests then it will technically change and you will get a
    //StaleElementReferenceException
    @FindBy( linkText = "Logout" )
    private WebElement logoutPageLinkElement;
    
    public LogoffSection( final WebDriver driver )
    {
        this.driver = driver;
        PageFactory.initElements( driver, this );
    }
    
    public LoginPage logout()
    {
        logoutPageLinkElement.click();
        return new LoginPage( driver );
    }
}
