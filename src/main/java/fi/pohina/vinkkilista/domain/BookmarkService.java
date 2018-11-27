package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import static java.lang.Boolean.FALSE;
import java.util.*;

public class BookmarkService {

    private final BookmarkDao bookmarkDao;

    public BookmarkService(BookmarkDao bookmarkDao) {
        this.bookmarkDao = bookmarkDao;
    }

    /**
     * Creates a new {@link Bookmark} from the specified title, url and author.
     */
    public void createBookmark(
        String title,
        String url,
        String author
    ) {
        String id = generateBookmarkId();

        Bookmark bookmark = new Bookmark(
            id,
            title,
            url,
            author,
            new HashSet<>()
        );

        bookmarkDao.add(bookmark);
    }

    public Collection<Bookmark> getAllBookmarks() {
        return bookmarkDao.findAll();
    }

    private String generateBookmarkId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Function for retrieving bookmarks with any of the tags specified as a
     * comma-separated string list.
     *
     * @param tags comma-separated string list of tags
     * @return matching bookmarks
     */
    public Collection<Bookmark> getBookmarksByTags(String tags) {
        Set<Tag> tagSet = tagSetStringToObject(parseTagsFromString(tags), FALSE);
        return dao.findByTagSet(tagSet);
    }
    
    /**
     * Function for parsing tags from a comma-delimited string to a string set.
     *
     * @param tags comma-separated string list of tags
     * @return tags in a string set
     */
    private Set<String> parseTagsFromString(String tags) {
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
     * @param createNew Boolean for whether to create missing tags
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
