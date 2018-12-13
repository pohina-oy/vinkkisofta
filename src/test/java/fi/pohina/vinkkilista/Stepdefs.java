package fi.pohina.vinkkilista;

import cucumber.api.java.After;
import cucumber.api.java.en.*;
import fi.pohina.vinkkilista.domain.TagService;

import java.text.SimpleDateFormat;
import java.util.*;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class Stepdefs {
    private WebDriver driver;
    private String baseUrl = "http://localhost:4567/bookmarks/";
    private String loginUrl = "http://localhost:4567/login";

    public Stepdefs() {
        driver = new HtmlUnitDriver();
    }

    @Given("new bookmark is selected")
    public void new_bookmark_is_selected() {
        loginWithTestUser();
        navigateToBookmarkListing();
        WebElement element = driver.findElement(By.id("newBookmarkLink"));
        element.click();
    }

    @Given("^the user is on the login page$")
    public void theUserIsOnTheLoginPage() {
        enableTestLogin();
        navigateToLogin();
    }

    @Given("^the user has not logged in$")
    public void theUserHasNotLoggedIn() {
        driver.manage().deleteAllCookies();
        navigateToLogin();
    }

    // Logout

    @Given("^the user is logged in on homepage$")
    public void theUserIsLoggedInOnHomepage() {
        navigateToBookmarkListing();
        loginWithTestUser();
    }
    @Given("^the user is logged in on search page")
    public void theUserIsLoggedInOnSearchPage() {
        navigateToSearchPage();
        loginWithTestUser();
    }
    @Given("^the user is logged in on create page")
    public void theUserIsLoggedInOnCreatePage() {
        navigateToCreatePage();
        loginWithTestUser();

    }

    @When("^the \"logout\" link is clicked$")
    public void theLogoutLinkIsClicked() {
        WebElement element = driver
                .findElement(By.id("userLogInLink"));
        element.click();
    }

    //@Then("^the user is logged out and redirected to the login page$")
    //public void theUserIsLoggedOutAndRedirectedToTheLoginPage() {
    //assertCurrentUrlContains("/login");
    //}

    // End logout

    // Toggle read


    @Given("given first bookmark read status contains {string}")
    public void the_First_Bookmark_Read_Status_Contains(String status) {
        WebElement firstBookmark = getBookmarkElementById(getFirstBookmarkId());
        assertBookmarkReadStatusContains(firstBookmark, status);
    }


    @When("^the first bookmark marking button is clicked$")
    public void theMarkingButtonIsClicked() {


        WebElement firstBookmark = getBookmarkElementById(getFirstBookmarkId());
        WebElement readToggleButton = getBookmarkElementReadToggleButtonElement(firstBookmark);

        readToggleButton.click();
    }
    @Then("the first bookmark read status contains {string}")
    public void theFirstBookmarkReadStatusContainsReadOn(String status) {

        WebElement firstBookmark = getBookmarkElementById(getFirstBookmarkId());
        assertBookmarkReadStatusContains(firstBookmark, status);
    }

    @Then("the first bookmark read status contains the current date")
    public void the_first_bookmark_read_status_contains_the_current_date() {

        String dateInString = new SimpleDateFormat("dd.MM.yyyy").format(new Date());

        WebElement firstBookmark = getBookmarkElementById(getFirstBookmarkId());
        assertBookmarkReadStatusContains(firstBookmark, dateInString);
    }

    @Then("^the new bookmark read status contains \"not read\"")
    public void the_new_bookmark_read_status_contains_not_read() {
        WebElement newBookmark = getBookmarkElementByTitle("readStatusBookmark");
        System.out.println(newBookmark.findElement(By.className("bookmarkTitle")).getText());
        assertBookmarkReadStatusContains(newBookmark, "not read");
    }

    // End toggle read

    @When("^the user navigates to /bookmarks/$")
    public void theUserNavigatesToBookmarks() {
        navigateToBookmarkListing();
    }

    @When("^the \"Sign in with Github\" link is clicked$")
    public void theSignInWithGithubLinkIsClicked() {
        WebElement element = driver
                .findElement(By.linkText("Sign in with Github"));
        element.click();
    }

    @When("valid title {string} and valid url {string} are given")
    public void valid_title_and_valid_url_are_given(String title, String url) {
        attemptBookmarkCreation(title, url, "", "");
    }

    @When("valid title {string} and valid url {string} and valid author {string} are given")
    public void valid_title_and_valid_url_and_valid_author_are_given(String title, String url, String author) {
        attemptBookmarkCreation(title, url, author, "");
    }

    @When("valid title {string} and valid url {string} and valid author {string} and valid tags {string} are given")
    public void valid_title_and_valid_url_and_valid_author_and_valid_tags_are_given(String title, String url, String author, String tags) {
        attemptBookmarkCreation(title, url, author, tags);
    }

    @When("missing title {string} and valid url {string} are given")
    public void missing_title_and_valid_url_are_given(String title, String url) {
        attemptBookmarkCreation(title, url, "", "");
    }

    @When("valid title {string} and missing url {string} are given")
    public void valid_title_and_missing_url_are_given(String title, String url) {
        attemptBookmarkCreation(title, url, "", "");
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
        loginWithTestUser();
        navigateToBookmarkListing();
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

    // Add Tags Based On Url
    
    @Then("a bookmark with title {string} is listed in search results")
    public void a_bookmark_with_title_is_listed_in_search_results(String title) {
        pageUrlIs(baseUrl + "search");
        WebElement bookmark;
        bookmark = getBookmarkElementByTitle(getBookmarkWebElements("bookmarkList"), title);
        
        pageHasContent("Search bookmarks by tags");
        assertTrue(bookmark.findElement(By.className("bookmarkTitle")).getText().contains(title));
    }
    
    @Then("a bookmark with title {string} is not listed in search results")
    public void a_bookmark_with_title_is_not_listed_in_search_results(String title) {
        pageUrlIs(baseUrl + "search");

        pageHasContent("Search bookmarks by tags");
        pageDoesNotHaveContent(title);
    }

    @Then("^the user is logged in and redirected to the bookmarks$")
    public void theUserIsLoggedInAndRedirectedToTheBookmarks() throws Throwable {
        assertCurrentUrlContains("/bookmarks/");
        assertLoggedInUserIs("testUser");
    }

    @Then("^the user is redirected to the login page$")
    public void theUserIsRedirectedToTheLoginPage() {
        pageUrlIs(loginUrl);
        assertTrue(driver.getTitle().contains("Sign in"));
    }
    
    // Bookmark Tags
    
    @When("valid title {string} and valid url {string} and valid tags {string} are given")
    public void valid_title_and_valid_url_and_valid_tags_are_given(String title, String url, String tags) {
        attemptBookmarkCreation(title, url, "", tags);
    }
    
    @Then("tag {string} is shown in bookmark listing")
    public void tag_is_shown_in_bookmark_listing_on_bookmark_with_title(String tag) {
        pageUrlIs(baseUrl);
        
        pageHasContent(tag);
    }
    
    // Bookmark Listing
    
    @Then("bookmark with title {string} is listed")
    public void bookmark_with_title_is_listed(String title) {
        pageUrlIs(baseUrl);
        WebElement bookmark;
        bookmark = getBookmarkElementByTitle(getBookmarkWebElements("bookmarkList"), title);
        
        assertTrue(bookmark.findElement(By.className("bookmarkTitle")).getText().contains(title));
    }
    
    @Then("bookmark with title {string} is not listed")
    public void bookmark_with_title_is_not_listed(String title) {
        pageUrlIs(baseUrl);
        
        pageDoesNotHaveContent(title);
    }
    
    // No Bookmark Type
    
    @Then("there is no type field")
    public void there_is_no_type_field() {
        pageUrlIs(baseUrl + "new");
        
        pageDoesNotHaveContent("Type");
    }
    
    // Bookmark creator
    
    @Given("user is logged in and new bookmark is selected")
    public void user_is_logged_in_and_new_bookmark_is_selected() {
        loginWithTestUser();
        navigateToBookmarkListing();
        WebElement element = driver.findElement(By.id("newBookmarkLink"));
        element.click();
    }
    
    @Then("the user {string} is shown as creator of the bookmark with title {string}")
    public void the_user_is_shown_as_creator_of_the_bookmark_with_title(String user, String title) {
        pageUrlIs(baseUrl);
        WebElement bookmark;
        bookmark = getBookmarkElementByTitle(getBookmarkWebElements("bookmarkList"), title);
        
        assertTrue(bookmark.findElement(By.className("bookmarkCreator")).getText().contains(user));
    }

    @After
    public void tearDown() {
        driver.quit();
    }

    // Helper functions

    private void attemptBookmarkCreation(String title, String url, String author, String tags) {
        typeToElementWithId("titleInput", title);
        typeToElementWithId("urlInput", url);
        typeToElementWithId("authorInput", author);
        typeToElementWithId("tagsInput", tags);
        submitElementWithId("submitForm");
    }

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

    private void pageDoesNotHaveContent(String content) {
        assertTrue(!driver.getPageSource().contains(content));
    }

    private void searchByTags(String tag) {
        typeToElementWithId("tagsInput", tag);
        submitElementWithId("submitForm");
    }

    private List<WebElement> getBookmarkWebElements(String containerClassName) {
        return driver.findElement(By.className(containerClassName))
                .findElements(By.className("bookmark"));
    }

    private void allBookmarksHaveAnyOfTags(List<WebElement> bookmarks, String tags) {
        for (WebElement bookmark : bookmarks) {
            assertTrue(
                    bookmarkHasAnyOfTags(bookmark, TagService.toValidatedSet(tags))
            );
        }
    }
    
    private Boolean bookmarkHasAnyOfTags(WebElement bookmark, Set<String> tagSet) {
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
    
    private Boolean bookmarkHasAllOfTags(WebElement bookmark, Set<String> tagSet) {
        WebElement tagsOfBookmark = bookmark.findElement(
            By.className("bookmarkTags")
        );

        for (String tag : tagSet) {
            if (!tagsOfBookmark.getText().contains(tag)) {
                return false;
            }
        }

        return true;
    }
    
    private WebElement getBookmarkElementByTitle(List<WebElement> bookmarks, String title) {
        for (WebElement bookmark : bookmarks) {
            if (bookmark.findElement(By.className("bookmarkTitle")).getText().contains(title)) {
                return bookmark;
            }
        }
        return null;
    }
    
    private void loginWithTestUser() {
        enableTestLogin();
        navigateToLogin();
        clickLogin();
    }

    private void enableTestLogin() {
        RunCukesTest.server.getApp().enableTestLogin();
    }

    private void navigateToLogin() {
        driver.get(loginUrl);
    }

    private void clickLogin() {
        WebElement element = driver.findElement(By.id("login"));
        element.click();
    }

    private void assertBookmarkReadStatusContains(WebElement bookmark, String status) {
        WebElement readToggle = getBookmarkElementReadStatus(bookmark);

        if (!readToggle.getText().toLowerCase().contains(status)) {
            fail("Bookmark element read toggle '" + readToggle.getText() + "' did not contain '" + status + "'");
        }
    }

    private void assertCurrentUrlContains(String substring) {
        String currentUrl = driver.getCurrentUrl();
        assertTrue(currentUrl.contains(substring));
    }

    private String getFirstBookmarkId() {
        return getBookmarkElementId(getBookmarkWebElements("bookmarkList").get(0));
    }

    private String getLastBookmarkId() {
        List<WebElement> elements = getBookmarkWebElements("bookmarkList");
        return getBookmarkElementId(elements.get(elements.size() - 1));
    }

    private WebElement getBookmarkElementReadStatus(WebElement element) {
        return element.findElement(By.className("bookmarkReadStatus"));
    }

    private WebElement getBookmarkElementReadToggleButtonElement(WebElement element) {
        return element.findElement(By.className("markBookmarkButton"));
    }

    private WebElement getBookmarkElementById(String id) {

        List<WebElement> bookmarks = getBookmarkWebElements("bookmarkList");

        for (WebElement element : bookmarks) {
            String currentId = getBookmarkElementId(element);

            if (id.equals(currentId)) {
                return element;
            }
        }
        return null;
    }

    private String getBookmarkElementId(WebElement bookmark) {
        return bookmark.findElement(By.name("bookmarkId")).getAttribute("value");
    }

    private WebElement getBookmarkElementByTitle(String title) {
        List<WebElement> bookmarks = getBookmarkWebElements("bookmarkList");

        for (WebElement element : bookmarks) {
            String currentTitle = getBookmarkElementTitle(element);

            if (title.equals(currentTitle)) {
                return element;
            }
        }
        return null;
    }

    private String getBookmarkElementTitle(WebElement bookmark) {
        return bookmark.findElement(By.className("bookmarkTitle")).getText();
    }

    private void assertLoggedInUserIs(String expectedUsername) {
        String currentUser = driver
                .findElement(By.id("userStatusText"))
                .getText();
        assertEquals(
                "You are logged in as: " + expectedUsername,
                currentUser
        );
    }

    private void navigateToBookmarkListing() {
        driver.get(baseUrl);
    }
    private void navigateToCreatePage() {
        driver.get(baseUrl + "new");
    }
    private void navigateToSearchPage() {
        driver.get(baseUrl + "search");
    }
}
