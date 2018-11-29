package fi.pohina.vinkkilista;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fi.pohina.vinkkilista.domain.*;
import java.lang.reflect.Type;
import java.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.utils.URLEncodedUtils;
import spark.*;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import com.google.common.base.Strings;
import static spark.Spark.*;

public class App {

    private static final String SESSION_ATTRIBUTE_USERID = "github-user";
    private static final String REQ_ATTRIBUTE_USER = "user";

    private static final String GITHUB_CLIENT_ID = "censored";
    private static final String GITHUB_CLIENT_SECRET = "censored";

    private final CommaSeparatedTagsParser tagParser
        = new CommaSeparatedTagsParser();

    private final BookmarkService bookmarks;
    private final UserService users;

    public App(BookmarkService bookmarks) {
        this.bookmarks = bookmarks;
        this.users = new UserService();
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
                Collection<Bookmark> bookmarks = this.bookmarks.getAllBookmarks();
                map.put("bookmarks", bookmarks);

                User user = req.attribute(REQ_ATTRIBUTE_USER);
                System.out.println("App::bookmarkIndexHandler\n  user:" + user);
                map.put("user", user);

                return render(map, "index");
            });

            get("/new", (req, res) -> {
                Map<String, Object> map = new HashMap<>();
                return render(map, "new");
            });

            post("/new", (req, res) -> {
                if (validateAndCreateBookmark(req.queryMap())) {
                    res.redirect("/bookmarks");
                    return "New bookmark added";
                } else {
                    res.redirect("/bookmarks/new");
                    return "Bookmark could not be created";
                }
            });

            redirect.any("*", "/bookmarks/");
        });

        get("/login", (req, res) -> {
            Map<String, Object> viewModel = new HashMap<>();
            viewModel.put("clientId", GITHUB_CLIENT_ID);
            return render(viewModel, "login");
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
                .get()
                .getValue();

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

                Type emailListType = new TypeToken<List<GithubUserEmail>>() {
                }.getType();

                List<GithubUserEmail> userEmails = new Gson().fromJson(userEmailsJson, emailListType);

                GithubUserEmail primaryEmail = userEmails.stream()
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

    class GithubUserEmail {

        private String email;
        private boolean primary;
        // omitted fields: boolean verified, Boolean visibility (vis can be null)


        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }

        @Override
        public String toString() {
            return "GithubUserEmail{email='" + email + "\', primary=" + primary + '}';
        }
    }
}
