package fi.pohina.vinkkilista;

import cucumber.api.java.After;
import cucumber.api.java.en.*;
import java.util.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Stepdefs {
    private WebDriver driver;
    private String baseUrl = "http://localhost:4567/bookmarks/";
    private final CommaSeparatedTagsParser tagParser
        = new CommaSeparatedTagsParser();
    
    public Stepdefs() {
        driver = new HtmlUnitDriver();
    }

    // Create Bookmark

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

    @Given("search bookmarks is selected")
    public void search_bookmarks_is_selected() {
        driver.get(baseUrl);
        WebElement element = driver.findElement(By.id("searchBookmarkLink"));
        element.click();
    }

    // Search Bookmark By Tags

    @When("valid tag {string} is given as search input")
    public void valid_tag_is_given_as_search_input(String tag) {
        searchByTags(tag);
    }

    @When("valid tags {string} are given as search input")
    public void valid_tags_are_given_as_search_input(String tag) {
        searchByTags(tag);
    }

    @When("valid tags {string} and unmatching tags {string} are given as search input")
    public void valid_tags_and_unmatching_tags_are_given_as_search_input(String matchingTags, String unmatchingTags) {
        searchByTags(matchingTags + "," + unmatchingTags);
    }

    @When("no valid tag {string} is given as search input")
    public void no_valid_tag_is_given_as_search_input(String tag) {
        searchByTags(tag);
    }

    @When("no matching valid tags {string} are given as search input")
    public void no_matching_valid_tags_are_given_as_search_input(String tag) {
        searchByTags(tag);
    }

    @Then("only bookmarks with tag {string} are listed")
    public void only_bookmarks_with_tag_are_listed(String tag) {
        pageUrlIs(baseUrl + "search");

        pageHasContent("Search bookmarks by tags");
        allBookmarksHaveAnyOfTags(getBookmarkWebElements("bookmarkList"), tag);
    }

    @Then("only bookmarks with one of the tags {string} are listed")
    public void only_bookmarks_with_one_of_the_tags_are_listed(String tags) {
        pageUrlIs(baseUrl + "search");

        pageHasContent("Search bookmarks by tags");
        allBookmarksHaveAnyOfTags(getBookmarkWebElements("bookmarkList"), tags);
    }

    @Then("no bookmarks are listed")
    public void no_bookmarks_are_listed() {
        assertEquals(0, getBookmarkWebElements("bookmarkList").size());
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

    private void searchByTags(String tag) {
        typeToElementWithId("tagsInput", tag);
        submitElementWithId("submitForm");
    }

    private List<WebElement> getBookmarkWebElements(String containerClassName) {
        return driver.findElement(By.className(containerClassName))
            .findElements(By.className("bookmark"));
    }

    private void allBookmarksHaveAnyOfTags(
        List<WebElement> bookmarks,
        String tags
    ) {
        for (WebElement bookmark : bookmarks) {
            assertTrue(
                bookmarkHasAnyOfTags(bookmark, tagParser.parse(tags))
            );
        }
    }

    private Boolean bookmarkHasAnyOfTags(
        WebElement bookmark,
        Set<String> tagSet
    ) {
        WebElement tagsOfBookmark = bookmark.findElement(
            By.className("bookmarkTags")
        );

        for (String tag : tagSet) {
            if (tagsOfBookmark.getText().contains(tag)) {
                return true;
            }
        }

        return false;
    }
}
