package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest {

    private BookmarkService bookmarks;

    @Before
    public void setUp() {
    }

    @Test
    public void createBookmarkCreatesCorrectBookmark() {
        BookmarkDao bookmarkDao = spy(new InMemoryBookmarkDao());

        bookmarks = new BookmarkService(bookmarkDao);

        String title = "foobar",
            url = "http://foo.com",
            author = "author";

        bookmarks.createBookmark(
            title,
            url,
            author
        );

        verify(bookmarkDao, times(1))
            .add(any(Bookmark.class));

        Bookmark createdBookmark = bookmarkDao.findAll().get(0);

        assertEquals(title, createdBookmark.getTitle());
        assertEquals(url, createdBookmark.getUrl());
        assertEquals(author, createdBookmark.getAuthor());
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
