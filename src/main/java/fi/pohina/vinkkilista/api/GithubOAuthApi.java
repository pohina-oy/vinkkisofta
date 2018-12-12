package fi.pohina.vinkkilista.api;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * Provides access to the Github OAuth HTTP API.
 */
public class GithubOAuthApi {

    private static final String ACCESS_TOKEN_URL
        = "https://github.com/login/oauth/access_token";
    private static final String ACCESS_TOKEN_RESPONSE_KEY = "access_token";

    private final String githubClientId;
    private final String githubClientSecret;

    /**
     * Initializes a new instance of the {@link GithubOAuthApi} class with the
     * specified Github API client ID and secret.
     */
    public GithubOAuthApi(String clientId, String clientSecret) {
        this.githubClientId = clientId;
        this.githubClientSecret = clientSecret;
    }

    /**
     * Exchanges the code received from Github in the application authentication
     * callback with the Github OAuth API to receive an access token for the
     * user.
     *
     * @param authCallbackCode the code received in the authenticetion callback.
     * @return the OAuth access token for the user, or <c>null</c> if the
     * exchange failed.
     * @throws IOException if a network operation failed.
     */
    public String exchangeCodeForAccessToken(
        String authCallbackCode
    ) throws IOException {
        List<NameValuePair> requestData
            = createAccessTokenFetchData(authCallbackCode);
        HttpResponse response = postAccessTokenEndpoint(requestData);

        List<NameValuePair> responseData = getResponseData(response);
        return findAccessTokenFromResponseData(responseData);
    }

    private List<NameValuePair> createAccessTokenFetchData(
        String authCallbackCode
    ) {
        return Form.form()
            .add("code", authCallbackCode)
            .add("client_id", githubClientId)
            .add("client_secret", githubClientSecret)
            .build();
    }

    private static HttpResponse postAccessTokenEndpoint(
        List<NameValuePair> postData
    ) throws IOException {
        return Request.Post(ACCESS_TOKEN_URL)
            .bodyForm(postData)
            .execute()
            .returnResponse();
    }

    private static String findAccessTokenFromResponseData(
        List<NameValuePair> responseData
    ) {
        Optional<NameValuePair> accessTokenProperty =
            findAccessTokenProperty(responseData);

        return accessTokenProperty
            .map(GithubOAuthApi::getAccessTokenValue)
            .orElse(null);
    }

    private static Optional<NameValuePair> findAccessTokenProperty(
        List<NameValuePair> responseData
    ) {
        return responseData
            .stream()
            .filter(GithubOAuthApi::isAccessTokenProperty)
            .findFirst();
    }

    private static boolean isAccessTokenProperty(NameValuePair property) {
        return ACCESS_TOKEN_RESPONSE_KEY.equals(property.getName());
    }

    private static String getAccessTokenValue(NameValuePair property) {
        return property.getValue();
    }

    private static List<NameValuePair> getResponseData(
        HttpResponse response
    ) throws IOException {
        return URLEncodedUtils.parse(response.getEntity());
    }
}
