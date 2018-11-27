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
        eka.setID(1);
        toka.setID(2);
        bookmarkDao.add(eka);
        bookmarkDao.add(toka);
    }

    /**
     * Tests that an existing bookmark can be found by id
     */
    @Test
    public void canFindExistingBookmarkByID() {
        assertEquals(1, bookmarkDao.findByID(1).getID());
    }

    /**
     * Tests that searching a non-existent bookmark by id returns null
     */
    @Test
    public void findingNonExistentBookmarkByIDReturnsNull() {
        assertEquals(null, bookmarkDao.findByID(3));
    }

    /**
     * Tests that finding all bookmarks returns all saved bookmarks
     */
    @Test
    public void findingAllReturnsSavedBookmarks() {
        ArrayList<Bookmark> result = bookmarkDao.findAll();

        assertTrue(result.contains(bookmarkDao.findByID(1)));
        assertTrue(result.contains(bookmarkDao.findByID(2)));
    }

    /**
     * Tests that newly added bookmark is saved in the bookmarks
     */
    @Test
    public void addingNewBookmarkWorks() {
        Blog newEntry = new Blog("uusi", "http://www.somesite.org");
        newEntry.setID(5);
        bookmarkDao.add(newEntry);

        assertEquals(5, bookmarkDao.findByID(5).getID());
        assertTrue(bookmarkDao.findAll().contains(newEntry));
    }

    /**
     * Tests that finding all returns up-to-date list of bookmarks after adding
     * new entries
     */
    @Test
    public void findAllKeepsUpWithAdd() {
        Blog newEntry = new Blog("uusi", "http://www.somesite.org");
        newEntry.setID(5);
        bookmarkDao.add(newEntry);
        ArrayList<Bookmark> bookmarks = bookmarkDao.findAll();

        assertTrue(bookmarks.contains(bookmarkDao.findByID(1)));
        assertTrue(bookmarks.contains(bookmarkDao.findByID(2)));
        assertTrue(bookmarks.contains(newEntry));
    }
    
    /**
     * Tests that finding bookmarks by tags returns only matching bookmarks
     */
    @Test
    public void findByTagSetReturnsCorrectBookmarks() {
    }
    
    /**
     * Tests that finding bookmarks by tags returns an empty list if there
     * are no matching bookmarks
     */
    @Test
    public void findByTagSetReturnsEmptyListIfNoMatches() {
    }
}
