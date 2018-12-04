package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Tag;
import java.util.*;

/**
 * Provides an in-memory implementation of the {@link TagDao} interface,
 * backed by a {@link List<Tag>}.
 */
public class InMemoryTagDao implements TagDao {

    private final List<Tag> tagsDB;

    public InMemoryTagDao() {
        this(new ArrayList<>());
    }

    public InMemoryTagDao(List<Tag> initialTags) {
        this.tagsDB = initialTags;
    }

    /**
     * {@inheritDoc}
     * This is a linear time operation.
     */
    @Override
    public Tag findById(String id) {
        for (Tag tag : tagsDB) {
            if (tag.getId().equals(id)) {
                return tag;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag findByName(String name) {
        for (Tag tag : tagsDB) {
            if (tag.getName().equals(name)) {
                return tag;
            }
        }

        return null;
    }

    @Override
    public List<Tag> findAll() {
        // return a new ArrayList so that the consumer cannot change our
        // internal copy of the tag list
        return new ArrayList<>(this.tagsDB);
    }

    /**
     * Function for adding a new tag
     *
     * @param tag tag which is saved
     */
    @Override
    public void add(Tag tag) {
        tagsDB.add(tag);
    }
}
