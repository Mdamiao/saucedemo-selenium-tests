package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.saucedemo.data.TestData.LOCKED_OUT_USER;
import static com.saucedemo.data.TestData.PASSWORD;
import static com.saucedemo.data.TestData.STANDARD_USER;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Login")
class LoginTest extends BaseTest {

    @Test
    @DisplayName("Deve autenticar o usuário padrão e exibir a vitrine de produtos")
    void shouldLoginWithStandardUser() {
        InventoryPage inventory = new LoginPage(driver).open().loginAs(STANDARD_USER, PASSWORD);

        assertThat(inventory.currentUrl()).as("URL após login").endsWith("/inventory.html");
        assertThat(inventory.title()).as("Título da página após login").isEqualTo("Products");
        assertThat(inventory.productNames()).as("Vitrine deve listar produtos").isNotEmpty();
    }

    @Test
    @DisplayName("Deve impedir o login de um usuário bloqueado e exibir mensagem de erro")
    void shouldBlockLockedOutUser() {
        LoginPage login = new LoginPage(driver).open().loginExpectingError(LOCKED_OUT_USER, PASSWORD);

        assertThat(login.errorMessage())
                .as("Mensagem de erro para usuário bloqueado")
                .isEqualTo("Epic sadface: Sorry, this user has been locked out.");
        assertThat(login.currentUrl()).as("Usuário deve permanecer na tela de login")
                .doesNotContain("inventory");
    }
}
