
package fi.pohina.vinkkilista.data_access;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({fi.pohina.vinkkilista.data_access.InMemoryBookmarkDaoTest.class, fi.pohina.vinkkilista.data_access.BookmarkDaoTest.class})
public class Data_accessSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }
    
}
