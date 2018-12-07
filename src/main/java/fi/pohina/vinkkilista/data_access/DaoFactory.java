package fi.pohina.vinkkilista.data_access;

import javax.sql.DataSource;

public class DaoFactory {

    private final DataSource dataSource;
    private final boolean isProduction;

    public DaoFactory(boolean isProduction, DataSource dataSource) {
        this.dataSource = dataSource;
        this.isProduction = isProduction;
    }

    public BookmarkDao createBookmarkDao() {
        return isProduction
            ? new PostgresBookmarkDao(dataSource)
            : new InMemoryBookmarkDao();
    }

    public TagDao createTagDao() {
        return isProduction
            ? new PostgresTagDao(dataSource)
            : new InMemoryTagDao();
    }

    public UserDao createUserDao() {
        return isProduction
            ? new PostgresUserDao(dataSource)
            : new InMemoryUserDao();
    }
}
