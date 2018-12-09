package fi.pohina.vinkkilista;

public class AppConfig {

    private final String githubClientId;
    private final String githubClientSecret;
    private final String stage;

    public AppConfig(
        String githubClientId,
        String githubClientSecret,
        String stage
    ) {
        this.githubClientId = githubClientId;
        this.githubClientSecret = githubClientSecret;
        this.stage = stage;
    }

    public String getGithubClientId() {
        return githubClientId;
    }

    public String getGithubClientSecret() {
        return githubClientSecret;
    }

    public boolean isProduction() {
        return "production".equals(stage);
    }

    // public String getStage() {
    //    return stage;
    //}
}
