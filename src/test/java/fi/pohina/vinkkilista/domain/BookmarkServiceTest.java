package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import java.util.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class BookmarkServiceTest {

    private BookmarkService bookmarkService;
    private BookmarkDao bookmarkDao;
    private TagDao tagDao;

    @Before
    public void setUp() {
        bookmarkDao = spy(new InMemoryBookmarkDao());
        tagDao = spy(new InMemoryTagDao());

        bookmarkService = spy(new BookmarkService(bookmarkDao, tagDao));
    }

    @Test
    public void createBookmarkCreatesCorrectBookmark() {
        String title = "foobar",
            url = "http://foo.com",
            author = "author";

        bookmarkService.createBookmark(
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
        String tagName = "video";

        Tag tag = bookmarkService.findOrCreateTag(tagName);

        verify(tagDao, times(1)).add(tag);

        Tag createdTag = tagDao.findAll().get(0);

        assertEquals(tagName, createdTag.getName());
    }

    @Test
    public void tagSetStringToObjectCreatesMissingTags() {
        String tagString1 = "video";
        String tagString2 = "blog";
        String tagString3 = "news";

        Tag tag1 = bookmarkService.findOrCreateTag(tagString1);
        Tag tag2 = bookmarkService.findOrCreateTag(tagString2);
        Tag tag3 = bookmarkService.findOrCreateTag(tagString3);

        HashSet<String> stringSet = new HashSet<>(
                Arrays.asList(tagString1, tagString2, "journal")
        );

        Set<Tag> tagSet = bookmarkService.findOrCreateTags(stringSet);

        verify(bookmarkService, times(1)).findOrCreateTag("journal");

        assertEquals(3, tagSet.size());
        assertTrue(tagSet.contains(tag1));
        assertTrue(tagSet.contains(tag2));
        assertTrue(tagSet.contains(tagDao.findByName("journal")));
    }

    @Test
    public void validateTagReturnsTagsInCorrectForm() {
        String tag = "testi";
        assertEquals("testi", bookmarkService.validateTag(tag));

        tag = "Testi";
        assertEquals("testi", bookmarkService.validateTag(tag));

        tag = "  Testi  ";
        assertEquals("testi", bookmarkService.validateTag(tag));

        tag = "tes ti";
        assertEquals("tes ti", bookmarkService.validateTag(tag));

        tag = "tes           ti";
        assertEquals("tes ti", bookmarkService.validateTag(tag));

        tag = "testi!#¤%&";
        assertEquals(tag.toLowerCase(), bookmarkService.validateTag(tag));

        tag = "testi < < testi < < < < testi";
        assertEquals("testi < < testi < < < < testi", bookmarkService.validateTag(tag));

        tag = "TE&/(    STI   !#";
        assertEquals("te&/( sti !#", bookmarkService.validateTag(tag));

        tag = "T3s71";
        assertEquals("t3s71", bookmarkService.validateTag(tag));
    }

    @Test
    public void validateTagReturnsTagsInCorrectFormLong() {
        char[] chars = new char[95];

        int index = 0;

        for (int i = 32; i < 127; i++) {
            chars[index] = (char) i;
            index++;
        }

        for (int i = 0; i < 1000; i++) {
            String random = getRandomString(chars, 100);

            String validated = bookmarkService.validateTag(random);

            for (int j = 0; j < validated.length() - 1; j++) {
                if (validated.charAt(0) == ' ') {
                    fail("Validated string has a space in front: " + validated);
                }

                if (validated.charAt(validated.length() - 1) == ' ') {
                    fail("Validated string has a space at the end: " + validated);
                }

                if (validated.charAt(j) == ' ') {
                    if (validated.charAt(j + 1) == ' ') {
                        fail("Validated string has 2 or "
                        + "more spaces in a row: " + validated);
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

    @Test
    public void validateTagDoesntReturnEmptyTags() {
        String tag = "";
        assertEquals(null, bookmarkService.validateTag(tag));

        tag = "        ";
        assertEquals(null, bookmarkService.validateTag(tag));
    }

    @Test
    public void validateTagSetReturnsCorrectSet() {
        Set<String> tags = new HashSet<>();
        tags.add("   11   ");
        tags.add("22");
        tags.add("33");
        tags.add("     ");
        tags.add("44   ");
        tags.add("55");
        tags.add("66");

        tags = bookmarkService.validateTagSet(tags);

        int i = 0;
        for (String tag : tags) {
            i++;
            assertEquals(true, tags.contains("" + i + i));
        }

        assertEquals(6, tags.size());
    }

    @Test
    public void tagByUrlGetsCorrectTag() {
        String tag = bookmarkService.addTagStringByUrl(
                "https://www.youtube.com/watch?v=ZgjWOo7IqQY");
        assertEquals("Video", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://youtu.be/G60llMJepZI");
        assertEquals("Video", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://tastytreats-blog.blogspot.com/");
        assertEquals("Blog", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://wordpress.org/showcase/the-dish/");
        assertEquals("Blog", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://www.suomalainen.com/webapp/wcs"
                + "/stores/servlet/fi/skk/lazarus-p9789513196455--77");
        assertEquals("Book", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://ieeexplore.ieee.org/document/8543874");
        assertEquals("Scientific Publication", tag);

        tag = bookmarkService.addTagStringByUrl(
                "https://dl.acm.org/citation.cfm?id=3292530&picked=prox");
        assertEquals("Scientific Publication", tag);
    }
}
