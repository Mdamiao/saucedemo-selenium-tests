package com.saucedemo.support;

import com.saucedemo.config.Config;
import com.saucedemo.driver.DriverManager;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captura evidências (screenshot + HTML da página + URL) quando um teste falha.
 *
 * Usa AfterTestExecutionCallback de propósito: ele roda logo após o método de teste e
 * ANTES dos métodos @AfterEach. Com um TestWatcher, o driver já teria sido encerrado
 * pelo @AfterEach e não seria possível tirar o screenshot.
 */
public class EvidenceOnFailureExtension implements AfterTestExecutionCallback {

    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isEmpty() || !DriverManager.isActive()) {
            return;
        }

        WebDriver driver = DriverManager.get();
        String baseName = sanitize(context.getRequiredTestClass().getSimpleName()
                + "_" + context.getRequiredTestMethod().getName()
                + "_" + context.getDisplayName())
                + "_" + LocalDateTime.now().format(TIMESTAMP);

        try {
            Path dir = Path.of(Config.evidenceDir());
            Files.createDirectories(dir);

            Path screenshot = dir.resolve(baseName + ".png");
            Files.write(screenshot, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));

            Path html = dir.resolve(baseName + ".html");
            Files.writeString(html, "<!-- URL: " + driver.getCurrentUrl() + " -->\n" + driver.getPageSource());

            context.publishReportEntry("screenshot", screenshot.toAbsolutePath().toString());
            System.out.printf("[EVIDÊNCIA] Teste falhou. Screenshot salvo em: %s%n", screenshot.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            // A captura de evidência nunca deve mascarar a falha original do teste.
            System.err.println("[EVIDÊNCIA] Não foi possível salvar evidências: " + e.getMessage());
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9-_]+", "_").replaceAll("_+", "_");
    }
}
