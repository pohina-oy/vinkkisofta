package fi.pohina.vinkkilista.data_access;

public class DaoFactory {

    private final ConnectionProvider connectionProvider;
    private final boolean isProduction;

    public DaoFactory(
        boolean isProduction,
        ConnectionProvider connectionProvider
    ) {
        this.connectionProvider = connectionProvider;
        this.isProduction = isProduction;
    }

    public BookmarkDao createBookmarkDao() {
        return isProduction
            ? new PostgresBookmarkDao(connectionProvider)
            : new InMemoryBookmarkDao();
    }

    public TagDao createTagDao() {
        return isProduction
            ? new PostgresTagDao(connectionProvider)
            : new InMemoryTagDao();
    }

    public UserDao createUserDao() {
        return isProduction
            ? new PostgresUserDao(connectionProvider)
            : new InMemoryUserDao();
    }
}
