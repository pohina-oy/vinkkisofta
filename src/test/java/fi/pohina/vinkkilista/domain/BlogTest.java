package fi.pohina.vinkkilista.domain;

import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BlogTest {
    
    
    @Test
    public void BlogConstructorWorks() {
        
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
    public void CanAddCoursesToBlog() {
        
        List<Course> courses = new ArrayList<Course>();
        courses.add(new Course("course1"));
        
    }
}
