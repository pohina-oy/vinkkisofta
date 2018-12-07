package fi.pohina.vinkkilista;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.pohina.vinkkilista.api.GithubEmail;
import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import spark.*;
import org.apache.http.client.utils.URLEncodedUtils;
import spark.ModelAndView;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.lang.reflect.Type;
import java.util.*;

import static spark.Spark.*;

public class App {

    private static final String SESSION_ATTRIBUTE_USERID = "github-user";
    private static final String REQ_ATTRIBUTE_USER = "user";

    private final CommaSeparatedTagsParser tagParser
            = new CommaSeparatedTagsParser();

    private final BookmarkService bookmarkService;
    private final AppConfig config;
    private final UserService users;

    public App(BookmarkService bookmarkService, UserService users, AppConfig config) {
        this.bookmarkService = bookmarkService;
        this.config = config;
        this.users = users;
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

            String accessToken = postAuthCallbackCode(callbackCode);

            // 2. get user info
            String userInfoJson = getUserInfoJson(accessToken);

            GithubUser githubUser = new Gson().fromJson(userInfoJson, GithubUser.class);

            // 3. get user emails because if user has multiple emails, we need to find the primary one
            if (githubUser.getEmail() == null) {
                githubUser.setEmail(getPrimaryUserEmail(accessToken));
            }

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

    private String postAuthCallbackCode(String code) throws Exception {
        String url = "https://github.com/login/oauth/access_token";

        // 1. fetch access token to Github API with code
        List<NameValuePair> form = Form.form()
                .add("code", code)
                .add("client_id", config.getGithubClientId())
                .add("client_secret", config.getGithubClientSecret())
                .build();

        HttpEntity responseEntity
                = org.apache.http.client.fluent.Request.Post(url)
                        .bodyForm(form)
                        .execute()
                        .returnResponse()
                        .getEntity();

        // 1.1 print access token response keys
        List<NameValuePair> responseQuery = URLEncodedUtils.parse(responseEntity);
        //responseQuery.forEach(nvp -> System.out.println("    * " + nvp.getName() + " - " + nvp.getValue()));

        String accessToken = responseQuery
                .stream()
                .filter(nvp -> nvp.getName().equals("access_token"))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElse(null);

        return accessToken;
    }

    private String getUserInfoJson(String accessToken) throws Exception {
        String userInfoJson
                = org.apache.http.client.fluent.Request.Get("https://api.github.com/user")
                        .addHeader("Authorization", String.format("Bearer %s", accessToken))
                        .addHeader("Accept", "application/json")
                        .execute()
                        .returnContent()
                        .asString();

        return userInfoJson;
    }

    private String getPrimaryUserEmail(String accessToken) throws Exception {
        String userEmailsJson
                = org.apache.http.client.fluent.Request.Get("https://api.github.com/user/emails")
                        .addHeader("Authorization", String.format("Bearer %s", accessToken))
                        .addHeader("Accept", "application/json")
                        .execute()
                        .returnContent()
                        .asString();

        Type emailListType = new TypeToken<List<GithubEmail>>() {
        }.getType();

        List<GithubEmail> userEmails = new Gson().fromJson(userEmailsJson, emailListType);

        GithubEmail primaryEmail = userEmails.stream()
                .filter(email -> email.isPrimary())
                .findFirst()
                .orElse(userEmails.get(0));

        return primaryEmail.getEmail();
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
