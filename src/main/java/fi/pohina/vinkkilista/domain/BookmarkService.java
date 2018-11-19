package fi.pohina.vinkkilista.domain;

import java.util.ArrayList;
import java.util.Collection;

public class BookmarkService {
    public Collection<Blog> getBlogs() {
        ArrayList<Blog> bookmarks = new ArrayList<>();
        bookmarks.add(new Blog(
            "GitHub Blog",
            "https://blog.github.com",
            "GitHub")
        );
        bookmarks.add(new Blog(
            "Domain Driven Design Weekly",
            "http://dddweekly.com"
        ));
        bookmarks.add(new Blog(
            "the morning paper",
            "https://blog.acolyer.org",
            "Adrian Colyer"
        ));
        return bookmarks;
    }
}
