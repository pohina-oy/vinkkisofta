package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.TagDao;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TagService {

    private final TagDao tagDao;

    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    /**
     * Finds existing {@link Tag} using the specified name, otherwise creates a
     * new one.
     *
     * @param name the name of the new tag to be created
     * @return reference to the matching or new tag
     */
    public Tag findOrCreateTag(String name) {
        name = validateTag(name);
        if (name.isEmpty()) {
            return null;
        }

        Tag tag = tagDao.findByName(name);
        if (tag != null) {
            return tag;
        }

        String id = generateTagId();

        tag = new Tag(id, name);

        tagDao.add(tag);

        return tag;
    }

    /**
     * Function for converting string tag set to a set of tag objects, asking
     * for new tags to be created if no matching ones are found. Validates the
     * given set of tags.
     *
     * @param tagNames set of tag strings
     * @return set of tag objects
     */
    public Set<Tag> findOrCreateTags(Set<String> tagNames) {
        Set<Tag> tagsSet = new HashSet<>();

        for (String name : tagNames) {
            Tag tag = findOrCreateTag(name);
            if (tag != null) {
                tagsSet.add(tag);
            }
        }

        return tagsSet;
    }

    /**
     * Function for validating and cleaning a tag.
     *
     * @param tag
     * @return A validated tag as a string that contains no extra spaces and has
     * only allowed characters. Null if length is 0. Currently Only allows
     * alpha-numeric characters.
     */
    private String validateTag(String tag) {
        if (tag == null) {
            return "";
        }
        String cleanedTag = tag
                .toLowerCase()
                .replaceAll(" +", " ")
                .trim();

        return cleanedTag.length() == 0 ? "" : cleanedTag;
    }

    private String generateTagId() {
        return UUID.randomUUID().toString();
    }
}
