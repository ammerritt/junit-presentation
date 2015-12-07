package com.merritt.samples.testing.encryption.selenium.test.pages;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import com.merritt.commons.testing.junit.selenium.TitlePageCheckPage;
import com.merritt.samples.testing.encryption.selenium.test.sections.LogoffSection;

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
public class EncryptionPage extends TitlePageCheckPage {
    private static final String ENCRYPTION_PAGE_TITLE = "Password Encryption";

    private final WebDriver driver;

    //FindBy will look up the element each time it is accessed.  
    //If it won't be changing, then you can annotate it with @CacheLookup so it will only look it up once
    //however, if the page refreshes during your tests then it will technically change and you will get a
    //StaleElementReferenceException
    @FindBy(id = "encryptForm")
    private WebElement encryptFormFieldElement;

    @FindBy(id = "encryptPassword")
    private WebElement encryptFormPasswordFieldElement;

    @FindBy(id = "encryptEncPassword")
    private WebElement encryptFormEncPasswordFieldElement;

    @FindBy(id = "encryptSubmit")
    private WebElement encryptFormSubmitFieldElement;

    @FindBy(id = "decryptForm")
    private WebElement decryptFormFieldElement;

    @FindBy(id = "decryptEncPassword")
    private WebElement decryptFormEncPasswordFieldElement;

    @FindBy(id = "decryptPassword")
    private WebElement decryptFormPasswordFieldElement;

    @FindBy(id = "decryptSubmit")
    private WebElement decryptFormSubmitFieldElement;

    private static LogoffSection logoutSection;

    public EncryptionPage(final WebDriver driver) {
        super(driver, ENCRYPTION_PAGE_TITLE, 15);
        this.driver = driver;
        PageFactory.initElements(driver, this);
        logoutSection = new LogoffSection(driver);
    }

    public String encryptPassword(final String password) {
        encryptFormPasswordFieldElement.clear();
        encryptFormPasswordFieldElement.sendKeys(password);
        encryptFormFieldElement.submit(); //you can submit the form itself
        //NOTE: Pay very close attention here.. You think you want to use the getText() method but that give you the inner html and not the value.
        return encryptFormEncPasswordFieldElement.getAttribute("value");
    }

    public String encryptPasswordFail(final String password) {
        encryptFormPasswordFieldElement.clear();
        encryptFormPasswordFieldElement.sendKeys(password);
        encryptFormSubmitFieldElement.click(); //OR you can click the button to submit the form
        return acceptAlert(driver);
    }

    public String decryptPassword(final String password) {
        decryptFormEncPasswordFieldElement.clear();
        decryptFormEncPasswordFieldElement.sendKeys(password);
        decryptFormSubmitFieldElement.click();
        //NOTE: Pay very close attention here.. You think you want to use the getText() method but that gives you the inner html and not the value.
        return decryptFormPasswordFieldElement.getAttribute("value");
    }

    public LoginPage logout() {
        return logoutSection.logout();
    }

    /*
     * Approach 1 to validating page requirements.  Easier to maintain but very difficult to determine where it broke when it does break.
     */
    public boolean isProperlyLoaded() {
        return validateEncryptForm() && validateDecryptForm();
    }

    private boolean validateEncryptForm() {
        return encryptFormFieldElement.isDisplayed() && encryptFormPasswordFieldElement.isDisplayed() && encryptFormPasswordFieldElement.getSize().equals(new Dimension(207, 22)) && encryptFormPasswordFieldElement.getLocation().equals(new Point(77, 92)) && encryptFormEncPasswordFieldElement.isDisplayed() && encryptFormSubmitFieldElement.isDisplayed();
    }

    private boolean validateDecryptForm() {
        return decryptFormFieldElement.isDisplayed() && decryptFormPasswordFieldElement.isDisplayed() && decryptFormPasswordFieldElement.getSize().equals(new Dimension(207, 22)) && decryptFormPasswordFieldElement.getLocation().equals(new Point(77, 264)) && decryptFormEncPasswordFieldElement.isDisplayed() && decryptFormSubmitFieldElement.isDisplayed();
    }

    /*
     * End of approach 1.
     */

    /*
     * Approach 2 to validating page requirements.  More code to write but easy to determine where it broke when it does break.
     * You would only write the ones that you truly need to ensure are correct.  If the position of an element doesn't matter; 
     * don't write a method for it and don't test it.
     */
    public boolean encryptFormPresent() {
        return encryptFormFieldElement.isDisplayed();
    }

    public boolean encryptFormPasswordFieldPresent() {
        return encryptFormPasswordFieldElement.isDisplayed();
    }

    public Dimension getEncryptFormPasswordFieldSize() {
        return encryptFormPasswordFieldElement.getSize();
    }

    public Point getEncryptFormPasswordFieldLocation() {
        return encryptFormPasswordFieldElement.getLocation();
    }

    public boolean encryptFormEncPasswordFieldPresent() {
        return encryptFormEncPasswordFieldElement.isDisplayed();
    }

    public boolean encryptFormButtonPresent() {
        return encryptFormSubmitFieldElement.isDisplayed();
    }

    public boolean decryptFormPresent() {
        return decryptFormFieldElement.isDisplayed();
    }

    public boolean decryptFormPasswordFieldPresent() {
        return decryptFormPasswordFieldElement.isDisplayed();
    }

    public Dimension getDecryptFormPasswordFieldSize() {
        return decryptFormPasswordFieldElement.getSize();
    }

    public Point getDecryptFormPasswordFieldLocation() {
        return decryptFormPasswordFieldElement.getLocation();
    }

    public boolean decryptFormEncPasswordFieldPresent() {
        return decryptFormEncPasswordFieldElement.isDisplayed();
    }

    public boolean decryptFormButtonPresent() {
        return decryptFormSubmitFieldElement.isDisplayed();
    }
    /*
     * End of approach 2.
     */
}
