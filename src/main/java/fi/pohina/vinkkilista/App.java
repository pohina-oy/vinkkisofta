package fi.pohina.vinkkilista;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.pohina.vinkkilista.api.GithubEmail;
import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.data_access.InMemoryUserDao;
import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import io.github.cdimascio.dotenv.Dotenv;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import spark.*;
import org.apache.http.client.utils.URLEncodedUtils;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.template.thymeleaf.ThymeleafTemplateEngine;

import java.lang.reflect.Type;
import java.util.*;

import static spark.Spark.*;

public class App {

    private static String stage = "";

    private static final String SESSION_ATTRIBUTE_USERID = "github-user";
    private static final String REQ_ATTRIBUTE_USER = "user";

    private static String githubClientID = "censored";
    private static String githubClientSecret = "censored";

    private final CommaSeparatedTagsParser tagParser
        = new CommaSeparatedTagsParser();

    private final BookmarkService bookmarks;
    private final AppConfig config;
    private final UserService users;

    public App(BookmarkService bookmarks, AppConfig config) {
        this.bookmarks = bookmarks;
        this.config = config;
        this.users = new UserService(new InMemoryUserDao());

        Dotenv dotenv = Dotenv
                .configure()
                .ignoreIfMissing()
                .load();


        githubClientID = dotenv.get("GITHUB_CLIENT_ID");
        githubClientSecret = dotenv.get("GITHUB_CLIENT_SECRET");
        stage = dotenv.get("STAGE");
    }

    /**
     * Configures and ignites the Spark server, binding to the specified port.
     *
     * @param portNumber The port to which the HTTP server will be bound to.
     */
    public void startServer(int portNumber) {
        staticFileLocation("/static");
        port(portNumber);

        System.out.println("\nStage: " + stage);
        if ("production".equals(stage)) {
            before("/", this::authenticationFilter);
            before("/bookmarks/*", this::authenticationFilter);
       }
        redirect.any("/", "/bookmarks/");

        path("/bookmarks", () -> {
            get("/", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                Collection<Bookmark> bookmarks = this.bookmarks.getAllBookmarks();
                map.put("bookmarks", bookmarks);

                User user = req.attribute(REQ_ATTRIBUTE_USER);
                System.out.println("App::bookmarkIndexHandler\n  user:" + user);
                if (user != null) {
                    map.put("user", user);
                } else {
                    map.put("user", new User("undefined", "undefined", "guest", 0));
                }

                return render(map, "index");
            });

            //Turha
            get("/bookmarks", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                Collection<Bookmark> bookmarks = this.bookmarks.getAllBookmarks();
                map.put("bookmarks", bookmarks);
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
                if (validateAndCreateBookmark(req.queryMap())) {
                    res.redirect("/bookmarks/");
                    return "New bookmark added";
                } else {
                    res.redirect("new");
                    return "Bookmark could not be created";
                }
            });

            post("/search", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                Collection<Bookmark> bookmarks = searchBookmarks(req.queryMap());
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

        System.out.println("App::authenticationFilter\n  user: " + user);

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
                .add("client_id", githubClientID)
                .add("client_secret", githubClientSecret)
                .build();

        HttpEntity responseEntity =
                org.apache.http.client.fluent.Request.Post(url)
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
        String userInfoJson =
                org.apache.http.client.fluent.Request.Get("https://api.github.com/user")
                        .addHeader("Authorization", String.format("Bearer %s", accessToken))
                        .addHeader("Accept", "application/json")
                        .execute()
                        .returnContent()
                        .asString();

        return userInfoJson;
    }

    private String getPrimaryUserEmail(String accessToken) throws Exception {
        String userEmailsJson =
                org.apache.http.client.fluent.Request.Get("https://api.github.com/user/emails")
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
     * Validates parameters and creates a bookmark.
     * Todo: Make a validator class.
     *
     * @param params the query parameters of the request.
     * @return <c>true</c> if the bookmark is successfully created,
     * otherwise <c>false</c>.
     */
    private boolean validateAndCreateBookmark(QueryParamsMap params) {
        String title = params.get("title").value();
        String url = params.get("url").value();
        String author = params.get("author").value();
        String commaSeparatedTags = params.get("tags").value();

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(url)) {
            return false;
        }

        Set<String> tags = tagParser.parse(commaSeparatedTags);

        bookmarks.createBookmark(
            title,
            url,
            author,
            tags
        );

        return true;
    }

    /**
     * Searches for bookmarks based on filter forms, e.g. tags.
     *
     * @param params the query parameters of the request.
     * @return list of bookmarks that match the query.
     */
    private Collection<Bookmark> searchBookmarks(QueryParamsMap params) {
        String commaSeparatedTags = params.get("tags").value();

        Set<String> tags = tagParser.parse(commaSeparatedTags);

        return bookmarks.getBookmarksByTags(tags);
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
