package com.merritt.commons.testing.junit.selenium;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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
public class TitlePageCheckPage {
    public TitlePageCheckPage(final WebDriver driver, final String expectedTitle, final int waitTime) {
        // Check that we're on the right page.
        if (!new WebDriverWait(driver, waitTime).until(ExpectedConditions.titleIs(expectedTitle))) {
            throw new IllegalStateException("Was expecting a page with title: \"" + expectedTitle + "\" but got a page with title: \"" + driver.getTitle() + "\"");
        }
    }

    public String acceptAlert(WebDriver driver) {
        String text = "";
        Alert alert = new WebDriverWait(driver, 15).until(ExpectedConditions.alertIsPresent());
        text = alert.getText();
        alert.accept();
        return text;
    }
}
