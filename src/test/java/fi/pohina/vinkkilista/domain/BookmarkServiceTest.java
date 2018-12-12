package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
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

    @Before
    public void setUp() {
        bookmarkDao = spy(new InMemoryBookmarkDao());
        bookmarkService = spy(new BookmarkService(bookmarkDao));
    }

    @Test
    public void bookmarkIsCreatedWithValidTitleAndUrl() {
        String title = "foobar";
        String url = "http://foo.com";
        String author = null;
        User creator = null;
        Set<Tag> tags = null;

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
        Set<Tag> tags = null;

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
        Set<Tag> tags = null;

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

        Set<Tag> tags = new HashSet<>(
                Arrays.asList(
                        new Tag("1", "scientific publication"),
                        new Tag("2", "journal"),
                        new Tag("3", "blog")));

        Bookmark created = bookmarkService.createBookmark(
                title,
                url,
                author,
                creator,
                tags);

        Set<String> expectedTags = new HashSet<>(
                Arrays.asList("scientific publication", "journal", "blog"));

        verifyBookmark(title, url, author, creator, expectedTags, created);
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
                null
        );

        bookmarkService.createBookmark(
                "third",
                "https://www.nature.com",
                "Editor",
                null,
                new HashSet<>(Arrays.asList(new Tag("1", "video"), new Tag("2", "journal")))
        );

        bookmarkService.createBookmark(
                "fourth",
                "https://www.videosite.net",
                "Firstname Lastname",
                null,
                new HashSet<>(Arrays.asList(new Tag("3", "video")))
        );
    }
}
