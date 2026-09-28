package com.saucedemo.config;

import java.time.Duration;

/**
 * Configuração centralizada da execução.
 *
 * Ordem de precedência de cada chave:
 *   1. System property  (ex.: mvn test -Dheadless=true)
 *   2. Variável de ambiente (ex.: HEADLESS=true) — ponto vira underscore e tudo maiúsculo
 *   3. Valor padrão definido aqui
 */
public final class Config {

    private Config() {
    }

    public static String baseUrl() {
        return get("base.url", "https://www.saucedemo.com");
    }

    /** chrome (padrão), firefox ou edge. */
    public static String browser() {
        return get("browser", "chrome").toLowerCase();
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    /** Quando preenchida, os testes rodam num Selenium Grid/Standalone remoto (ex.: Docker). */
    public static String remoteUrl() {
        return get("selenium.remote.url", "");
    }

    public static Duration explicitWait() {
        return Duration.ofSeconds(Long.parseLong(get("explicit.wait.seconds", "10")));
    }

    public static Duration pageLoadTimeout() {
        return Duration.ofSeconds(Long.parseLong(get("page.load.timeout.seconds", "30")));
    }

    public static String evidenceDir() {
        return get("evidence.dir", "target/evidencias");
    }

    private static String get(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (isBlank(value)) {
            value = System.getenv(key.replace('.', '_').toUpperCase());
        }
        return isBlank(value) ? defaultValue : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
