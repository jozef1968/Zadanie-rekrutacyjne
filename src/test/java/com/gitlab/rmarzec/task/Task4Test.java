package com.gitlab.rmarzec.task;

import com.gitlab.rmarzec.framework.utils.DriverFactory;
import com.gitlab.rmarzec.model.YTTile;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class Task4Test {

    @Test
    public void Task4Test() {
        DriverFactory driverFactory = new DriverFactory();
        WebDriver webDriver = driverFactory.initDriver();
        WebDriverWait webDriverWait = new WebDriverWait(webDriver, Duration.ofSeconds(30));
        FluentWait<WebDriver> fluentWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(25))
                .pollingEvery(Duration.ofMillis(200))
                .ignoring(NoSuchElementException.class)
                .ignoring(StaleElementReferenceException.class);
        int requiredLimit = 12;

        try {
            openYoutube(webDriver);
            acceptCookies(webDriverWait);
            openShorts(webDriverWait);
            printChannelName(webDriverWait);
            returnToHomePage(webDriver);
            searchLive(webDriver, webDriverWait);
            verifySearchHeader(webDriver, webDriverWait);
            List<WebElement> filmsFiltered = collectFilteredFilms(webDriver, webDriverWait, requiredLimit);
            List<String> allVideos = readVideoDetails(filmsFiltered, requiredLimit, webDriver,  fluentWait);
            List<YTTile> ytTileList = buildYTTileList(allVideos);
            printNonLiveVideos(ytTileList);

        } finally {
            webDriver.quit();
        }
    }

    private void openYoutube(WebDriver webDriver) {
        webDriver.get("https://www.youtube.com");
        webDriver.manage().window().maximize();
        webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
    }

    private void acceptCookies(WebDriverWait wait) {
        WebElement acceptBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[.//text()='Accept all']")));
        acceptBtn.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("tp-yt-iron-overlay-backdrop")));
    }

    private void openShorts(WebDriverWait wait) {
        WebElement shortsButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//yt-formatted-string[contains(normalize-space(.),'Shorts')]/ancestor::a")));
        shortsButton.click();
    }

    private void printChannelName(WebDriverWait wait) {
        WebElement channelName = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(@href,'/@')]")));
        System.out.println(channelName.getText());
    }

    private void returnToHomePage(WebDriver webDriver) {
        webDriver.get("https://www.youtube.com");
    }

    private void searchLive(WebDriver webDriver, WebDriverWait wait) {
        WebElement searchField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='center']/yt-searchbox/div[1]/form/input")));
        searchField.sendKeys("Live");
        Assert.assertTrue(wait.
                until(ExpectedConditions.textToBePresentInElementValue(
                        webDriver.findElement(By.xpath("//*[@id='center']/yt-searchbox/div[1]/form/input")), "Live")));
        WebElement searchBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='center']/yt-searchbox/button")));
        searchBtn.click();
    }

    private void verifySearchHeader(WebDriver webDriver, WebDriverWait wait) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//ytd-search-header-renderer")));
        WebElement header = webDriver.findElement(By.xpath("//ytd-search-header-renderer//*[contains(@class,'yt-core-attributed-string')]"));
        Assert.assertEquals("About these results", header.getText());
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//ytd-video-renderer")));
    }

    private List<WebElement> collectFilteredFilms(WebDriver webDriver, WebDriverWait wait, int requiredLimit) {
        Actions actions = new Actions(webDriver);
        List<WebElement> filmsFiltered = new ArrayList<>();
        int previousSize = 0;
        while (filmsFiltered.size() < requiredLimit) {
            List<WebElement> films = webDriver.findElements(By.xpath("//ytd-video-renderer"));
            for (WebElement video : films) {
                if (filmsFiltered.size() == requiredLimit)
                    break;
                if (filmsFiltered.contains(video))
                    continue;
                boolean hasDuration = !video.findElements(By.xpath(".//ytd-thumbnail-overlay-time-status-renderer")).isEmpty();
                boolean isLive = !video.findElements(By.xpath(".//ytd-badge-supported-renderer[contains(., 'LIVE')]")).isEmpty();
                boolean isShorts = false;
                try {
                    WebElement link = video.findElement(By.xpath(".//a[@id='thumbnail']"));
                    String href = link.getAttribute("href");
                    if (href != null && href.contains("/shorts/")) {
                        isShorts = true;
                    }
                } catch (NoSuchElementException ignored) {
                }
                if ((hasDuration || isLive) && !isShorts) {
                    filmsFiltered.add(video);
                }
            }
            if (filmsFiltered.size() == previousSize)
                break;
            previousSize = filmsFiltered.size();
            actions.sendKeys(Keys.PAGE_DOWN).perform();
        }
        return filmsFiltered;
    }

    private void waitForAllChannelsToLoad(WebDriver driver, FluentWait fluentWait) {
        fluentWait.until(d -> {
            List<WebElement> channels = driver.findElements(By.xpath("//ytd-video-renderer//ytd-channel-name//a"));
            return !channels.isEmpty()  && channels.stream().allMatch(e -> e.getAttribute("href") != null
            );
        });
    }

    private List<String> readVideoDetails(List<WebElement> filmsFiltered, int requiredLimit, WebDriver driver, FluentWait fluentWait) {
        List<String> allVideos = new ArrayList<>();
        waitForAllChannelsToLoad(driver, fluentWait);
        for (WebElement video : filmsFiltered) {
            String title = video.findElement(By.xpath(".//a[@id='video-title']")).getAttribute("title");
            WebElement channelWebElement = video.findElement(By.xpath(".//ytd-channel-name//a[@href]"));
            String href = channelWebElement.getAttribute("href");
            String channel;
            if (href.contains("/@")) {
                channel = href.substring(href.lastIndexOf("/@") + 2);
            } else if (href.contains("/channel/")) {
                channel = href.substring(href.lastIndexOf("/") + 1);
            } else {
                channel = "Unknown channel";
            }
            boolean isLive = !video.findElements(By.xpath(".//ytd-badge-supported-renderer[contains(., 'LIVE')]")).isEmpty();
            if (isLive) {
                allVideos.add(title + " ; channel: " + channel + " ; duration: live");
            } else {
                List<WebElement> durationElements = video.findElements(By.xpath(".//span[contains(@class,'ytd-thumbnail-overlay-time-status-renderer')]"));
                if (!durationElements.isEmpty()) {
                    WebElement durationElement = durationElements.get(0);
                    String duration = durationElement.getText().trim();
                    if (duration.isEmpty()) {
                        duration = durationElement.getAttribute("aria-label");
                    }
                    allVideos.add(title + " ; channel: " + channel + " ; duration: " + duration);
                }
            }
            if (allVideos.size() == requiredLimit)
                break;
        }
        return allVideos;
    }

    private List<YTTile> buildYTTileList(List<String> allVideos) {
        List<YTTile> ytTileList = new ArrayList<>();
        for (String oneVideo : allVideos) {
            String[] infoAboutFilms = oneVideo.split(";");
            YTTile ytTile = new YTTile();
            ytTile.setTitle(infoAboutFilms[0]);
            ytTile.setChannel(infoAboutFilms[1].split(":")[1]);
            ytTile.setLength(infoAboutFilms[2].split(":")[1]);
            ytTileList.add(ytTile);
        }
        return ytTileList;
    }

    private void printNonLiveVideos(List<YTTile> ytTileList) {
        for (YTTile ytTileEntry : ytTileList) {
            if (!ytTileEntry.getLength().trim().equals("live")) {
                System.out.println("Title: " + ytTileEntry.getTitle() + " duration: " + ytTileEntry.getLength());
            }
        }
    }
}

