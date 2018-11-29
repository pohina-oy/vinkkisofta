package fi.pohina.vinkkilista.domain;

import java.util.Objects;

public class User {

    private String id;
    private String email;
    private String username;
    private int githubId;

    public User(String id, String email, String username, int githubId) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.githubId = githubId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getGithubId() {
        return githubId;
    }

    public void setGithubId(int githubId) {
        this.githubId = githubId;
    }

    @Override
    public String toString() {
        return "User{" +
            "id='" + id + '\'' +
            ", email='" + email + '\'' +
            ", username='" + username + '\'' +
            ", githubId=" + githubId +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return githubId == user.githubId &&
            Objects.equals(id, user.id) &&
            Objects.equals(email, user.email) &&
            Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, username, githubId);
    }
}
