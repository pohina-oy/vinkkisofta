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
    private final AppConfig config;
    private final UserService users;
    private final RequestUserManager requestUserManager;

    public App(BookmarkService bookmarkService, TagService tagService, UserService userService, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.tagService = tagService;
        this.config = config;
        this.users = userService;
        this.githubOAuthApi = new GithubOAuthApi(
            config.getGithubClientId(),
            config.getGithubClientSecret()
        );
        this.requestUserManager = new RequestUserManager(userService);
    }

    /**
     * Configures and ignites the Spark server, binding to the specified port.
     *
     * @param portNumber The port to which the HTTP server will be bound to.
     */
    public void startServer(int portNumber) {
        staticFileLocation("/static");
        port(portNumber);

        if (config.isProduction()) {
            before("/", this::authenticationFilter);
            before("/bookmarks/*", this::authenticationFilter);
        }
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

            post("/:id/toggleRead", (req, res) -> {
                String bookmarkId = req.params("id");

                if (requestUserManager.getSignedInUser(req).getBookmarkReadStatus(bookmarkId) == null) {
                    users.markBookmarkAsRead(
                        requestUserManager.getSignedInUser(req).getId(),
                        bookmarkId
                    );
                } else {
                    users.unmarkBookmarkAsRead(
                        requestUserManager.getSignedInUser(req).getId(),
                        bookmarkId
                    );
                }

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

                Set<String> tags = tagService.toValidatedSet(req.queryParams("tags"));
                Collection<Bookmark> bookmarks = bookmarkService.getBookmarksByTags(tags);
                map.put("bookmarks", bookmarks);
                map.put("user", replaceNullUserWithGuest(requestUserManager.getSignedInUser(req)));

                return render(map, "search");
            });

            post("/search/:id/toggleRead", (req, res) -> {
                String bookmarkId = req.params("id");

                if (requestUserManager.getSignedInUser(req).getBookmarkReadStatus(bookmarkId) == null) {
                    users.markBookmarkAsRead(
                        requestUserManager.getSignedInUser(req).getId(),
                        bookmarkId
                    );
                } else {
                    users.unmarkBookmarkAsRead(
                        requestUserManager.getSignedInUser(req).getId(),
                        bookmarkId
                    );
                }

                res.redirect("/bookmarks/search");
                return null;
            });
        });

        get("/login", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("clientId", config.getGithubClientId());
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
            map.put("userLogInText", "log out");
            map.put("userLogInLink", "/logout");
        } else {

            map.put("userLogInText", "log in");
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
