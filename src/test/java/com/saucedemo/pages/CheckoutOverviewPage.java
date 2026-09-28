package com.saucedemo.pages;

import com.saucedemo.data.Money;
import com.saucedemo.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.math.BigDecimal;
import java.util.List;

/** Página "Checkout: Overview" (resumo do pedido antes de finalizar). */
public class CheckoutOverviewPage extends BasePage {

    private static final By ITEM_TOTAL = By.cssSelector(".summary_subtotal_label");
    private static final By FINISH_BUTTON = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver);
        visible(FINISH_BUTTON);
    }

    public List<Product> items() {
        return readCartItems();
    }

    public int quantityOf(String productName) {
        return readCartQuantity(productName);
    }

    /** Soma dos itens, sem taxa ("Item total: $29.99"). */
    public BigDecimal itemTotal() {
        return Money.parse(visible(ITEM_TOTAL).getText());
    }

    public CheckoutCompletePage finish() {
        click(FINISH_BUTTON);
        return new CheckoutCompletePage(driver);
    }
}
