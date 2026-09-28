package com.saucedemo.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página "Checkout: Your Information" (dados do comprador). */
public class CheckoutInformationPage extends BasePage {

    private static final By FIRST_NAME = By.id("first-name");
    private static final By LAST_NAME = By.id("last-name");
    private static final By POSTAL_CODE = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");

    public CheckoutInformationPage(WebDriver driver) {
        super(driver);
        visible(CONTINUE_BUTTON);
    }

    public CheckoutInformationPage fillInformation(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME, firstName);
        type(LAST_NAME, lastName);
        type(POSTAL_CODE, postalCode);
        return this;
    }

    public CheckoutOverviewPage continueToOverview() {
        click(CONTINUE_BUTTON);
        return new CheckoutOverviewPage(driver);
    }
}
