package com.saucedemo.data;

import java.math.BigDecimal;

/** Produto como exibido na interface: nome e preço. */
public record Product(String name, BigDecimal price) {
}
