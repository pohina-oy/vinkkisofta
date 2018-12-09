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
    public void bookmarkIsCreatedWithValidTitleAndUrl() {
        String title = "foobar";
        String url = "http://foo.com";
        String author = null;
        User creator = null;
        Set<String> tags = null;

        Bookmark created = bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

        Set<String> expectedTags = new HashSet<>();

        verifyBookmark(title, url, author, creator, expectedTags, created);
    }

    @Test
    public void bookmarkIsCreatedWithValidAuthor() {
        String title = "barfoo";
        String url = "http://example.com";
        String author = "Gabe Newell";
        User creator = null;
        Set<String> tags = null;

        Bookmark created = bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

        Set<String> expectedTags = new HashSet<>();

        verifyBookmark(title, url, author, creator, expectedTags, created);
    }

    @Test
    public void bookmarkIsCreatedWithValidCreator() {
        String title = "Example title for tests";
        String url = "http://testing.com";
        String author = "Test Guru";
        User creator = new User("-1", "example@test.com", "testguru", -1);
        Set<String> tags = null;

        Bookmark created = bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

        Set<String> expectedTags = new HashSet<>();

        verifyBookmark(title, url, author, creator, expectedTags, created);
    }

    @Test
    public void bookmarkIsCreatedWithValidTags() {
        String title = "Valid tags";
        String url = "http://tags.com";
        String author = "Tagging King";
        User creator = new User("-1", "example.tagging@tagging.com", "tagging_king", -1);
        Set<String> tags = new HashSet<>(
                Arrays.asList("test   tag", "journal", "TäMä", "  trimmaus    ", "", "   ", "journal"));

        Bookmark created = bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

        Set<String> expectedTags = new HashSet<>(
                Arrays.asList("test tag", "journal", "tämä", "trimmaus"));

        verifyBookmark(title, url, author, creator, expectedTags, created);
    }
    
    @Test
    public void bookmarkGetsTagsFromUrl() {
        
    }

    @Test
    public void createTagCreatesCorrectTag() {
        String tagName = "video";

        Tag created = bookmarkService.findOrCreateTag(tagName);

        verify(tagDao, times(1)).add(created);

        assertEquals(tagName, created.getName());
        assertEquals(created, tagDao.findById(created.getId()));
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

    private void verifyBookmark(String title, String url, String author, User creator, Set<String> tags, Bookmark created) {
        verify(bookmarkDao, times(1))
                .add(any(Bookmark.class));
        assertEquals(title, created.getTitle());
        assertEquals(url, created.getUrl());
        assertEquals(author, created.getAuthor());
        assertEquals(creator, created.getCreator());
        assertEquals(tags.size(), created.getTags().size());
        for (Tag tag : created.getTags()) {
            assertTrue(tags.contains(tag.getName()));
        }
        assertEquals(created, bookmarkDao.findById(created.getId()));
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
}
