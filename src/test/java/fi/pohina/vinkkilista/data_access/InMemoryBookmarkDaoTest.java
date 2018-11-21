package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Blog;
import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class InMemoryBookmarkDaoTest {

    private InMemoryBookmarkDao bookmarkDao;

    @Before
    public void setUp() {
        bookmarkDao = new InMemoryBookmarkDao();
        Blog eka = new Blog("eka", "https://www.abc.fi");
        Blog toka = new Blog("toka", "https://www.qwerty.com", "tekija");
        bookmarkDao.add(eka);
        bookmarkDao.add(toka);
    }

    /**
     * Tests that an existing bookmark can be found by title
     */
    @Test
    public void canFindExistingBookmarkByTitle() {
        assertEquals("eka", bookmarkDao.findByTitle("eka").getTitle());
    }

    /**
     * Tests that searcing a non-existent bookmark by title returns null
     */
    @Test
    public void findingNonExistentBookmarkByTitleReturnsNull() {
        assertEquals(null, bookmarkDao.findByTitle("eiole"));
    }

    /**
     * Tests that finding all bookmarks returns all saved bookmarks
     */
    @Test
    public void findingAllReturnsSavedBookmarks() {
        ArrayList<Bookmark> result = bookmarkDao.findAll();

        assertTrue(result.contains(bookmarkDao.findByTitle("eka")));
        assertTrue(result.contains(bookmarkDao.findByTitle("toka")));
    }

    /**
     * Tests that newly added bookmark is saved in the bookmarks
     */
    @Test
    public void addingNewBookmarkWorks() {
        Blog newEntry = new Blog("uusi", "http://www.somesite.org");
        bookmarkDao.add(newEntry);

        assertEquals("uusi", bookmarkDao.findByTitle("uusi").getTitle());
        assertTrue(bookmarkDao.findAll().contains(newEntry));
    }

    /**
     * Tests that finding all returns up-to-date list of bookmarks after adding
     * new entries
     */
    @Test
    public void findAllKeepsUpWithAdd() {
        Blog newEntry = new Blog("uusi", "http://www.somesite.org");
        bookmarkDao.add(newEntry);
        ArrayList<Bookmark> bookmarks = bookmarkDao.findAll();

        assertTrue(bookmarks.contains(bookmarkDao.findByTitle("eka")));
        assertTrue(bookmarks.contains(bookmarkDao.findByTitle("toka")));
        assertTrue(bookmarks.contains(newEntry));
    }
}
