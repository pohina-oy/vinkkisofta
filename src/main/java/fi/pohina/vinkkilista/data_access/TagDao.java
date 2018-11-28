package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Tag;
import java.util.*;

public interface TagDao {

    /**
     * Finds and returns a tag by the specified ID, or <c>null</c> if not found.
     *
     * @param id the id of the tag.
     * @return the tag if found, otherwise <c>null</c>.
     */
    Tag findById(String id);
    
    /**
     * Finds and returns a tag by tag name, or <c>null</c> if not found.
     *
     * @param name
     * @return
     */
    Tag findByName(String name);

    List<Tag> findAll();

    void add(Tag tag);
}
