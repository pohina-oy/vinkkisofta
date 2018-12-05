package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.UserDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import fi.pohina.vinkkilista.data_access.InMemoryUserDao;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.UserService;

import java.util.*;
import fi.pohina.vinkkilista.data_access.PostgresBookmarkDao;
import fi.pohina.vinkkilista.data_access.PostgresTagDao;
import fi.pohina.vinkkilista.data_access.PostgresUserDao;
import io.github.cdimascio.dotenv.Dotenv;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();
        String stage = dotenv.get("STAGE");

        BookmarkDao bookmarkDao = getBookmarkDao(dotenv);
        TagDao tagDao = getTagDao(dotenv);
        UserDao userDao = getUserDao(dotenv);

        BookmarkService bookmarkService = new BookmarkService(bookmarkDao, tagDao);
        UserService userService = new UserService(userDao);

        if (stage == null || !stage.equals("production")) {
            addMockBookmarks(bookmarkService);
        }

        int port = getPort(dotenv);
        AppConfig config = getConfig(dotenv);

        new App(bookmarkService, userService, config).startServer(port);
    }

    private static int getPort(Dotenv dotenv) {
        String port = dotenv.get("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }

    private static AppConfig getConfig(Dotenv dotenv) {
        return new AppConfig(
            dotenv.get("GITHUB_CLIENT_ID"),
            dotenv.get("GITHUB_CLIENT_SECRET")
        );
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
        return new PostgresBookmarkDao(dbHost, dbUser, dbPassword, db);
    }

    private static TagDao getTagDao(Dotenv dotenv) {
        String stage = dotenv.get("STAGE");
        if (stage == null || !stage.equals("production")) {
            return new InMemoryTagDao();
        }
        String dbHost = dotenv.get("DB_HOST");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String db = dotenv.get("DB_DATABASE");
        return new PostgresTagDao(dbHost, dbUser, dbPassword, db);
    }

    private static UserDao getUserDao(Dotenv dotenv) {
        String stage = dotenv.get("STAGE");
        if (stage == null || !stage.equals("production")) {
            return new InMemoryUserDao();
        }
        String dbHost = dotenv.get("DB_HOST");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        String db = dotenv.get("DB_DATABASE");
        return new PostgresUserDao(dbHost, dbUser, dbPassword, db);
    }
}
