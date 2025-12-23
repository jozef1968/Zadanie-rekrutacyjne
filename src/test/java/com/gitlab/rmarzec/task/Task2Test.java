package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.Assertion.*;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.lang.System.*;


public class Task2Test {
    @Test
    public void Task2Test(){
        DriverFactory driverFactory = new DriverFactory();
        WebDriver webDriver = driverFactory.initDriver();
        WebDriverWait webDriverWait = new WebDriverWait(webDriver,  Duration.ofSeconds(10));
        preparation(webDriver);
        displayURLForEnglish(searchForLanguages(webDriver, webDriverWait), webDriver);
        webDriver.quit();
    }
    private void preparation(WebDriver webDriver){
        webDriver.get("https://pl.wikipedia.org/wiki/Wiki");
        WebDriver.Options manage = webDriver.manage();
        manage.window().maximize();
        manage.timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    private List<WebElement> searchForLanguages(WebDriver webDriver, WebDriverWait webDriverWait) {
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("(//div[contains(@class,'vector-dropdown mw-portlet')])[1]")));
        String buttonAdditionalLanguage = webDriver.findElement(By.xpath("(//div[contains(@class,'vector-dropdown mw-portlet')])[1]")).getText();
        Assert.assertEquals(buttonAdditionalLanguage,"150 języków");
        webDriver.findElement(By.xpath("(//div[contains(@class,'vector-dropdown mw-portlet')])[1]")).click();
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[contains(@class,'uls-lcd-region-section')]")));
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//li[contains(@class,'interlanguage-link')]")));
        return webDriver.findElements(By.xpath("//a[contains(@class,'autonym')]"));
    }

    private void displayURLForEnglish(List<WebElement> languages, WebDriver webDriver) {
        int i = 1;
        for (WebElement lang : languages) {
            out.print(i++ + ": ");
            out.println(lang.getText());
            String langName = "English";
            if (lang.getText().equals(langName)) {
                WebElement englishLink = webDriver.findElement(By.xpath("//a[normalize-space()='"+ langName+ "']"));
                String href = englishLink.getAttribute("href");
                System.out.println(href);
            }
        }
    }
}
