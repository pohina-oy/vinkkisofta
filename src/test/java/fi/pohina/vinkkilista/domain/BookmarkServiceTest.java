package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest {

    private BookmarkService bookmarks;
    private BookmarkDao bookmarkDao;
    private TagDao tagDao;

    @Before
    public void setUp() {
        bookmarkDao = spy(new InMemoryBookmarkDao());
        tagDao = spy(new InMemoryTagDao());

        bookmarks = new BookmarkService(bookmarkDao, tagDao);
    }

    @Test
    public void createBookmarkCreatesCorrectBookmark() {
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

    @Test
    public void createTagCreatesCorrectTag() {
    }

    @Test
    public void parseTagsFromStringCorrectlyParsesString() {
    }

    @Test
    public void tagSetStringToObjectFindsExistingTags() {
    }

    @Test
    public void tagSetStringToObjectCreatesMissingTags() {
    }

    @Test
    public void tagByUrlGetsCorrectTag() {
        String tag = bookmarks.addTagStringByUrl("https://www.youtube.com/watch?v=ZgjWOo7IqQY");
        assertEquals("Video", tag);
        tag = bookmarks.addTagStringByUrl("https://youtu.be/G60llMJepZI");
        assertEquals("Video", tag);
        tag = bookmarks.addTagStringByUrl("https://tastytreats-blog.blogspot.com/");
        assertEquals("Blog", tag);
        tag = bookmarks.addTagStringByUrl("https://wordpress.org/showcase/the-dish/");
        assertEquals("Blog", tag);
        tag = bookmarks.addTagStringByUrl("https://www.suomalainen.com/webapp/wcs/stores/servlet/fi/skk/lazarus-p9789513196455--77");
        assertEquals("Book", tag);
        tag = bookmarks.addTagStringByUrl("https://ieeexplore.ieee.org/document/8543874");
        assertEquals("Scientific Publication", tag);
        tag = bookmarks.addTagStringByUrl("https://dl.acm.org/citation.cfm?id=3292530&picked=prox");
        assertEquals("Scientific Publication", tag);
    }
}
