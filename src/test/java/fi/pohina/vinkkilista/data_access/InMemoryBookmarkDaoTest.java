
package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class InMemoryBookmarkDaoTest {
    
    public InMemoryBookmarkDaoTest() {
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
     * Test of findByTitle method, of class InMemoryBookmarkDao.
     */
    @Test
    public void testFindByTitle() {
        System.out.println("findByTitle");
        String title = "";
        InMemoryBookmarkDao instance = new InMemoryBookmarkDao();
        Bookmark expResult = null;
        Bookmark result = instance.findByTitle(title);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of findAll method, of class InMemoryBookmarkDao.
     */
    @Test
    public void testFindAll() {
        System.out.println("findAll");
        InMemoryBookmarkDao instance = new InMemoryBookmarkDao();
        ArrayList<Bookmark> expResult = null;
        ArrayList<Bookmark> result = instance.findAll();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of create method, of class InMemoryBookmarkDao.
     */
    @Test
    public void testCreate() {
        System.out.println("create");
        Bookmark bookmark = null;
        InMemoryBookmarkDao instance = new InMemoryBookmarkDao();
        instance.create(bookmark);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
