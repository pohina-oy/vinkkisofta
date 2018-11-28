package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.*;

public interface BookmarkDao {

    /**
     * Finds and returns a bookmark by the specified ID, or <c>null</c> if not
     * found.
     *
     * @param id the id of the bookmark.
     * @return the bookmark if found, otherwise <c>null</c>.
     */
    Bookmark findById(String id);

    List<Bookmark> findAll();

    void add(Bookmark bookmark);
}
