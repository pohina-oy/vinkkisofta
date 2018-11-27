package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import java.util.*;

public class BookmarkService {

    private final BookmarkDao bookmarkDao;
    private final TagDao tagDao;

    public BookmarkService(BookmarkDao bookmarkDao, TagDao tagDao) {
        this.bookmarkDao = bookmarkDao;
        this.tagDao = tagDao;
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
    /**
     * Creates a new {@link Bookmark} from the specified title, url, author and an array of tags.
     */
    public void createBookmark(
            String title,
            String url,
            String author,
            String tags
    ) {
        String id = generateBookmarkId();

        Set<Tag> set = tagSetStringToObject(parseTagsFromString(tags), true);

        Bookmark bookmark = new Bookmark(
                id,
                title,
                url,
                author,
                set
        );

        bookmarkDao.add(bookmark);
    }

    /**
     * Creates a new {@link Tag} from the specified tag name.
     *
     * @param name the name of the new tag to be created
     * @return reference to the new tag
     */
    public Tag createTag(String name) {
        String id = generateBookmarkId();

        Tag tag = new Tag(id, name);

        tagDao.add(tag);

        return tag;
    }

    public Collection<Bookmark> getAllBookmarks() {
        return bookmarkDao.findAll();
    }

    private String generateBookmarkId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Function for parsing tags from a comma-delimited string to a string set.
     *
     * @param tags comma-separated string list of tags
     * @return tags in a string set
     */
    private Set<String> parseTagsFromString(String tags) {
        String[] tagsList = tags.split(",");
        Set<String> tagsSet = new HashSet<>();
        
        for (String tag : tagsList) {
            tag = validateTag(tag);

            if (tag != null) {
                tagsSet.add(tag);
            }
        }
        
        return tagsSet;
    }

    /***
     * Function for validating a tag.
     * @param tag
     * @return A validated tag as a string that contains no extra spaces and has only allowed characters.
     */
    private String validateTag(String tag) {

        tag = tag.trim().toLowerCase();
        if (tag.length() == 0) {
            return null;
        }

        StringBuilder validated = new StringBuilder();
        char lastCharacter = ' ';

        for (char c : tag.toCharArray()) {
            if (allowedCharacter(c, lastCharacter != ' ')) {
                validated.append(c);
            }
            lastCharacter = c;
        }

        return validated.toString();
    }
    private boolean allowedCharacter(char c, boolean allowSpace) {

        if (c >= 'a' && c <= 'z') {
            return true;
        }

        if (c >= '0' || c <= '9') {
            return true;
        }

        if (c == 'ä' || c == 'ö' || c == 'å') {
            return true;
        }

        if (allowSpace && c == ' ') {
            return true;
        }

        return false;
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
                tagsSet.add(createTag(tagString));
            }
        }
        
        return tagsSet;
    }
}
