package fi.pohina.vinkkilista.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import org.apache.http.client.fluent.Request;

/**
 * Provides access to the Github JSON API.
 */
public class GithubJsonApi {

    private static final String API_URL_BASE = "https://api.github.com";

    private static final Type EMAIL_LIST_TYPE
        = new TypeToken<List<GithubEmail>>() {
    }.getType();

    private final Gson gson = new Gson();
    private final String accessToken;

    /**
     * Initializes a new instance of the {@link GithubJsonApi} class with the
     * specified OAuth access token. The token specifies which user's data
     * is accessed.
     *
     * @param userAccessToken the user's OAuth access token.
     */
    public GithubJsonApi(String userAccessToken) {
        this.accessToken = userAccessToken;
    }

    /**
     * Fetches a {@link GithubUser} from the API. If the user has multiple
     * emails, the email is set to either (in order): the primary email, any
     * available email, or null.
     *
     * @return the user.
     * @throws IOException if a network operation fails.
     */
    public GithubUser getGithubUser() throws IOException {
        GithubUser user = fetchUser();

        if (userHasOnlyOneEmail(user)) {
            return user;
        }

        String primaryEmail = fetchUsersPrimaryEmail();
        user.setEmail(primaryEmail);
        return user;
    }

    private GithubUser fetchUser() throws IOException {
        String url = constructApiUrl("/user");
        String json = getJson(url);
        return gson.fromJson(json, GithubUser.class);
    }

    private String fetchUsersPrimaryEmail() throws IOException {
        List<GithubEmail> emails = fetchUserEmails();
        return findPrimaryEmail(emails)
            .orElseGet(() -> findAnyEmailOrNull(emails));
    }

    private List<GithubEmail> fetchUserEmails() throws IOException {
        String url = constructApiUrl("/user/emails");
        String json = getJson(url);
        return gson.fromJson(json, EMAIL_LIST_TYPE);
    }

    private Optional<String> findPrimaryEmail(List<GithubEmail> emails) {
        return emails
            .stream()
            .filter(GithubEmail::isPrimary)
            .findFirst()
            .map(GithubEmail::getEmail);
    }

    private String findAnyEmailOrNull(List<GithubEmail> emails) {
        return emails
            .stream()
            .findAny()
            .map(GithubEmail::getEmail)
            .orElse(null);
    }

    private String getJson(String url) throws IOException {
        return Request.Get(url)
            .addHeader("Authorization", "Bearer " + accessToken)
            .addHeader("Accept", "application/json")
            .execute()
            .returnContent()
            .asString();
    }

    private static String constructApiUrl(String path) {
        return API_URL_BASE + path;
    }

    private static boolean userHasOnlyOneEmail(GithubUser user) {
        // the Github API returns null for the user's email field if the user
        // has multiple emails
        return user.getEmail() != null;
    }
}
