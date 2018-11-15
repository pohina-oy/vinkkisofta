
package fi.pohina.vinkkilista.access;

import fi.pohina.vinkkilista.domain.Blog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import fi.pohina.vinkkilista.domain.Bookmark;
import static fi.pohina.vinkkilista.domain.BookmarkType.Blog;


public class BookmarkDao implements Dao<Bookmark, Integer> {

    Map<Integer, Bookmark> bookmarksDB = new HashMap<>();
    
    public BookmarkDao() {
        bookmarksDB.put(0, new Blog("eka", "https://www.abc.fi", Blog));
        bookmarksDB.put(1, new Blog("toinen", "https://www.qwerty.fi", Blog));
    }
    
    @Override
    public Bookmark findOne(Integer key) {
        return bookmarksDB.get(key);
    }

    @Override
    public List<Bookmark> findAll() {
        return new ArrayList<>(bookmarksDB.values());
    }

    @Override
    public void create(Integer key, Bookmark bookmark) {
        bookmarksDB.put(key, bookmark);
    }
    
}
