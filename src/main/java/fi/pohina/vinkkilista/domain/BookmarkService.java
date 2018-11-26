package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.domain.Tag;
import static java.lang.Boolean.FALSE;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookmarkService {

    private final BookmarkDao dao;

    public BookmarkService(BookmarkDao dao) {
        this.dao = dao;
    }
    
    public void addBlog(Blog blog) {
        dao.add(blog);
    }

    public Collection<Bookmark> getBlogs() {
        return dao.findAll();
    }
    
    /**
     * Function for retrieving bookmarks with any of the tags specified as a
     * comma-separated string list.
     *
     * @param tags comma-separated string list of tags
     * @return matching bookmarks
     */
    public Collection<Bookmark> getBookmarksByTags(String tags) {
        Set<Tag> tagSet = tagSetStringToObject(parseTagsString(tags), FALSE);
        return findByTagSet(tagSet);
    }
    
    /**
     * Function for parsing tags from a comma-delimited string to a string set.
     *
     * @param tags comma-separated string list of tags
     * @return tags in a string set
     */
    private Set<String> parseTagsString(String tags) {
        List<String> tagsList = Arrays.asList(tags.split(","));
        Set<String> tagsSet = new HashSet<>();
        
        for (String tag : tagsList) {
            tag = tag.trim();
            if (tag.length() > 0 && !tagsSet.contains(tag)) {
                tagsSet.add(tag);
            }
        }
        
        return tagsSet;
    }
    
    /**
     * Function for converting string tag set to a set of existing tag objects,
     * and alternatively also creates missing tags if specified.
     *
     * @param tags comma-separated string list of tags
     * @param createNew comma-separated string list of tags
     * @return set of tag objects
     */
    private Set<Tag> tagSetStringToObject(Set<String> tags, Boolean createNew) {
        Set<Tag> tagsSet = new HashSet<>();
        
        for (String tagString : tags) {
            Tag tagObject = tagDao.findByName(tagString);
            if (tagObject != null && !tagsSet.contains(tagObject)) {
                tagsSet.add(tagObject);
            } else if (createNew) {
                createTag(tagString);
                tagsSet.add(tagDao.findByName(tagString));
            }
        }
        
        return tagsSet;
    }
}
