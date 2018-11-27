package fi.pohina.vinkkilista;

import cucumber.api.java.After;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import static org.junit.Assert.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;


public class Stepdefs {
    private WebDriver driver;
    private String baseUrl = "http://localhost:4567/";
    
    public Stepdefs() {
        /*ChromeDriverService.Builder builder = new ChromeDriverService.Builder();
        builder.usingDriverExecutable(new File(System.getProperty("chromeDriverBinary")));
        builder.usingAnyFreePort();
        ChromeDriverService service = builder.build();
        
        ChromeOptions opts = new ChromeOptions();
        List<String> args = new ArrayList<>();
        args.add("--disable-gpu");
        args.add("--no-sandbox");
        args.add("--headless");
        args.add("--start-maximized");
        args.add("--allow-insecure-localhost");
        opts.addArguments(args);
        opts.setBinary(System.getProperty("chromeBinary"));
        
        driver = new ChromeDriver(service, opts);*/
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
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
