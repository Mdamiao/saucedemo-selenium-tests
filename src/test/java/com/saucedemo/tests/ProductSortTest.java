package com.saucedemo.tests;

import com.saucedemo.pages.InventoryPage;
import com.saucedemo.pages.SortOption;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Filtro de ordenação da vitrine")
class ProductSortTest extends BaseTest {

    @Test
    @DisplayName("Deve ordenar os produtos por preço, do menor para o maior")
    void shouldSortProductsByPriceLowToHigh() {
        InventoryPage inventory = loginAsStandardUser().sortBy(SortOption.PRICE_LOW_TO_HIGH);

        assertThat(inventory.activeSortLabel()).isEqualTo(SortOption.PRICE_LOW_TO_HIGH.label());
        assertThat(inventory.productPrices())
                .as("Preços devem estar em ordem crescente")
                .isNotEmpty()
                .isSorted();
    }

    @Test
    @DisplayName("Deve ordenar os produtos por nome, de Z para A")
    void shouldSortProductsByNameZToA() {
        InventoryPage inventory = loginAsStandardUser().sortBy(SortOption.NAME_Z_TO_A);

        assertThat(inventory.activeSortLabel()).isEqualTo(SortOption.NAME_Z_TO_A.label());
        assertThat(inventory.productNames())
                .as("Nomes devem estar em ordem alfabética decrescente")
                .isNotEmpty()
                .isSortedAccordingTo(Comparator.reverseOrder());
    }
}
