package com.saucedemo.pages.components;

import com.saucedemo.pages.CartPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/** Cabeçalho compartilhado pelas páginas logadas (ícone e contador do carrinho). */
public class HeaderComponent {

    private static final By CART_LINK = By.cssSelector(".shopping_cart_link");
    private static final By CART_BADGE = By.cssSelector(".shopping_cart_badge");

    private final WebDriver driver;

    public HeaderComponent(WebDriver driver) {
        this.driver = driver;
    }

    /** Quantidade exibida no badge do carrinho (0 quando o badge não é exibido). */
    public int cartItemCount() {
        List<WebElement> badge = driver.findElements(CART_BADGE);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText().trim());
    }

    public CartPage openCart() {
        driver.findElement(CART_LINK).click();
        return new CartPage(driver);
    }
}
