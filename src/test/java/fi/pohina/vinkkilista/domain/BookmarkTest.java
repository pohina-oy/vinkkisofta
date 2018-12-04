package fi.pohina.vinkkilista.domain;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.*;

public class BookmarkTest {

    @Test
    public void constructorSetsAllPropertiesCorrectly() {
        String id = "id",
            title = "title",
            url = "url",
            author = "John Wick";
        Tag tag = new Tag("id 1", "video");
        Set<Tag> expectedTags = new HashSet<>();
        expectedTags.add(tag);

        Bookmark bookmark = new Bookmark(id, title, url, author, expectedTags);

        assertEquals(id, bookmark.getId());
        assertEquals(title, bookmark.getTitle());
        assertEquals(url, bookmark.getUrl());
        assertEquals(author, bookmark.getAuthor());
        assertEquals(1, bookmark.getTags().size());
        assertThat(bookmark.getTags(), hasItem(tag));
    }

    @Test
    public void constructorOverloadSetsEmptyTagListCorrectly() {
        Bookmark bookmark = new Bookmark(
            "id", "title", "url", "author"
        );

        Set<Tag> tags = bookmark.getTags();

        assertEquals(0, tags.size());
    }

    @Test
    public void constructorOverloadSetsNullAuthorCorrectly() {
        Bookmark bookmark = new Bookmark(
            "id", "title", "url"
        );

        String author = bookmark.getAuthor();

        assertNull(author);
    }

    @Test
    public void bookmarkReturnsCorrectHashCode() {

        String id = "foobar";

        Bookmark bookmark = new Bookmark(
                id, "title", "url"
        );

        assertEquals(id.hashCode(), bookmark.hashCode());
    }
}
