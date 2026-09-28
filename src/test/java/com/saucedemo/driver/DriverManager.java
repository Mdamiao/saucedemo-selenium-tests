package com.saucedemo.driver;

import org.openqa.selenium.WebDriver;

/**
 * Mantém um WebDriver por thread (ThreadLocal), permitindo execução paralela no futuro
 * e acesso ao driver a partir de extensões do JUnit (ex.: captura de evidências).
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static void start() {
        if (DRIVER.get() == null) {
            DRIVER.set(DriverFactory.create());
        }
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("WebDriver não iniciado. Chame DriverManager.start() antes.");
        }
        return driver;
    }

    public static boolean isActive() {
        return DRIVER.get() != null;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
