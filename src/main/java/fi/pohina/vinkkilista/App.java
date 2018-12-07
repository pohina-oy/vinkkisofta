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

    private static final String SESSION_ATTRIBUTE_USERID = "github-user";
    private static final String REQ_ATTRIBUTE_USER = "user";

    private final CommaSeparatedTagsParser tagParser
            = new CommaSeparatedTagsParser();

    private final GithubOAuthApi githubOAuthApi;
    private final BookmarkService bookmarkService;
    private final AppConfig config;
    private final UserService users;

    public App(BookmarkService bookmarkService, UserService users, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.config = config;
        this.users = users;
        this.githubOAuthApi = new GithubOAuthApi(
            config.getGithubClientId(),
            config.getGithubClientSecret()
        );
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

                User user = req.attribute(REQ_ATTRIBUTE_USER);
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
                User creator = users.findById(getUserIdFromSession(req.session()));
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

            req.session(true)
                    .attribute(SESSION_ATTRIBUTE_USERID, user.getId());

            res.redirect("/bookmarks/");
            return "";
        });
    }

    private void authenticationFilter(Request req, Response res) {
        String userId = getUserIdFromSession(req.session());
        User user = users.findById(userId);

        if (user == null) {
            res.redirect("/login");
            halt();
            return;
        }

        req.attribute(REQ_ATTRIBUTE_USER, user);
    }

    private String getUserIdFromSession(Session session) {
        return session.attribute(SESSION_ATTRIBUTE_USERID);
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
