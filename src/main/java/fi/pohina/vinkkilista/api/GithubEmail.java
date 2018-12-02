package fi.pohina.vinkkilista.api;

public class GithubEmail {

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
}
