
package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.ArrayList;


public class InMemoryBookmarkDao implements BookmarkDao<Bookmark> {
    ArrayList<Bookmark> bookmarksDB = new ArrayList<>();
    
    public InMemoryBookmarkDao() {
    }

    /**
     * Function for finding saved bookmark by title
     * @param title
     * @return 
     */
    @Override
    public Bookmark findByTitle(String title) {
        for (Bookmark bookmark : bookmarksDB) {
            if (bookmark.getTitle().equals(title)) {
                return bookmark;
            }
        }
        return null;
    }

    /**
     * Function for finding all saved bookmarks
     * @return 
     */
    @Override
    public ArrayList<Bookmark> findAll() {
        return bookmarksDB;
    }

    /**
     * Function for adding a new bookmark
     * @param bookmark
     */
    @Override
    public void add(Bookmark bookmark) {
        bookmarksDB.add(bookmark);
    }
    
}
