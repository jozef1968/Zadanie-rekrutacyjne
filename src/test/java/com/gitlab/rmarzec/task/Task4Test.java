package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import com.gitlab.rmarzec.model.YTTile;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Task4Test {
    final Logger log = LoggerFactory.getLogger(Task4Test.class);
    DriverFactory driverFactory = new DriverFactory();
    WebDriver webDriver = driverFactory.initDriver();
    FluentWait<WebDriver> webDriverWait = new FluentWait<>(webDriver)
            .withTimeout(Duration.ofSeconds(30))
            .pollingEvery(Duration.ofSeconds(5))
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class);

    @Test
    public void Task4Test() {
        log.info("START testu Task4 – wyszukiwanie filmów bez LIVE");
        int requiredLimit = 12;

        try {
            openYoutube();
            acceptCookies();
            openShorts();
            printChannelName();
            returnToHomePage();
            searchLive();
            verifySearchHeader();
            List<YTTile> ytTileList = collectFilteredFilms(requiredLimit);
            printNonLiveVideos(ytTileList);
        } finally {
            webDriver.quit();
        }
    }

    private void openYoutube() {
        log.info("Otwarcie strony youtube.com");
        webDriver.get("https://www.youtube.com");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
    }

    private void acceptCookies() {
        log.info("Akceptacja cookies dla youtube.com");
        WebElement acceptBtn = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ytd-consent-bump-v2-lightbox//tp-yt-paper-dialog//*[@id='content']//ytd-button-renderer[position()=last()]//button")));
        acceptBtn.click();
        webDriverWait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("tp-yt-iron-overlay-backdrop")));
    }

    private void openShorts() {
        log.info("Przejście na zakładkę Shorts");
        WebElement shortsButton = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//yt-formatted-string[contains(normalize-space(.),'Shorts')]/ancestor::a")));
        shortsButton.click();
    }

    private void printChannelName() {
        log.info("Wypisanie nazwy kanału");
        WebElement channelName = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/@')]")));
        System.out.println(channelName.getText());
    }

    private void returnToHomePage() {
        log.info("Powrót na stronę główną linkiem Home");
        WebElement home = webDriver.findElement(By.xpath("//ytd-guide-entry-renderer//a[@href='/']"));
        Actions actions = new Actions(webDriver);
        actions.moveToElement(home).pause(Duration.ofMillis(2000)).clickAndHold().pause(Duration.ofMillis(2000)).release().perform();
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("ytd-rich-grid-renderer")));
    }

    private void searchLive() {
        log.info("Szukanie filmów LIVE");
        WebElement searchField = webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='center']/yt-searchbox/div[1]/form/input")));
        searchField.sendKeys("Live");
        Assert.assertTrue(webDriverWait.until(ExpectedConditions.textToBePresentInElementValue(webDriver.findElement(By.xpath("//*[@id='center']/yt-searchbox/div[1]/form/input")), "Live")));
        WebElement searchBtn = webDriverWait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='center']/yt-searchbox/button")));
        searchBtn.click();
    }

    private void verifySearchHeader() {
        log.info("Sprawdzenie nagłówka strony");
        webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ytd-search-header-renderer")));
        webDriverWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ytd-video-renderer")));
    }

    private List<YTTile> collectFilteredFilms(int requiredLimit) {
        log.info("Zbieram listę filmów (limit = {})", requiredLimit);
        List<YTTile> ytTileList = new ArrayList<>();
        do {
            List<WebElement> films = webDriver.findElements(By.xpath("//ytd-video-renderer"));
            for (WebElement video : films) {
                if (ytTileList.size() == requiredLimit)
                    break;
                boolean hasDuration = !video.findElements(By.xpath(".//ytd-thumbnail-overlay-time-status-renderer")).isEmpty();
                boolean isLive = !video.findElements(By.xpath(".//ytd-badge-supported-renderer[contains(., 'LIVE')]")).isEmpty();
                String title = video.findElement(By.xpath(".//a[@id='video-title']")).getAttribute("title");
                checkingKindOfFilms(video, ytTileList, title, hasDuration, isLive);
            }
            if (ytTileList.size() < requiredLimit) {
                JavascriptExecutor js = (JavascriptExecutor) webDriver;
                js.executeScript("window.scrollBy(0, 400);");
            }
        } while (ytTileList.size() < requiredLimit);
        return ytTileList;
    }

    private void checkingKindOfFilms(WebElement video, List<YTTile> ytTileList, String title, boolean hasDuration, boolean isLive) {
        boolean exists = ytTileList.stream().anyMatch(tile -> title.equals(tile.getTitle()));
        if (!exists) {
            String channelFull = (video.findElement(By.xpath(".//ytd-channel-name//a[@href]")).getAttribute("href"));
            String channel = channelFull.substring(channelFull.indexOf("@") + 1);
            boolean isShorts = false;
            try {
                WebElement link = video.findElement(By.xpath(".//a[@id='thumbnail']"));
                String href = link.getAttribute("href");
                if (href != null && href.contains("/shorts/")) {
                    isShorts = true;
                    log.info("Short found");
                }
            } catch (NoSuchElementException ignored) {
            }
            if ((hasDuration || isLive) && !isShorts) {
                evaluateLiveOrNotLive(video, isLive, ytTileList, title, channel);
            }
        }
    }

    private void evaluateLiveOrNotLive(WebElement video, boolean isLive, List<YTTile> ytTileList, String title, String channel) {
        if (isLive) {
            ytTileList.add(updateYTTileObject(title, channel,"live"));
        } else {
            List<WebElement> durationElements = video.findElements(By.xpath(".//span[contains(@class,'ytd-thumbnail-overlay-time-status-renderer')]"));
            if (!durationElements.isEmpty()) {
                WebElement durationElement = durationElements.get(0);
                String duration = durationElement.getText().trim();
                if (duration.isEmpty()) {
                    duration = durationElement.getAttribute("aria-label");
                }
                log.debug("Film: {} | kanał: {} | live: {}", title, channel, isLive);
                ytTileList.add(updateYTTileObject(title, channel, duration));
            }
        }
    }

    private YTTile updateYTTileObject(String title, String channel, String duration) {
        YTTile ytTile = new YTTile();
        ytTile.setTitle(title);
        ytTile.setChannel(channel);
        ytTile.setLength(duration);
        return ytTile;
    }

    private void printNonLiveVideos(List<YTTile> ytTileList) {
        log.info("Wyświetlam filmy bez znacznika Live");
        int counter = 1;
        for (YTTile ytTileEntry : ytTileList) {
            if (!ytTileEntry.getLength().trim().equals("live")) {
                System.out.println("Title " + counter++ + ": " + ytTileEntry.getTitle() + " duration: " + ytTileEntry.getLength());
            }
        }
    }
}

