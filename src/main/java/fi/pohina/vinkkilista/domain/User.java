package fi.pohina.vinkkilista.domain;

import java.time.LocalDateTime;
import java.util.HashMap;

public class User {

    private final String id;
    private final String email;
    private final String username;
    private final int githubId;
    private final HashMap<String, LocalDateTime> bookmarkReadDates;

    public User(String id, String email, String username, int githubId) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.githubId = githubId;
        bookmarkReadDates = new HashMap<>();
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

    public LocalDateTime getBookmarkReadStatus(String bookmarkId) {
        return bookmarkReadDates.get(bookmarkId);
    }

    public void setBookmarkReadStatus(String bookmarkId, LocalDateTime dateRead) {
        bookmarkReadDates.put(bookmarkId, dateRead);
    }

    public void removeBookmarkReadStatus(String bookmarkId) {
        bookmarkReadDates.remove(bookmarkId);
    }
}