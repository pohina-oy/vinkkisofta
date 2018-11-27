package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

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
    public void validateTagReturnsTagsInCorrectForm(){

        String tag = "testi";
        assertEquals("testi", bookmarks.validateTag(tag));

        tag = "Testi";
        assertEquals("testi", bookmarks.validateTag(tag));

        tag = "  Testi  ";
        assertEquals("testi", bookmarks.validateTag(tag));

        tag = "tes ti";
        assertEquals("tes ti", bookmarks.validateTag(tag));

        tag = "tes           ti";
        assertEquals("tes ti", bookmarks.validateTag(tag));

        tag = "testi!#¤%&/()=?:;:   >";
        assertEquals("testi", bookmarks.validateTag(tag));

        tag = "testi < < testi < < < < testi";
        assertEquals("testi testi testi", bookmarks.validateTag(tag));

        tag = "t<e<s<t<i< < < < < < ";
        assertEquals("testi", bookmarks.validateTag(tag));

        tag = "T3s71";
        assertEquals("t3s71", bookmarks.validateTag(tag));
    }
    @Test
    public void validateTagReturnsTagsInCorrectFormLong(){

        char[] chars = new char[98];
        chars[0] = 'ö';
        chars[1] = 'ä';
        chars[2] = 'å';

        int index = 3;

        for (int i = 32; i < 127; i++) {
            chars[index] = (char) i;
            index++;
        }


        for (int i = 0; i < 1000; i++) {

            String random = getRandomString(chars, 100);

            String validated = bookmarks.validateTag(random);

            if (!validated.matches("^[a-z0-9 öäå]*$")) {
                fail("Validated string contains forbidden characters: " + validated);
            }

            for (int j = 0; j < validated.length() - 1; j++) {
                if (validated.charAt(j) == ' ') {
                    if (validated.charAt(j + 1) == ' '){
                        fail("Validated string has 2 or more spaces in a row: " + validated);
                    }
                }
            }
        }
    }
    private String getRandomString(char[] chars, int length) {

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = chars[(int) (Math.random() * chars.length)];
            word.append(c);
        }

        return word.toString();
    }
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
