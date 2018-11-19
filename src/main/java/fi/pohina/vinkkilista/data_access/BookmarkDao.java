
package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.List;

public interface BookmarkDao<T> {
    Bookmark findByTitle(String title);
    List<Bookmark> findAll();
    void create(Bookmark bookmark);
}

