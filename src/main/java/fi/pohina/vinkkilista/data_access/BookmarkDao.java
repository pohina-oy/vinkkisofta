package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    
    public ArrayList<Bookmark> findByTagSet(Set<Tag> tags);

    void add(Bookmark bookmark);
}
