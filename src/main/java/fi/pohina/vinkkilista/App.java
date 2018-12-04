package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.api.*;

import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.client.fluent.Form;

import fi.pohina.vinkkilista.domain.User;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import com.google.common.base.Strings;
import static spark.Spark.*;

public class App {

    private static final String GITHUB_CLIENT_ID = "censored";
    private static final String GITHUB_CLIENT_SECRET = "censored";

    private final CommaSeparatedTagsParser tagParser
        = new CommaSeparatedTagsParser();

    private final BookmarkService bookmarks;
    private final AppConfig config;

    public App(BookmarkService bookmarks, AppConfig config) {
        this.bookmarks = bookmarks;
        this.config = config;
    }

    /**
     * Configures and ignites the Spark server, binding to the specified port.
     *
     * @param portNumber The port to which the HTTP server will be bound to.
     */
    public void startServer(int portNumber) {
        staticFileLocation("/static");
        port(portNumber);

        get("/", (req, res) -> {
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
                res.redirect("/");
                return "New bookmark added";
            } else {
                res.redirect("/new");
                return "Bookmark could not be created";
            }
        });

        post("/search", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Collection<Bookmark> bookmarks = searchBookmarks(req.queryMap());
            map.put("bookmarks", bookmarks);
            return render(map, "search");
        });

        get("/login", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("clientId", config.getGithubClientId());
            return render(map, "login");
        });

        get("/auth/gh-callback", (req, res) -> {
            System.out.println("App::githubCallback");

            String callbackCode = req.queryParams("code");
            String url = "https://github.com/login/oauth/access_token";

            // 1. fetch access token to Github API with code

            List<NameValuePair> form = Form.form()
                    .add("code", callbackCode)
                    .add("client_id", GITHUB_CLIENT_ID)
                    .add("client_secret", GITHUB_CLIENT_SECRET)
                    .build();

            HttpEntity responseEntity =
                    org.apache.http.client.fluent.Request.Post(url)
                            .bodyForm(form)
                            .execute()
                            .returnResponse()
                            .getEntity();

            // 1.1 print access token response keys

            System.out.println("  - POST /login/oauth/access_token");
            List<NameValuePair> responseQuery = URLEncodedUtils.parse(responseEntity);
            responseQuery.forEach(nvp -> System.out.println("    * " + nvp.getName() + " - " + nvp.getValue()));

            String accessToken = responseQuery
                    .stream()
                    .filter(nvp -> nvp.getName().equals("access_token"))
                    .findFirst()
                    .map(NameValuePair::getValue)
                    .orElse(null);

            // 2. get user info
            String userInfoJson =
                    org.apache.http.client.fluent.Request.Get("https://api.github.com/user")
                            .addHeader("Authorization", String.format("Bearer %s", accessToken))
                            .addHeader("Accept", "application/json")
                            .execute()
                            .returnContent()
                            .asString();

            System.out.println("  - GET /user\n     * " + userInfoJson);

            GithubUser githubUser = new Gson().fromJson(userInfoJson, GithubUser.class);

            if (githubUser.getEmail() == null) {
                System.out.println("  - Email was null, fetching all emails");
                // 3. get user emails because if user has multiple emails, we need to find the primary one
                String userEmailsJson =
                        org.apache.http.client.fluent.Request.Get("https://api.github.com/user/emails")
                            .addHeader("Authorization", String.format("Bearer %s", accessToken))
                            .addHeader("Accept", "application/json")
                            .execute()
                            .returnContent()
                            .asString();

                System.out.println("  - GET /user/emails\n     * " + userEmailsJson);

                Type emailListType = new TypeToken<List<GithubEmail>>() {
                }.getType();

                List<GithubEmail> userEmails = new Gson().fromJson(userEmailsJson, emailListType);

                GithubEmail primaryEmail = userEmails.stream()
                            .filter(email -> email.primary)
                            .findFirst()
                            .orElse(userEmails.get(0));

                System.out.println("  - Identified primary email as: " + primaryEmail.getEmail());
                githubUser.setEmail(primaryEmail.getEmail());
            }

            System.out.println("  - Finding or creating user by Github user");
            User user = users.findOrCreateByGithubUser(githubUser);

            System.out.println("  - user: " + user);
            req.session(true)
                    .attribute(SESSION_ATTRIBUTE_USERID, user.getId());

            System.out.println("  - Finish, redirect to /bookmarks/");
            res.redirect("/bookmarks/");
            return "";
        });
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
