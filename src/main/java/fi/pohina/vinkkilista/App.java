package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.api.GithubJsonApi;
import fi.pohina.vinkkilista.api.GithubOAuthApi;
import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.Tag;
import fi.pohina.vinkkilista.domain.TagService;
import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import spark.*;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.util.*;

import static spark.Spark.*;

public class App {

    private final GithubOAuthApi githubOAuthApi;
    private final BookmarkService bookmarkService;
    private final TagService tagService;
    private final UserService users;
    private final RequestUserManager requestUserManager;
    private String githubAuthLoginUrl;
    private boolean testLoginEnabled;

    public App(BookmarkService bookmarkService, TagService tagService, UserService userService, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.tagService = tagService;
        this.users = userService;
        this.githubOAuthApi = new GithubOAuthApi(
            config.getGithubClientId(),
            config.getGithubClientSecret()
        );
        this.requestUserManager = new RequestUserManager(userService);
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
                Collection<Bookmark> bookmarks = bookmarkService.getAllBookmarks();

                setUserStatusToMap(req, map);
                map.put("bookmarks", bookmarks);
                map.put("user", replaceNullUserWithGuest(requestUserManager.getSignedInUser(req)));

                return render(map, "index");
            });

            get("/new", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                setUserStatusToMap(req, map);
                return render(map, "new");
            });

            get("/search", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                setUserStatusToMap(req, map);
                map.put("user", requestUserManager.getSignedInUser(req));
                map.put("user", replaceNullUserWithGuest(requestUserManager.getSignedInUser(req)));
                return render(map, "search");
            });

            post("/", (req, res) -> {
                toggleBookmarkReadStatus(req);
                res.redirect("/bookmarks/");
                return null;
            });

            post("/new", (req, res) -> {
                String title = req.queryParams("title");
                String url = req.queryParams("url");
                String author = req.queryParams("author");
                User creator = requestUserManager.getSignedInUser(req);
                Set<String> tagNames = tagService.toValidatedSet(req.queryParams("tags"));
                tagNames.add(tagService.tagFromUrl(url));
                
                Set<Tag> tags = tagService.findOrCreateTags(tagNames);
                Bookmark created = bookmarkService.createBookmark(title, url, author, creator, tags);

                if (created != null) {
                    res.redirect("/bookmarks/");
                    return "New bookmark added";

                } else {
                    res.redirect("new");
                    return "Bookmark could not be created";
                }
            });

            post("/search", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                String tagInput = req.queryParams("tags");

                toggleBookmarkReadStatus(req);

                Set<String> tags = tagService.toValidatedSet(tagInput);
                Collection<Bookmark> bookmarks = bookmarkService.getBookmarksByTags(tags);
                setUserStatusToMap(req, map);
                map.put("bookmarks", bookmarks);
                map.put("user", replaceNullUserWithGuest(requestUserManager.getSignedInUser(req)));
                map.put("tags", tagInput);

                return render(map, "search");
            });
        });

        get("/login", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("authUrl", githubAuthLoginUrl);
            return render(map, "login");
        });

        get("/logout", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            requestUserManager.clearSignedInUser(req);
            res.redirect("/");
            return null;
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
     * Replaces null user with a generated guest user for display.
     *
     * @param user
     * @return existing user or generated guest user
     */
    private User replaceNullUserWithGuest(User user) {
        if (user == null) {
            return new User("undefined", "undefined", "guest", 0);
        }

        return user;
    }

    /**
     * Toggles a bookmark's read status for a user specified in request.
     *
     * @param req
     */
    private void toggleBookmarkReadStatus(Request req) {
        String bookmarkId = req.queryParams("bookmarkId");
        User user = requestUserManager.getSignedInUser(req);

        if (bookmarkId != null) {
            if (user.getBookmarkReadStatus(bookmarkId) == null) {
                users.markBookmarkAsRead(user.getId(), bookmarkId);
            } else {
                users.unmarkBookmarkAsRead(user.getId(), bookmarkId);
            }
        }
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
     * Sets the login status based on whether or not the user has signed in.
     */
    private void setUserStatusToMap(Request req, Map map) {
        User user = requestUserManager.getSignedInUser(req);
        if (user != null) {

            map.put("userStatusText", "You are logged in as: " + user.getUsername());
            map.put("userLogInText", "Log out");
            map.put("userLogInLink", "/logout");
        } else {

            map.put("userLogInText", "Log in");
            map.put("userLogInLink", "/login");
            map.put("userStatusText", "You are logged in as a guest.");
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
