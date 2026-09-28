package com.saucedemo.pages;

import com.saucedemo.data.Product;
import com.saucedemo.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;

/** Página "Your Cart". */
public class CartPage extends BasePage {

    private static final By CHECKOUT_BUTTON = By.id("checkout");

    private final HeaderComponent header;

    public CartPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        visible(CHECKOUT_BUTTON);
    }

    public HeaderComponent header() {
        return header;
    }

    public List<Product> items() {
        return readCartItems();
    }

    public int quantityOf(String productName) {
        return readCartQuantity(productName);
    }

    public CheckoutInformationPage checkout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutInformationPage(driver);
    }
}
