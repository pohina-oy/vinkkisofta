package fi.pohina.vinkkilista.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookmarkServiceTest {
    
    BookmarkService bookmarks;

    @Before
    public void setUp() {
        bookmarks = new BookmarkService();
    }
    
    @Test
    public void canAddBlogs() {
        int blogsInBeginning = bookmarks.getBlogs().size();
        Blog blog = new Blog(
            "TestBlog",
            "https://www.example.com",
            "Testing Company");
        bookmarks.addBlog(blog);
        assertEquals(blogsInBeginning + 1, bookmarks.getBlogs().size());
    }

}
