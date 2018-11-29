package fi.pohina.vinkkilista.domain;

public class User {

    private final String id;
    private final String email;
    private final String username;
    private final int githubId;

    public User(String id, String email, String username, int githubId) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.githubId = githubId;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public int getGithubId() {
        return githubId;
    }
}