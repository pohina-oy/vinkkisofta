package fi.pohina.vinkkilista.domain;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class BlogTest {

    @Test
    public void blogConstructorWorks() {

        String title = "title";
        String url = "url";
        BookmarkType type = BookmarkType.Blog;
        String author = "author";

        Blog blog = new Blog(title, url, type);

        assertEquals(title, blog.getTitle());
        assertEquals(url, blog.getUrl());
        assertEquals(type, blog.getBookmarkType());
        assertEquals("No author specified.", blog.getAuthor());

        blog = new Blog(title, url, type, author);

        assertEquals(title, blog.getTitle());
        assertEquals(url, blog.getUrl());
        assertEquals(type, blog.getBookmarkType());
        assertEquals(author, blog.getAuthor());
    }

    @Test
    public void canAddCourseToBlog() {

        List<Course> courses = new ArrayList<Course>();
        courses.add(new Course("course1"));
        courses.add(new Course("course2"));
        courses.add(new Course("course3"));

        Blog blog = new Blog("a", "b", BookmarkType.Blog);

        blog.addRelatedCourse(new Course("course0"));
        ArrayList<Course> related = blog.getRelatedCourses();
        
        
        if (related == null) {
            fail("Related course list was null.");
        }
        if (related.size() == 0) {
            fail("Related course list was empty.");
        }

        assertEquals("course0", blog.getRelatedCourses().get(0).getName());
    }

    @Test
    public void canAddCoursesToBlog() {

        List<Course> courses = new ArrayList<Course>();
        courses.add(new Course("course1"));
        courses.add(new Course("course2"));
        courses.add(new Course("course3"));

        Blog blog = new Blog("a", "b", BookmarkType.Blog);

        blog.addRelatedCourses(courses);
        ArrayList<Course> related = blog.getRelatedCourses();

        if (related == null) {
            fail("Related course list was null.");
        }
        if (related.size() != 3) {
            fail("Related course list was empty.");
        }
        
        for (int i = 0; i < courses.size(); i++) {
            
            if (related.get(i) == null) {
                fail("Course was null.");
            }
            assertEquals(courses.get(i), related.get(i));
        }

    }
}
