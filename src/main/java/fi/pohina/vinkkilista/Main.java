package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.domain.BookmarkService;

public class Main {
    public static void main(String[] args) {
        BookmarkDao bookmarkDao = new InMemoryBookmarkDao();

        BookmarkService service = new BookmarkService(bookmarkDao);
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
            "GitHub"
        );
        bookmarks.createBookmark(
            "Domain Driven Design Weekly",
            "http://dddweekly.com",
            null
        );
        bookmarks.createBookmark(
            "the morning paper",
            "https://blog.acolyer.org",
            "Adrian Colyer"
        );
        bookmarks.createBookmark(
            "An Industrial-Strength Audio Search Algorithm",
            "https://www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf",
            "Avery Li-Chun Wang"
        );
    }
}
