package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import fi.pohina.vinkkilista.domain.BookmarkService;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        BookmarkDao bookmarkDao = new InMemoryBookmarkDao();
        TagDao tagDao = new InMemoryTagDao();

        BookmarkService service = new BookmarkService(bookmarkDao, tagDao);
        addMockBookmarks(service);

        int port = getPort();

        new App(service).startServer(port);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }

    private static void addMockBookmarks(BookmarkService bookmarks) {
        bookmarks.createBookmark(
            "GitHub Blog",
            "https://blog.github.com",
            "GitHub",
            new HashSet<>(Arrays.asList("blog"))
        );
        bookmarks.createBookmark(
            "Domain Driven Design Weekly",
            "http://dddweekly.com",
            null
        );
        bookmarks.createBookmark(
            "the morning paper",
            "https://blog.acolyer.org",
            "Adrian Colyer",
            new HashSet<>(Arrays.asList("blog"))
        );
        bookmarks.createBookmark(
            "An Industrial-Strength Audio Search Algorithm",
            "https://www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf",
            "Avery Li-Chun Wang",
            new HashSet<>(Arrays.asList("scientific publication"))
        );
    }
}
