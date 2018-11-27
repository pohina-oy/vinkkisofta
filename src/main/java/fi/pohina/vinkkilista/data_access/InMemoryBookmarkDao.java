package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;
import java.util.*;

/**
 * Provides an in-memory implementation of the {@link BookmarkDao} interface,
 * backed by a {@link List<Bookmark>}.
 */
public class InMemoryBookmarkDao implements BookmarkDao {

    private final List<Bookmark> bookmarksDB;

    public InMemoryBookmarkDao() {
        this(new ArrayList<>());
    }

    public InMemoryBookmarkDao(List<Bookmark> initialBookmarks) {
        this.bookmarksDB = initialBookmarks;
    }

    /**
     * {@inheritDoc}
     * This is a linear time operation.
     */
    @Override
    public Bookmark findById(String id) {
        for (Bookmark bookmark : bookmarksDB) {
            if (bookmark.getId().equals(id)) {
                return bookmark;
            }
        }

        return null;
    }

    @Override
    public List<Bookmark> findAll() {
        // return a new ArrayList so that the consumer cannot change our
        // internal copy of the bookmark list
        return new ArrayList<>(this.bookmarksDB);
    }

    /**
     * Function for finding saved bookmarks matching any of the tags specified
     * in input set.
     *
     * @param tags set of tags to search for bookmarks
     * @return found bookmarks as an ArrayList
     */
    @Override
    public ArrayList<Bookmark> findByTagSet(Set<Tag> tags) {
        ArrayList<Bookmark> bookmarksList = new ArrayList<>();
        
        for (Bookmark bookmark : bookmarksDB) {
            for (Tag tag : bookmark.getTags()) {
                if (tags.contains(tag)) {
                    bookmarksList.add(bookmark);
                    break;
                }
            }
        }
        
        return bookmarksList;
    }

    /**
     * Function for adding a new bookmark
     *
     * @param bookmark bookmark which is saved
     */
    
    @Override
    public void add(Bookmark bookmark) {
        bookmarksDB.add(bookmark);
    }
}
