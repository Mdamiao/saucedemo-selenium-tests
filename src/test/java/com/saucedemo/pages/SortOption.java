package com.saucedemo.pages;

/** Opções do filtro de ordenação da vitrine (value do &lt;select&gt; + texto exibido). */
public enum SortOption {

    NAME_A_TO_Z("az", "Name (A to Z)"),
    NAME_Z_TO_A("za", "Name (Z to A)"),
    PRICE_LOW_TO_HIGH("lohi", "Price (low to high)"),
    PRICE_HIGH_TO_LOW("hilo", "Price (high to low)");

    private final String value;
    private final String label;

    SortOption(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }
}
