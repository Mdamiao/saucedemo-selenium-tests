package com.saucedemo.pages;

import com.saucedemo.config.Config;
import com.saucedemo.data.Money;
import com.saucedemo.data.Product;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

/**
 * Base de todos os Page Objects: concentra as esperas explícitas e interações comuns.
 * Nenhum Thread.sleep é usado — toda sincronização é feita com WebDriverWait.
 */
public abstract class BasePage {

    private static final By PAGE_TITLE = By.cssSelector(".title");

    // Itens listados no carrinho e no resumo do checkout compartilham a mesma estrutura.
    protected static final By CART_ITEM = By.cssSelector(".cart_item");
    protected static final By ITEM_NAME = By.cssSelector(".inventory_item_name");
    protected static final By ITEM_PRICE = By.cssSelector(".inventory_item_price");
    protected static final By ITEM_QUANTITY = By.cssSelector(".cart_quantity");

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.explicitWait());
    }

    /** Título exibido no cabeçalho da página (ex.: "Products", "Your Cart"). */
    public String title() {
        return visible(PAGE_TITLE).getText();
    }

    public String currentUrl() {
        return driver.getCurrentUrl();
    }

    protected WebElement visible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected List<WebElement> allVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
    }

    protected void click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement field = visible(locator);
        field.clear();
        field.sendKeys(text);
    }

    protected boolean isPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Lê os produtos exibidos em uma lista de carrinho/checkout. */
    protected List<Product> readCartItems() {
        if (!isPresent(CART_ITEM)) {
            return List.of();
        }
        return allVisible(CART_ITEM).stream()
                .map(item -> new Product(
                        item.findElement(ITEM_NAME).getText(),
                        Money.parse(item.findElement(ITEM_PRICE).getText())))
                .toList();
    }

    /** Quantidade exibida para um produto específico no carrinho/checkout. */
    protected int readCartQuantity(String productName) {
        return allVisible(CART_ITEM).stream()
                .filter(item -> item.findElement(ITEM_NAME).getText().equals(productName))
                .findFirst()
                .map(item -> Integer.parseInt(item.findElement(ITEM_QUANTITY).getText().trim()))
                .orElseThrow(() -> new IllegalStateException(
                        "Produto '" + productName + "' não encontrado na lista"));
    }
}
