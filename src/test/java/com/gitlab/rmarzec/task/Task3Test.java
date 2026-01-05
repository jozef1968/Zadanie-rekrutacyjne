package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;

public class Task3Test {
    final Logger log = LoggerFactory.getLogger(Task3Test.class);
    DriverFactory driverFactory = new DriverFactory();
    WebDriver webDriver = driverFactory.initDriver();
    FluentWait<WebDriver> webDriverWait = new FluentWait<>(webDriver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofSeconds(5))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);
    String searchFor = "HTML select tag - W3Schools";
    String auto = "Opel";

    @Test
    public void Task3Test() {
        try {
            openGoogle();
            acceptGoogleCookies();
            typeSearchQuery(searchFor);
            clickLuckyButton();
            verifyW3SchoolsPage();
            acceptW3Cookies();
            clickTryIt();
            readHeaderFromIframe();
            selectOpel();
        } finally {
            webDriver.quit();
        }
    }

    private void openGoogle() {
        log.info("Otwarcie strony google.com");
        webDriver.get("https://www.google.com");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    private void acceptGoogleCookies() {
        log.info("Akceptacja cookies dla google.com");
        WebElement acceptCookiesButton = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='L2AGLb']")));
        acceptCookiesButton.click();
    }

    private void typeSearchQuery(String searchQuery) {
        log.info("Wpisanie żądanego tekstu: " + searchQuery);
        WebElement inputTextArea = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='APjFqb']")));
        inputTextArea.sendKeys(searchQuery);
    }

    private void clickLuckyButton() {
        log.info("Kliknięcie w klawisz Szczęśliwy traf");
        WebElement buttonLucky = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@name='btnI']")));
        buttonLucky.click();
    }

    private void verifyW3SchoolsPage() {
        log.info("Załadowanie strony tag_select.asp");
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(10));
        String currentURL = webDriver.getCurrentUrl();
        String expectedURL = "https://www.w3schools.com/tags/tag_select.asp";
        if (!currentURL.equals(expectedURL)) {
            System.out.println("Current URL: " + currentURL);
            log.info("Problem z załadowaniem strony tag_select.asp");
            webDriver.get(expectedURL);
        }
    }

    private void acceptW3Cookies() {
        log.info("Akceptacja cookies");
        webDriver.switchTo().frame("fast-cmp-iframe");
        WebElement buttonAcceptCookies = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='fast-cmp-button-primary']")));
        buttonAcceptCookies.click();
        webDriver.switchTo().defaultContent();
    }

    private void clickTryIt() {
        log.info("Kliknięcie klawisza Try it");
        WebElement buttonTryIt = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='main']/div[3]/a")));
        String originalWindow = webDriver.getWindowHandle();
        Assert.assertEquals(webDriver.getWindowHandles().size(), 1);
        buttonTryIt.click();
        webDriverWait.until(driver -> driver.getWindowHandles().size() > 1);
        for (String windowHandle : webDriver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                webDriver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    private void readHeaderFromIframe() {
        log.info("Odczytanie treści nagłówka");
        WebElement iframeResult = webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframeResult")));
        webDriver.switchTo().frame(iframeResult);
        String headerText = webDriver.findElement(By.xpath("/html/body/h1")).getText();
        System.out.println(headerText);
        webDriver.switchTo().defaultContent();
    }

    private void selectOpel() {
        log.info("Wybór opla");
        webDriverWait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("iframeResult"));
        WebElement selectElement = webDriverWait.until(driver -> {
            WebElement select = driver.findElement(By.tagName("select"));
            return (select.isDisplayed() && select.isEnabled()) ? select : null;
        });
        if (selectElement != null) {
            Select select = new Select(selectElement);
            try {
                select.selectByVisibleText(auto);
                WebElement selectedOption = select.getFirstSelectedOption();
                Assert.assertTrue(selectedOption.isSelected());
                System.out.println("Selected cars: " + selectedOption.getText() + ", " + selectedOption.getAttribute("value"));
            } catch (NoSuchElementException noElement) {
                log.error("Nazwa " + auto + " nie istnieje - "+ noElement.getMessage());
            }
        } else {
            log.error("Lista aut nie istnieje!");
        }
    }
}
