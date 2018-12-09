package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.*;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.UserService;

import java.util.*;
import io.github.cdimascio.dotenv.Dotenv;
import javax.sql.DataSource;
import org.postgresql.ds.PGPoolingDataSource;
import com.google.common.base.Strings;

public class Main {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv
            .configure()
            .ignoreIfMissing()
            .load();

        DaoFactory daoFactory = new DaoFactory(
            isProduction(dotenv),
            createPostgresDataSource(dotenv)
        );

        BookmarkDao bookmarkDao = daoFactory.createBookmarkDao();
        TagDao tagDao = daoFactory.createTagDao();
        UserDao userDao = daoFactory.createUserDao();

        BookmarkService bookmarkService = new BookmarkService(
            bookmarkDao,
            tagDao
        );
        UserService userService = new UserService(userDao);

        if (!isProduction(dotenv)) {
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
            dotenv.get("GITHUB_CLIENT_SECRET"),
            dotenv.get("STAGE")
        );
    }

    private static void addMockBookmarks(BookmarkService bookmarks) {
        bookmarks.createBookmark(
            "GitHub Blog",
            "https://blog.github.com",
            "GitHub",
            null,
            new HashSet<>(Arrays.asList("blog"))
        );
        bookmarks.createBookmark(
            "Domain Driven Design Weekly",
            "http://dddweekly.com",
            null,
            null,
            null
        );
        bookmarks.createBookmark(
            "the morning paper",
            "https://blog.acolyer.org",
            "Adrian Colyer",
            null,
            new HashSet<>(Arrays.asList("blog"))
        );
        bookmarks.createBookmark(
            "An Industrial-Strength Audio Search Algorithm",
            "https://www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf",
            "Avery Li-Chun Wang",
            null,
            new HashSet<>(Arrays.asList("scientific publication"))
        );
    }

    private static boolean isProduction(Dotenv dotenv) {
        String stage = dotenv.get("STAGE");
        return stage != null && stage.equals("production");
    }

    private static DataSource createPostgresDataSource(Dotenv dotenv) {
        PGPoolingDataSource ds = new PGPoolingDataSource();
        ds.setServerName(dotenv.get("DB_HOST"));
        ds.setDatabaseName(dotenv.get("DB_DATABASE"));
        ds.setUser(dotenv.get("DB_USER"));
        ds.setPassword(dotenv.get("DB_PASSWORD"));

        String portStr = dotenv.get("DB_PORT");
        int port = Strings.isNullOrEmpty(portStr) ? 0 : Integer.parseInt(portStr);
        ds.setPortNumber(port);
        // for pooled connections: allow up to three simultaneous connections
        ds.setMaxConnections(3);
        return ds;
    }
}
