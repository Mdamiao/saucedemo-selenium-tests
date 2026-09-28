package com.saucedemo.pages;

import com.saucedemo.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** Página "Checkout: Complete!" (confirmação do pedido). */
public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_HEADER = By.cssSelector(".complete-header");

    private final HeaderComponent header;

    public CheckoutCompletePage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        visible(COMPLETE_HEADER);
    }

    public HeaderComponent header() {
        return header;
    }

    public String confirmationMessage() {
        return visible(COMPLETE_HEADER).getText();
    }
}
