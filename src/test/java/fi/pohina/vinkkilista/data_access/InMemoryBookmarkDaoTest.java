package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class InMemoryBookmarkDaoTest {

    private InMemoryBookmarkDao bookmarkDao;

    @Before
    public void setUp() {
        bookmarkDao = new InMemoryBookmarkDao();

        Bookmark first = new Bookmark(
            "first-id",
            "first",
            "https://www.abc.fi"
        );

        Bookmark second = new Bookmark(
            "second-id",
            "second",
            "https://www.qwerty.com",
            "John Smith"
        );

        bookmarkDao.add(first);
        bookmarkDao.add(second);
    }

    @Test
    public void findByIdFindsInitiallyAddedBookmarkById() {
        String expectedId = "first-id";

        Bookmark foundBookmark = bookmarkDao.findById(expectedId);

        assertEquals(expectedId, foundBookmark.getId());
    }

    @Test
    public void findByIdDoesNotFindNonExistentId() {
        Bookmark foundBookmark = bookmarkDao.findById("intellij-is-cool");

        assertNull(foundBookmark);
    }

    @Test
    public void findAllCorrectlyFindsInitiallyAddedBookmarks() {
        List<String> foundIds = bookmarkDao.findAll()
            .stream()
            .map(Bookmark::getId)
            .collect(Collectors.toList());

        assertEquals(2, foundIds.size());
        assertThat(foundIds, hasItems("first-id", "second-id"));
    }

    @Test
    public void findAllFindsPreviousBookmarksAfterAdd() {
        String newId = "new-123";
        Bookmark newEntry = new Bookmark(
            newId,
            "bookmarks daily 2",
            "http://www.somesite.com"
        );

        bookmarkDao.add(newEntry);

        List<String> bookmarkIds = bookmarkDao.findAll()
            .stream()
            .map(Bookmark::getId)
            .collect(Collectors.toList());

        assertThat(bookmarkIds, hasItem("first-id"));
        assertThat(bookmarkIds, hasItem("second-id"));
        assertThat(bookmarkIds, hasItem(newId));
    }

    @Test
    public void addCorrectlyAddsNewBookmark() {
        String addedId = "bookmark-3";
        Bookmark newEntry = new Bookmark(
            addedId,
            "bookmarks daily",
            "http://www.somesite.org"
        );

        bookmarkDao.add(newEntry);

        assertEquals(addedId, bookmarkDao.findById(addedId).getId());
        assertThat(bookmarkDao.findAll(), hasItem(newEntry));
    }

    /**
     * Tests that finding bookmarks by tags returns only matching bookmarks
     */
    @Test
    public void findByTagSetReturnsCorrectBookmarks() {
        Tag tagVideo = new Tag("tag1", "video");
        Tag tagJournal = new Tag("tag3", "journal");

        Bookmark third = new Bookmark(
            "third-id",
            "third",
            "https://www.nature.com",
            "Editor",
            null,
            new HashSet<>(Arrays.asList(tagVideo, tagJournal))
        );

        Bookmark fourth = new Bookmark(
            "fourth-id",
            "fourth",
            "https://www.videosite.net",
            "Firstname Lastname",
            null,
            new HashSet<>(Arrays.asList(tagVideo))
        );

        bookmarkDao.add(third);
        bookmarkDao.add(fourth);

        List<String> foundIdsVideo = bookmarkDao
            .findByTagSet(new HashSet<>(Arrays.asList("video")))
            .stream()
            .map(Bookmark::getId)
            .collect(Collectors.toList());

        assertEquals(2, foundIdsVideo.size());
        assertThat(foundIdsVideo, hasItems("third-id", "fourth-id"));

        List<String> foundIdsJournal = bookmarkDao
            .findByTagSet(new HashSet<>(Arrays.asList("journal")))
            .stream()
            .map(Bookmark::getId)
            .collect(Collectors.toList());

        assertEquals(1, foundIdsJournal.size());
        assertThat(foundIdsJournal, hasItems("third-id"));
    }

    /**
     * Tests that finding bookmarks by tags returns an empty list if there
     * are no matching bookmarks
     */
    @Test
    public void findByTagSetReturnsEmptyListIfNoMatches() {
        Tag tagVideo = new Tag("tag1", "video");
        Tag tagBlog = new Tag("tag2", "blog");

        Bookmark fourth = new Bookmark(
            "fourth-id",
            "fourth",
            "https://www.videosite.net",
            "Firstname Lastname",
            null,
            new HashSet<>(Arrays.asList(tagVideo))
        );

        bookmarkDao.add(fourth);

        List<Bookmark> foundBookmarks = bookmarkDao
            .findByTagSet(new HashSet<>(Arrays.asList("blog")));

        assertEquals(0, foundBookmarks.size());
    }
}
