package fi.pohina.vinkkilista.domain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author vgpulsa
 */
public class CourseTest {
    private Course course;
    
    public CourseTest() {
    }
    
    
    @Before
    public void setUp() {
        course = new Course("course0");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of setName method, of class Course.
     */
    @Test
    public void testSetGetName() {
        course.setName("test1");
        assertEquals("test1", course.getName());
    }

    /**
     * Test of setDescription method, of class Course.
     */
    @Test
    public void testSetGetDescription() {
        course.setDescription("desc1");
        assertEquals("desc1", course.getDescription());
    }

    @Test
    public void testConstructor() {
        Course course2 = new Course("name1");
        
        assertEquals("name1", course2.getName());
        assertEquals("", course2.getDescription());
    }
}
