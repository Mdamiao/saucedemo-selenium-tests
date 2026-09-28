package com.saucedemo.driver;

import com.saucedemo.config.Config;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Map;

/**
 * Cria a instância de WebDriver de acordo com a configuração.
 * O driver do navegador é resolvido automaticamente pelo Selenium Manager (Selenium 4.6+),
 * então não é necessário baixar chromedriver/geckodriver manualmente.
 */
public final class DriverFactory {

    private static final String WINDOW_SIZE = "--window-size=1366,768";

    private DriverFactory() {
    }

    public static WebDriver create() {
        String browser = Config.browser();
        String remoteUrl = Config.remoteUrl();

        WebDriver driver = remoteUrl.isBlank()
                ? createLocal(browser)
                : createRemote(remoteUrl, optionsFor(browser));

        driver.manage().timeouts().pageLoadTimeout(Config.pageLoadTimeout());
        return driver;
    }

    private static WebDriver createLocal(String browser) {
        return switch (browser) {
            case "firefox" -> new FirefoxDriver(firefoxOptions());
            case "edge" -> new EdgeDriver(edgeOptions());
            default -> new ChromeDriver(chromeOptions());
        };
    }

    private static WebDriver createRemote(String remoteUrl, Capabilities options) {
        try {
            return new RemoteWebDriver(URI.create(remoteUrl).toURL(), options);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL do Selenium remoto inválida: " + remoteUrl, e);
        }
    }

    private static Capabilities optionsFor(String browser) {
        return switch (browser) {
            case "firefox" -> firefoxOptions();
            case "edge" -> edgeOptions();
            default -> chromeOptions();
        };
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments(WINDOW_SIZE, "--disable-dev-shm-usage", "--disable-notifications");
        if (Config.headless()) {
            options.addArguments("--headless=new", "--no-sandbox");
        }
        // Evita o pop-up "Alterar sua senha" (detecção de senha vazada) que o Chrome
        // exibe após o login com as credenciais públicas do SauceDemo.
        options.setExperimentalOption("prefs", Map.of(
                "credentials_enable_service", false,
                "profile.password_manager_enabled", false,
                "profile.password_manager_leak_detection", false
        ));
        return options;
    }

    private static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments(WINDOW_SIZE, "--disable-dev-shm-usage");
        if (Config.headless()) {
            options.addArguments("--headless=new");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--width=1366", "--height=768");
        if (Config.headless()) {
            options.addArguments("-headless");
        }
        return options;
    }
}
