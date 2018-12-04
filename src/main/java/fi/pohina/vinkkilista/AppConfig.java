package fi.pohina.vinkkilista;

public class AppConfig {

    private final String githubClientId;
    private final String githubClientSecret;

    public AppConfig(String githubClientId, String githubClientSecret) {
        this.githubClientId = githubClientId;
        this.githubClientSecret = githubClientSecret;
    }

    public String getGithubClientId() {
        return githubClientId;
    }

    public String getGithubClientSecret() {
        return githubClientSecret;
    }
}
