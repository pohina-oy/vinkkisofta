
package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookmarkDaoTest {
    
    public BookmarkDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of findByTitle method, of class BookmarkDao.
     */
    @Test
    public void testFindByTitle() {
        System.out.println("findByTitle");
        String title = "";
        BookmarkDao instance = new BookmarkDaoImpl();
        Bookmark expResult = null;
        Bookmark result = instance.findByTitle(title);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class BookmarkDao.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        BookmarkDao instance = new BookmarkDaoImpl();
        List<Bookmark> expResult = null;
        List<Bookmark> result = instance.findAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class BookmarkDao.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Bookmark bookmark = null;
        BookmarkDao instance = new BookmarkDaoImpl();
        instance.create(bookmark);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    public class BookmarkDaoImpl implements BookmarkDao {

        public Bookmark findByTitle(String title) {
            return null;
        }

        public List<Bookmark> findAll() {
            return null;
        }

        public void create(Bookmark bookmark) {
        }
    }
    
}
