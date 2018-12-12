package fi.pohina.vinkkilista.data_access;

import javax.sql.DataSource;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class DaoFactoryTest {

    private DataSource dataSource;
    private DaoFactory factory;

    @Before
    public void setUp() {
        dataSource = mock(DataSource.class);
    }

    @Test
    public void createBookmarkDaoCreatesInMemoryVariantsWhenNotInProduction() {
        factory = new DaoFactory(false, null);

        BookmarkDao bookmarkDao = factory.createBookmarkDao();
        UserDao userDao = factory.createUserDao();
        TagDao tagDao = factory.createTagDao();

        assertTrue(bookmarkDao instanceof InMemoryBookmarkDao);
        assertTrue(userDao instanceof InMemoryUserDao);
        assertTrue(tagDao instanceof InMemoryTagDao);
    }

    @Test
    public void createBookmarkDaoCreatesPostgresVariantsWhenInProduction() {
        factory = new DaoFactory(true, dataSource);

        BookmarkDao bookmarkDao = factory.createBookmarkDao();
        UserDao userDao = factory.createUserDao();
        TagDao tagDao = factory.createTagDao();

        assertTrue(bookmarkDao instanceof PostgresBookmarkDao);
        assertTrue(userDao instanceof PostgresUserDao);
        assertTrue(tagDao instanceof PostgresTagDao);
    }
}
