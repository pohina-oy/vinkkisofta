package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import java.util.*;
import java.util.stream.Collectors;
import static org.hamcrest.CoreMatchers.hasItems;
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

        User creator = null;
        Set<String> tags = new HashSet<>(Arrays.asList("TE&/(    STI   !#  ", "journal"));

        bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

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

    /**
     * Tests that searching for bookmarks by tags returns a list of only
     * matching bookmarks
     */
    @Test
    public void canSearchBookmarksByTags() {
        addTaggedBookmarks();

        Collection<Bookmark> foundBookmarks = bookmarkService.getBookmarksByTags(
                new HashSet<>(Arrays.asList("video"))
        );

        assertEquals(2, foundBookmarks.size());
        assertThat(
                foundBookmarks.stream()
                        .map(Bookmark::getTitle)
                        .collect(Collectors.toList()),
                hasItems("third", "fourth")
        );

        foundBookmarks = bookmarkService.getBookmarksByTags(
                new HashSet<>(Arrays.asList("journal"))
        );

        assertEquals(1, foundBookmarks.size());
        assertThat(
                foundBookmarks.stream()
                        .map(Bookmark::getTitle)
                        .collect(Collectors.toList()),
                hasItems("third")
        );
    }

    /**
     * Tests that searching for bookmarks by tags not in dao does not result in
     * creating new tags.
     */
    @Test
    public void searchBookmarksByTagsDoesNotCreateNewTags() {
        addTaggedBookmarks();

        Collection<Bookmark> foundBookmarks = bookmarkService
                .getBookmarksByTags(new HashSet<>(Arrays.asList("blog")));

        assertEquals(0, foundBookmarks.size());
    }

    /**
     * Tests that searching for bookmarks by tags returns an empty list if no
     * matching bookmarks are found
     */
    @Test
    public void searchBookmarksByTagsReturnsEmptyListIfNoMatches() {
        addTaggedBookmarks();

        Collection<Bookmark> foundBookmarks = bookmarkService
                .getBookmarksByTags(new HashSet<>(Arrays.asList("blog")));

        assertEquals(0, foundBookmarks.size());
    }

    private void addTaggedBookmarks() {
        bookmarkService.createBookmark(
                "no tags",
                "www.tagless.com",
                "unknown",
                null,
                new HashSet<>()
        );

        bookmarkService.createBookmark(
                "third",
                "https://www.nature.com",
                "Editor",
                null,
                new HashSet<>(Arrays.asList("video", "journal"))
        );

        bookmarkService.createBookmark(
                "fourth",
                "https://www.videosite.net",
                "Firstname Lastname",
                null,
                new HashSet<>(Arrays.asList("video"))
        );
    }

    @Test
    public void tagSetStringToObjectCreatesMissingTags() {
        String tagString1 = "video";
        String tagString2 = "blog";
        String tagString3 = "news";

        Tag tag1 = bookmarkService.findOrCreateTag(tagString1);
        Tag tag2 = bookmarkService.findOrCreateTag(tagString2);
        Tag tag3 = bookmarkService.findOrCreateTag(tagString3);

        Set<String> stringSet = new HashSet<>(
                Arrays.asList(tagString1, tagString2, "journal")
        );

        Set<Tag> tagSet = bookmarkService.findOrCreateTags(stringSet);

        verify(bookmarkService, times(1)).findOrCreateTag("journal");

        assertEquals(3, tagSet.size());
        assertTrue(tagSet.contains(tag1));
        assertTrue(tagSet.contains(tag2));
        assertTrue(tagSet.contains(tagDao.findByName("journal")));
    }

    private String getRandomString(char[] chars, int length) {

        StringBuilder word = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char c = chars[(int) (Math.random() * chars.length)];
            word.append(c);
        }

        return word.toString();
    }
}
