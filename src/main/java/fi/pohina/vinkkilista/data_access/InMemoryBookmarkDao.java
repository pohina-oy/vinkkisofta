package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.ArrayList;

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
     * Function for adding a new bookmark
     *
     * @param bookmark bookmark which is saved
     */
    @Override
    public void add(Bookmark bookmark) {
        bookmarksDB.add(bookmark);
    }

}
