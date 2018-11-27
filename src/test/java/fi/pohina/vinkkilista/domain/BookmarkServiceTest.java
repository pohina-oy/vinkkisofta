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

    /**
     * Tests that searching for bookmarks by tags returns a list of only
     * matching bookmarks
     */
    @Test
    public void canSearchBookmarksByTags() {
    }

    /**
     * Tests that searching for bookmarks by tags returns an empty list if no
     * matching bookmarks are found
     */
    @Test
    public void searchBookmarksByTagsReturnsEmptyListIfNoMatches() {
    }
}
