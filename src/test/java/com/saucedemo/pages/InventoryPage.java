package com.saucedemo.pages;

import com.saucedemo.config.Config;
import com.saucedemo.data.Money;
import com.saucedemo.data.Product;
import com.saucedemo.pages.components.HeaderComponent;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.List;

/** Vitrine de produtos exibida após o login. */
public class InventoryPage extends BasePage {

    private static final By INVENTORY_LIST = By.cssSelector(".inventory_list");
    private static final By INVENTORY_ITEM = By.cssSelector(".inventory_item");
    private static final By SORT_SELECT = By.cssSelector(".product_sort_container");
    private static final By ACTIVE_SORT = By.cssSelector(".active_option");
    private static final By ITEM_BUTTON = By.tagName("button");

    private final HeaderComponent header;

    public InventoryPage(WebDriver driver) {
        super(driver);
        this.header = new HeaderComponent(driver);
        visible(INVENTORY_LIST);
    }

    public HeaderComponent header() {
        return header;
    }

    // ---------- Filtro / ordenação ----------

    public InventoryPage sortBy(SortOption option) {
        new Select(visible(SORT_SELECT)).selectByValue(option.value());
        wait.until(ExpectedConditions.textToBe(ACTIVE_SORT, option.label()));
        return this;
    }

    public String activeSortLabel() {
        return visible(ACTIVE_SORT).getText();
    }

    // ---------- Leitura da vitrine ----------

    public List<String> productNames() {
        return allVisible(INVENTORY_ITEM).stream()
                .map(item -> item.findElement(ITEM_NAME).getText())
                .toList();
    }

    public List<BigDecimal> productPrices() {
        return allVisible(INVENTORY_ITEM).stream()
                .map(item -> Money.parse(item.findElement(ITEM_PRICE).getText()))
                .toList();
    }

    public boolean isProductDisplayed(String productName) {
        return productNames().contains(productName);
    }

    /** Localiza o produto na vitrine e devolve nome + preço exibidos. */
    public Product product(String productName) {
        WebElement card = productCard(productName);
        return new Product(
                card.findElement(ITEM_NAME).getText(),
                Money.parse(card.findElement(ITEM_PRICE).getText()));
    }

    // ---------- Ações no produto ----------

    public InventoryPage addToCart(String productName) {
        productCard(productName).findElement(ITEM_BUTTON).click();
        // Após adicionar, o botão do card passa a exibir "Remove". A página pode recriar o botão
        // (o elemento antigo fica stale) e o texto pode vir em maiúsculas via CSS; por isso o
        // botão é localizado de novo a cada tentativa e o texto é comparado sem diferenciar caixa.
        new WebDriverWait(driver, Config.explicitWait())
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Botão do produto '" + productName + "' não mudou para 'Remove' após adicionar")
                .until(d -> cartButtonLabel(productName).equalsIgnoreCase("Remove"));
        return this;
    }

    public String cartButtonLabel(String productName) {
        return productCard(productName).findElement(ITEM_BUTTON).getText();
    }

    /**
     * Encontra o card pelo NOME do produto (e não por posição/índice), o que mantém o teste
     * estável mesmo quando a ordenação da vitrine muda.
     */
    private WebElement productCard(String productName) {
        return new WebDriverWait(driver, Config.explicitWait())
                .ignoring(StaleElementReferenceException.class)
                .withMessage("Produto '" + productName + "' não encontrado na vitrine")
                .until(d -> d.findElements(INVENTORY_ITEM).stream()
                        .filter(item -> item.findElement(ITEM_NAME).getText().equals(productName))
                        .findFirst()
                        .orElse(null));
    }
}
