package fi.pohina.vinkkilista.domain;

import java.util.*;

public class Bookmark {

    private final String id;
    private final String title;
    private final String url;
    private final String author;
    private final Set<Tag> tags;

    public Bookmark(String id, String title, String url) {
        this(id, title, url, null);
    }

    public Bookmark(String id, String title, String url, String author) {
        this(id, title, url, author, new HashSet<>());
    }

    public Bookmark(
        String id,
        String title,
        String url,
        String author,
        Set<Tag> tags
    ) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.author = author;
        this.tags = tags;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl() {
        return url;
    }

    public Set<Tag> getTags() {
        return tags;
    }
}
