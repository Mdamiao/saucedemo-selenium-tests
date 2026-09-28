package com.saucedemo.pages;

import com.saucedemo.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final By USERNAME = By.id("user-name");
    private static final By PASSWORD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test='error']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage open() {
        driver.get(Config.baseUrl());
        visible(LOGIN_BUTTON);
        return this;
    }

    /** Login com sucesso: retorna a vitrine de produtos. */
    public InventoryPage loginAs(String username, String password) {
        submitCredentials(username, password);
        return new InventoryPage(driver);
    }

    /** Login que deve falhar: permanece na página de login para validar a mensagem. */
    public LoginPage loginExpectingError(String username, String password) {
        submitCredentials(username, password);
        return this;
    }

    public String errorMessage() {
        return visible(ERROR_MESSAGE).getText();
    }

    private void submitCredentials(String username, String password) {
        type(USERNAME, username);
        type(PASSWORD, password);
        click(LOGIN_BUTTON);
    }
}
