package com.saucedemo.tests;

import com.saucedemo.driver.DriverManager;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import com.saucedemo.support.EvidenceOnFailureExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import static com.saucedemo.data.TestData.PASSWORD;
import static com.saucedemo.data.TestData.STANDARD_USER;

/** Ciclo de vida do navegador: um navegador limpo por teste (isolamento total entre cenários). */
@ExtendWith(EvidenceOnFailureExtension.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void startBrowser() {
        DriverManager.start();
        driver = DriverManager.get();
    }

    @AfterEach
    void closeBrowser() {
        DriverManager.quit();
    }

    /** Atalho para os testes cujo foco não é o login. */
    protected InventoryPage loginAsStandardUser() {
        return new LoginPage(driver).open().loginAs(STANDARD_USER, PASSWORD);
    }
}
