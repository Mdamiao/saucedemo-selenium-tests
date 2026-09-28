package com.saucedemo.tests;

import com.saucedemo.data.Product;
import com.saucedemo.pages.CartPage;
import com.saucedemo.pages.CheckoutCompletePage;
import com.saucedemo.pages.CheckoutInformationPage;
import com.saucedemo.pages.CheckoutOverviewPage;
import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.saucedemo.data.TestData.FIRST_NAME;
import static com.saucedemo.data.TestData.LAST_NAME;
import static com.saucedemo.data.TestData.POSTAL_CODE;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Fluxo principal do desafio:
 * login -> filtrar/buscar produto -> adicionar ao carrinho -> validar o produto no checkout.
 */
@DisplayName("Fluxo de compra: busca, carrinho e checkout")
class CheckoutFlowTest extends BaseTest {

    @ParameterizedTest(name = "[{index}] Produto: {0}")
    @ValueSource(strings = {"Sauce Labs Backpack", "Sauce Labs Fleece Jacket"})
    @DisplayName("Deve filtrar o produto, adicioná-lo ao carrinho e validá-lo no checkout")
    void shouldAddProductToCartAndValidateItOnCheckout(String productName) {
        // 1. Login
        InventoryPage inventory = loginAsStandardUser();
        assertThat(inventory.title()).as("Vitrine exibida após login").isEqualTo("Products");
        assertThat(inventory.header().cartItemCount()).as("Carrinho deve iniciar vazio").isZero();

        // 2. Filtrar e localizar o produto
        inventory.sortBy(SortOption.PRICE_LOW_TO_HIGH);
        assertThat(inventory.activeSortLabel()).as("Filtro aplicado")
                .isEqualTo(SortOption.PRICE_LOW_TO_HIGH.label());
        assertThat(inventory.isProductDisplayed(productName))
                .as("Produto '%s' deve estar na vitrine", productName).isTrue();
        Product expected = inventory.product(productName);

        // 3. Adicionar ao carrinho
        inventory.addToCart(productName);
        assertThat(inventory.cartButtonLabel(productName))
                .as("Botão do produto deve mudar para 'Remove'").isEqualToIgnoringCase("Remove");
        assertThat(inventory.header().cartItemCount()).as("Badge do carrinho").isEqualTo(1);

        // 4. Validar o carrinho
        CartPage cart = inventory.header().openCart();
        assertThat(cart.title()).isEqualTo("Your Cart");
        assertThat(cart.items()).as("Itens do carrinho").containsExactly(expected);
        assertThat(cart.quantityOf(productName)).as("Quantidade no carrinho").isEqualTo(1);

        // 5. Informar dados do comprador
        CheckoutInformationPage information = cart.checkout();
        assertThat(information.title()).isEqualTo("Checkout: Your Information");
        CheckoutOverviewPage overview = information
                .fillInformation(FIRST_NAME, LAST_NAME, POSTAL_CODE)
                .continueToOverview();

        // 6. Validar o produto no checkout (etapa principal do desafio)
        assertThat(overview.title()).isEqualTo("Checkout: Overview");
        assertThat(overview.items()).as("Itens no resumo do checkout").containsExactly(expected);
        assertThat(overview.quantityOf(productName)).as("Quantidade no checkout").isEqualTo(1);
        assertThat(overview.itemTotal()).as("Item total deve ser igual ao preço do produto")
                .isEqualByComparingTo(expected.price());

        // 7. Finalizar a compra
        CheckoutCompletePage complete = overview.finish();
        assertThat(complete.title()).isEqualTo("Checkout: Complete!");
        assertThat(complete.confirmationMessage()).isEqualTo("Thank you for your order!");
        assertThat(complete.header().cartItemCount()).as("Carrinho deve ser esvaziado").isZero();
    }
}
