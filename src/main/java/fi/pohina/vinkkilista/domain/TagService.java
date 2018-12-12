package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.TagDao;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TagService {

    private final TagDao tagDao;
    private final Map<String, String> urlToTagMap = new HashMap<>();

    public TagService(TagDao tagDao) {
        this.tagDao = tagDao;
        initUrlToTagMap();
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
     * Function for converting comma separated tag names to validated set of tag
     * names.
     *
     * @param tagCsv comma separated tag names
     * @return set of validated tag names
     */
    public static Set<String> toValidatedSet(String tagCsv) {
        Set<String> tagNames = new HashSet<>();

        if (tagCsv == null || tagCsv.isEmpty()) {
            return tagNames;
        }

        String[] tagArray = tagCsv.split(",");

        for (String tagName : tagArray) {
            String validated = validateTag(tagName);
            if (!validated.isEmpty()) {
                tagNames.add(validated);
            }
        }

        return tagNames;
    }

    /**
     * Function for getting a tag name by url.
     *
     * @param url the url to be analyzed
     * @return name of tag or empty string if none was found
     */
    public String tagFromUrl(String url) {
        for (String key : urlToTagMap.keySet()) {
            if (url.contains(key)) {
                return urlToTagMap.get(key);
            }
        }

        return "";
    }

    /**
     * Function for validating and cleaning a tag.
     *
     * @param tag
     * @return A validated tag as a string that contains no extra spaces and has
     * only allowed characters. Null if length is 0. Currently Only allows
     * alpha-numeric characters.
     */
    private static String validateTag(String tag) {
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

    private void initUrlToTagMap() {
        urlToTagMap.put("youtube.com", "video");
        urlToTagMap.put("youtu.be", "video");
        urlToTagMap.put("vimeo.com", "video");
        urlToTagMap.put("blogger.com", "blog");
        urlToTagMap.put("blogs.helsinki.fi", "blog");
        urlToTagMap.put("wordpress.org", "blog");
        urlToTagMap.put("blogspot.com", "blog");
        urlToTagMap.put(".suomalainen.com", "book");
        urlToTagMap.put("dl.acm.org", "scientific publication");
        urlToTagMap.put("ieeexplore.ieee.org", "scientific publication");
    }
}
