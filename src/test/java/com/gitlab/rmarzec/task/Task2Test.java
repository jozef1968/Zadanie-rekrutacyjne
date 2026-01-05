package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

import static java.lang.System.*;


public class Task2Test {
    final Logger log = LoggerFactory.getLogger(Task2Test.class);
    DriverFactory driverFactory = new DriverFactory();
    WebDriver webDriver = driverFactory.initDriver();
    FluentWait<WebDriver> webDriverWait = new FluentWait<>(webDriver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofSeconds(5))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);

    @Test
    public void Task2Test(){
        log.info("START testu Task2 – otwieranie strony WikiPedia");
        preparation(webDriver);
        displayLanguagesAndURLForEnglish(searchForLanguages());
        webDriver.quit();
    }

    private void preparation(WebDriver webDriver){
        webDriver.get("https://pl.wikipedia.org/wiki/Wiki");
        WebDriver.Options manage = webDriver.manage();
        manage.window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    private List<WebElement> searchForLanguages() {
        log.info("Poszukiwanie listy języków");
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'vector-dropdown mw-portlet')])[1]")));
        webDriver.findElement(By.xpath("(//div[contains(@class,'vector-dropdown mw-portlet')])[1]")).click();
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'uls-lcd-region-section')]")));
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[contains(@class,'interlanguage-link')]")));
        return webDriver.findElements(By.xpath("//a[contains(@class,'autonym')]"));
    }

    private void displayLanguagesAndURLForEnglish(List<WebElement> languages) {
        log.info("Wyświetlanie listy języków");
        int i = 1;
        for (WebElement lang : languages) {
            out.print(i++ + ": ");
            out.println(lang.getText());
            String langName = "English";
            if (lang.getText().equals(langName)) {
                String urlEnglish = lang.getAttribute("href");
                System.out.println(urlEnglish);
            }
        }
    }
}
