package fi.pohina.vinkkilista;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;


public class Stepdefs {
    private WebDriver driver;
    private String baseUrl = "http://localhost:4567/";
    
    public Stepdefs() {
        driver = new HtmlUnitDriver();
    }

    @Given("new bookmark is selected")
    public void new_bookmark_is_selected() {
        driver.get(baseUrl);
        WebElement element = driver.findElement(By.id("newBookmarkLink"));
        element.click();
    }

    @When("valid title {string} and valid url {string} are given")
    public void valid_title_and_valid_url_are_given(String title, String url) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        submitElementWithId("submitForm");
    }

    @When("valid title {string} and valid url {string} and valid author {string} are given")
    public void valid_title_and_valid_url_and_valid_author_are_given(String title, String url, String author) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        typeToElementWithId("authorInput", author);
        submitElementWithId("submitForm");
    }
    
     @When("valid title {string} and valid url {string} and valid author {string} and valid tags {string} are given")
    public void valid_title_and_valid_url_and_valid_author_and_valid_tags_are_given(String title, String url, String author, String tags) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        typeToElementWithId("authorInput", author);
        typeToElementWithId("tagsInput", tags);
        submitElementWithId("submitForm");
    }


    @When("missing title {string} and valid url {string} are given")
    public void missing_title_and_valid_url_are_given(String title, String url) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        submitElementWithId("submitForm");
    }

    @When("valid title {string} and missing url {string} are given")
    public void valid_title_and_missing_url_are_given(String title, String url) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        submitElementWithId("submitForm");
    }

    @Then("a new bookmark is created")
    public void a_new_bookmark_is_created() {
        pageUrlIs(baseUrl);
        pageHasContent("testBookmark");
    }
    
    @Then("a new bookmark is created with given inputs")
    public void a_new_bookmark_is_created_with_given_inputs() {
        pageUrlIs(baseUrl);
        pageHasContent("This is the title");
        pageHasContent("by The Author");
        pageHasContent("blog");
        pageHasContent("testing");
        
    }

    @Then("a new bookmark is not created")
    public void a_new_bookmark_is_not_created() {
        pageUrlIs(baseUrl + "new");
    }

    @After
    public void tearDown() {
        driver.quit();
    }
    
    // Helper functions
    
    private void submitElementWithId(String id) {
        WebElement element = driver.findElement(By.id(id));
        element.submit();
    }
    
    private void typeToElementWithId(String id, String text) {
        WebElement element = driver.findElement(By.id(id));
        element.sendKeys(text);
    }

    private void pageUrlIs(String url) {
        assertEquals(driver.getCurrentUrl(), url);
    }

    private void pageHasContent(String content) {
        assertTrue(driver.getPageSource().contains(content));
    }
}
