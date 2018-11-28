package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import fi.pohina.vinkkilista.domain.BookmarkService;
import java.util.*;
import fi.pohina.vinkkilista.data_access.PostgreBookmarkDao;
import fi.pohina.vinkkilista.domain.BookmarkService;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();
        String stage = dotenv.get("STAGE");

        BookmarkDao bookmarkDao = getBookmarkDao(dotenv);
        TagDao tagDao = new InMemoryTagDao();

        BookmarkService service = new BookmarkService(bookmarkDao, tagDao);

        if (stage != null && !stage.equals("production")) {
            addMockBookmarks(service);
        }

        int port = getPort(dotenv);

        new App(service).startServer(port);
    }

    private static int getPort(Dotenv dotenv) {
        String port = dotenv.get("PORT");
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

    private static BookmarkDao getBookmarkDao(Dotenv dotenv) {
        String stage = dotenv.get("STAGE");
        if (stage == null || !stage.equals("production")) {
            return new InMemoryBookmarkDao();
        }
        String dbHost = dotenv.get("DB_HOST");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String db = dotenv.get("DB_DATABASE");
        return new PostgreBookmarkDao(dbHost, dbUser, dbPassword, db); 
    }
}
