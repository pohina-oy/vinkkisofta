package fi.pohina.vinkkilista;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

public class Stepdefs {

    private WebDriver driver = new ChromeDriver();
    private String baseUrl = "http://localhost:4567/";

    @Given("new bookmark is selected")
    public void new_bookmark_is_selected() {
        driver.get(baseUrl);
        WebElement element = driver.findElement(By.id("newBookmarkLink"));
        element.click();
    }

    @When("valid title {string} and valid url {string} are given")
    public void valid_title_and_valid_url_are_given(String title, String url) {
        WebElement element = driver.findElement(By.id("titleInput"));
        element.sendKeys(title);
        element = driver.findElement(By.id("urlInput"));
        element.sendKeys(url);
        element = driver.findElement(By.id("submitForm"));
        element.submit();
    }

    @When("valid title {string} and valid url {string} and valid author {string} are given")
    public void valid_title_and_valid_url_and_valid_author_are_given(String title, String url, String author) {
        WebElement element = driver.findElement(By.id("titleInput"));
        element.sendKeys(title);
        element = driver.findElement(By.id("urlInput"));
        element.sendKeys(url);
        element = driver.findElement(By.id("authorInput"));
        element.sendKeys(author);
        element = driver.findElement(By.id("submitForm"));
        element.submit();
    }

    @When("missing title {string} and valid url {string} are given")
    public void missing_title_and_valid_url_are_given(String title, String url) {
        WebElement element = driver.findElement(By.id("titleInput"));
        element.sendKeys(title);
        element = driver.findElement(By.id("urlInput"));
        element.sendKeys(url);
        element = driver.findElement(By.id("submitForm"));
        element.submit();
    }

    @When("valid title {string} and missing url {string} are given")
    public void valid_title_and_missing_url_are_given(String title, String url) {
        WebElement element = driver.findElement(By.id("titleInput"));
        element.sendKeys(title);
        element = driver.findElement(By.id("urlInput"));
        element.sendKeys(url);
        element = driver.findElement(By.id("submitForm"));
        element.submit();
    }

    @Then("a new bookmark is created")
    public void a_new_bookmark_is_created() {
        pageUrlIs(baseUrl);
    }

    @Then("a new bookmark is not created")
    public void a_new_bookmark_is_not_created() {
        pageUrlIs(baseUrl + "new");
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    private void pageUrlIs(String url) {
        assertEquals(driver.getCurrentUrl(), url);
    }

    private void pageHasContent(String content) {
        assertTrue(driver.getPageSource().contains(content));
    }
}
