package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;
import java.util.ArrayList;
import java.util.Set;

public class InMemoryBookmarkDao implements BookmarkDao {

    private ArrayList<Bookmark> bookmarksDB = new ArrayList<>();

    public InMemoryBookmarkDao() {
    }

    /**
     * Function for finding a saved bookmark by id
     *
     * @param id id by which a bookmark is searched
     * @return found bookmark, otherwise null
     */
    @Override
    public Bookmark findByID(int id) {
        for (Bookmark bookmark : bookmarksDB) {
            if (bookmark.getID() == id) {
                return bookmark;
            }
        }
        return null;
    }

    /**
     * Function for finding all saved bookmarks
     *
     * @return found bookmarks as an ArrayList
     */
    @Override
    public ArrayList<Bookmark> findAll() {
        return bookmarksDB;
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
