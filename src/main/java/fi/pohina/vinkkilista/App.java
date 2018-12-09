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

    public App(BookmarkService bookmarkService, UserService users, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.config = config;
        this.users = users;
        this.githubOAuthApi = new GithubOAuthApi(
            config.getGithubClientId(),
            config.getGithubClientSecret()
        );
        this.requestUserManager = new RequestUserManager(users);
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
                Collection<Bookmark> bookmarks = this.bookmarkService.getAllBookmarks();
                map.put("bookmarks", bookmarks);

                User user = requestUserManager.getSignedInUser(req);
                if (user != null) {
                    map.put("user", user);
                } else {
                    user = new User("undefined", "undefined", "guest", 0);
                    map.put("user", user);
                }

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
            map.put("clientId", config.getGithubClientId());
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
