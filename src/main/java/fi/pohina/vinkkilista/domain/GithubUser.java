package fi.pohina.vinkkilista.domain;

public class GithubUser {

    private String login;
    private int id;
    private String email; // email is null if user has multiple emails
    // Other fields omitted, they are not relevant for us

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "GithubUser{" +
            "login='" + login + '\'' +
            ", id=" + id +
            ", email='" + email + '\'' +
            '}';
    }
}