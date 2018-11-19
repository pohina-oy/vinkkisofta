
package fi.pohina.vinkkilista.access;

import fi.pohina.vinkkilista.domain.Blog;
import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.ArrayList;


public class InMemoryBookmarkDao implements BookmarkDao<Bookmark> {

    ArrayList<Bookmark> bookmarksDB = new ArrayList<>();
    
    public InMemoryBookmarkDao() {
        bookmarksDB.add(new Blog("eka", "https://www.abc.fi"));
        bookmarksDB.add(new Blog("toinen", "https://www.qwerty.fi"));
    }

    @Override
    public Bookmark findByTitle(String title) {
        return bookmarksDB.get(0);
    }

    @Override
    public ArrayList<Bookmark> findAll() {
        return bookmarksDB;
    }

    @Override
    public void create(Bookmark bookmark) {
        bookmarksDB.add(bookmark);
    }
    
}
