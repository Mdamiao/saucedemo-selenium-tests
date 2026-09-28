package com.saucedemo.data;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Converte textos de preço da tela ("$29.99", "Item total: $29.99") em BigDecimal. */
public final class Money {

    private static final Pattern AMOUNT = Pattern.compile("\\$\\s*(\\d+(?:\\.\\d{1,2})?)");

    private Money() {
    }

    public static BigDecimal parse(String text) {
        Matcher matcher = AMOUNT.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Nenhum valor monetário encontrado em: '" + text + "'");
        }
        return new BigDecimal(matcher.group(1));
    }
}
