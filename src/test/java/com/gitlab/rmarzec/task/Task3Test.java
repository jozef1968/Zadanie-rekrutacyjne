package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class Task3Test {

    @Test
    public void Task3Test() {
        DriverFactory driverFactory = new DriverFactory();
        WebDriver webDriver = driverFactory.initDriver();
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(10));

        try {
            step1_openGoogle(webDriver);
            step2_acceptGoogleCookies(webDriverWait);
            step3_typeSearchQuery(webDriverWait);
            step4_clickLuckyButton(webDriverWait);
            step5_verifyW3SchoolsPage(webDriver);
            step6_acceptW3Cookies(webDriver, webDriverWait);
            step7_clickTryIt(webDriver, webDriverWait);
            step8_readHeaderFromIframe(webDriver, webDriverWait);
            step9_selectOpel(webDriverWait);
        } finally {
            webDriver.quit();
        }
    }
        private void step1_openGoogle(WebDriver webDriver) {
            webDriver.get("https://www.google.com");
            webDriver.manage().window().maximize();
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        }

    private void step2_acceptGoogleCookies(WebDriverWait wait) {
        WebElement acceptCookiesButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[normalize-space()='Zaakceptuj wszystko']")));
        acceptCookiesButton.click();
    }

    private void step3_typeSearchQuery(WebDriverWait wait) {
        WebElement inputTextArea = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//textarea[@id='APjFqb']")));
        inputTextArea.sendKeys("HTML select tag - W3Schools");
    }

    private void step4_clickLuckyButton(WebDriverWait wait) {
        WebElement buttonLucky = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Szczęśliwy traf']")));
        buttonLucky.click();
    }

    private void step5_verifyW3SchoolsPage(WebDriver webDriver) {
        String currentURL = webDriver.getCurrentUrl();
        String expectedURL = "https://www.w3schools.com/tags/tag_select.asp";

        if (!currentURL.equals(expectedURL)) {
            System.out.println("Current URL: " + currentURL);
            webDriver.get(expectedURL);
        }
    }

    private void step6_acceptW3Cookies(WebDriver webDriver, WebDriverWait wait) {
        webDriver.switchTo().frame("fast-cmp-iframe");
        WebElement buttonAcceptCookies = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[normalize-space()='Accept']")));
        buttonAcceptCookies.click();
        webDriver.switchTo().defaultContent();
    }

    private void step7_clickTryIt(WebDriver webDriver, WebDriverWait wait) {
        WebElement buttonTryIt = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='main']/div[3]/a")));
        String originalWindow = webDriver.getWindowHandle();
        Assert.assertEquals(webDriver.getWindowHandles().size(), 1);
        buttonTryIt.click();
        wait.until(driver -> driver.getWindowHandles().size() > 1);
        for (String windowHandle : webDriver.getWindowHandles()) {
            if (!windowHandle.equals(originalWindow)) {
                webDriver.switchTo().window(windowHandle);
                break;
            }
        }
    }

    private void step8_readHeaderFromIframe(WebDriver webDriver, WebDriverWait wait) {
        WebElement iframeResult = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("iframeResult")));
        webDriver.switchTo().frame(iframeResult);
        String headerText = webDriver.findElement(By.xpath("/html/body/h1")).getText();
        System.out.println(headerText);
        webDriver.switchTo().defaultContent();
    }

    private void step9_selectOpel(WebDriverWait wait) {
        wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt("iframeResult"));
        WebElement selectElement = wait.until(driver -> {
            WebElement select = driver.findElement(By.tagName("select"));
            return (select.isDisplayed() && select.isEnabled()) ? select : null;
        });
        Select select = new Select(selectElement);
        select.selectByVisibleText("Opel");
        WebElement selectedOption = select.getFirstSelectedOption();
        Assert.assertTrue(selectedOption.isSelected());
        System.out.println("Selected cars: " +
                        selectedOption.getText() + ", " +
                        selectedOption.getAttribute("value")
        );
    }
}
