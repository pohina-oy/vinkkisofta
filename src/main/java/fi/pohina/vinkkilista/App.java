package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.api.GithubJsonApi;
import fi.pohina.vinkkilista.api.GithubOAuthApi;
import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import spark.*;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.*;

import static spark.Spark.*;

public class App {

    private final CommaSeparatedTagsParser tagParser
            = new CommaSeparatedTagsParser();

    private final GithubOAuthApi githubOAuthApi;
    private final BookmarkService bookmarkService;
    private final AppConfig config;
    private final UserService users;
    private final RequestUserManager requestUserManager;
    private String githubAuthLoginUrl;
    private boolean testLoginEnabled;

    public App(BookmarkService bookmarkService, UserService users, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.config = config;
        this.users = users;
        this.githubOAuthApi = new GithubOAuthApi(
            config.getGithubClientId(),
            config.getGithubClientSecret()
        );
        this.requestUserManager = new RequestUserManager(users);
        this.testLoginEnabled = false;

        // Construct the auth URL here instead of just directly in the template
        // so that we can change the URL to our test login endpoint
        // while running cucumber tests. Because we don't have time to mock
        // the Github API.
        this.githubAuthLoginUrl = "https://github.com/login/oauth/authorize"
            + "?scope=user:email%20read:user&client_id="
            + config.getGithubClientId();
    }

    // package-private utility functions solely for cucumber tests
    void enableTestLogin() {
        // Disables the Github login by changing the "Sign up with Github"
        // link to redirect to the test login endpoint below. This allows
        // us to go through the "click login to login" scenario, while freeing
        // us of having to write a mock server for the Github oauth & user API.

        this.githubAuthLoginUrl = "/test/login";

        if (!this.testLoginEnabled) {
            this.testLoginEnabled = true;
            // Test endpoint for logging in in the tests.
            get("/test/login", (req, res) -> {
                GithubUser testUser = new GithubUser();
                testUser.setLogin("testUser");
                testUser.setEmail("test@test.com");
                testUser.setId(123);

                User user = users.findOrCreateByGithubUser(testUser);
                requestUserManager.setSignedInUser(req, user);

                res.redirect("/bookmarks/");
                return "";
            });
        }
    }

    /**
     * Configures and ignites the Spark server, binding to the specified port.
     *
     * @param portNumber The port to which the HTTP server will be bound to.
     */
    public void startServer(int portNumber) {
        staticFileLocation("/static");
        port(portNumber);

        before("/", this::authenticationFilter);
        before("/bookmarks/*", this::authenticationFilter);
        redirect.any("/", "/bookmarks/");

        path("/bookmarks", () -> {
            get("/", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                Collection<Bookmark> bookmarks = this.bookmarkService.getAllBookmarks();
                map.put("bookmarks", bookmarks);

                User user = requestUserManager.getSignedInUser(req);
                map.put("user", user);

                return render(map, "index");
            });

            get("/new", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                return render(map, "new");
            });

            get("/search", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                return render(map, "search");
            });

            post("/new", (req, res) -> {
                String title = req.queryParams("title");
                String url = req.queryParams("url");
                String author = req.queryParams("author");
                User creator = requestUserManager.getSignedInUser(req);
                Set<String> tags = tagParser.parse(req.queryParams("tags"));

                boolean success = bookmarkService.createBookmark(title, url, author, creator, tags);

                if (success) {
                    res.redirect("/bookmarks/");
                    return "New bookmark added";

                } else {
                    res.redirect("new");
                    return "Bookmark could not be created";
                }
            });

            post("/search", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                String commaSeparatedTags = req.queryParams("tags");
                Set<String> tags = tagParser.parse(commaSeparatedTags);
                Collection<Bookmark> bookmarks = bookmarkService.getBookmarksByTags(tags);
                map.put("bookmarks", bookmarks);
                return render(map, "search");
            });
        });

        get("/login", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("authUrl", githubAuthLoginUrl);
            return render(map, "login");
        });

        get("/auth/gh-callback", (req, res) -> {
            String callbackCode = req.queryParams("code");

            String accessToken
                = githubOAuthApi.exchangeCodeForAccessToken(callbackCode);
            GithubUser githubUser
                = new GithubJsonApi(accessToken).getGithubUser();

            User user = users.findOrCreateByGithubUser(githubUser);

            requestUserManager.setSignedInUser(req, user);

            res.redirect("/bookmarks/");
            return "";
        });
    }

    /**
     * Redirects all users who have not signed in to the login page.
     */
    private void authenticationFilter(Request req, Response res) {
        User user = requestUserManager.getSignedInUser(req);

        if (user == null) {
            res.redirect("/login");
            halt();
        }
    }

    /**
     * Stops the Spark server.
     */
    public void stopServer() {
        stop();
    }

    private static String render(
            Map<String, Object> model,
            String templatePath
    ) {
        return new ThymeleafTemplateEngine().render(
                new ModelAndView(model, templatePath)
        );
    }
}
