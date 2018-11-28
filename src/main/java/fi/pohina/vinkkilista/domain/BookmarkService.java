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

        Set<String> tagStringSet = parseTagsFromString(addTagStringByUrl(url));
        Set<Tag> tagSet = tagSetStringToObject(tagStringSet, true);

        Bookmark bookmark = new Bookmark(
            id,
            title,
            url,
            author,
            tagSet
        );

        bookmarkDao.add(bookmark);
    }
    /**
     * Creates a new {@link Bookmark} from the specified title, url, author and 
     * a string of tags.
     */
    public void createBookmark(
            String title, 
            String url, 
            String author, 
            String tags
    ) {
        String id = generateBookmarkId();

        Set<String> tagStringSet = parseTagsFromString(
                tags + "," + addTagStringByUrl(url));
        Set<Tag> tagSet = tagSetStringToObject(tagStringSet, true);

        Bookmark bookmark = new Bookmark(
                id,
                title,
                url,
                author,
                tagSet
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
     * @return A validated tag as a string that contains no extra spaces and 
     * has only allowed characters.
     * Currently Only allows alpha-numeric characters.
     */
    public String validateTag(String tag) {

        tag = tag.toLowerCase();
        if (tag.length() == 0) {
            return null;
        }

        StringBuilder validated = new StringBuilder();

        for (int i = 0; i < tag.length(); i++) {
            char c = tag.charAt(i);

            if (allowedCharacter(c)) {
                validated.append(c);
            }
        }

        return validated.toString().trim().replaceAll(" +", " ");
    }
    private boolean allowedCharacter(char c) {

        if (c >= 'a' && c <= 'z') {
            return true;
        }

        if ((c >= '0' && c <= '9') || c == ' ') {
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

    /**
     * Takes the URL given and returns the appropriate tag 
     * related to that URL as string.
     *
     * @param url The URL of the bookmark
     * @return Name of the tag as string
     */
    public String addTagStringByUrl(String url) {
        String[] videoUrls = {"youtube.com", "vimeo.com", "youtu.be"};
        String[] blogUrls = {
            "blogger.com", "blogs.helsinki.fi", 
            "wordpress.org", "blogspot.com"};
        String[] bookUrls = {".suomalainen.com"};
        String[] scienceUrls = {"dl.acm.org", "ieeexplore.ieee.org"};

        for (String videoUrl : videoUrls) {
            if (url.contains(videoUrl)) {
                return "Video";
            }
        }

        for (String blogUrl : blogUrls) {
            if (url.contains(blogUrl)) {
                return "Blog";
            }
        }

        for (String bookUrl : bookUrls) {
            if (url.contains(bookUrl)) {
                return "Book";
            }
        }

        for (String scienceUrl : scienceUrls) {
            if (url.contains(scienceUrl)) {
                return "Scientific Publication";
            }
        }

        return "";
    }
}
