package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BookmarkServiceTest {

    private BookmarkService bookmarks;

    @Before
    public void setUp() {
        bookmarks = new BookmarkService(new InMemoryBookmarkDao());
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
