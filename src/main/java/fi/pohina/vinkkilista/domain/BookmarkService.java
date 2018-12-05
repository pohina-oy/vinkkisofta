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
     * Creates a new {@link Bookmark} from the specified title, url, author and 
     * a string of tags.
     */
    public void createBookmark(
            String title, 
            String url, 
            String author,
            User creator,
            Set<String> tags
    ) {
        String id = generateBookmarkId();
        
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.add(addTagStringByUrl(url));
        
        Set<Tag> tagSet = findOrCreateTags(tags);

        Bookmark bookmark = new Bookmark(
                id,
                title,
                url,
                author,
                creator,
                tagSet
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
     * set of strings.
     *
     * @param stringTags set of tags as string
     * @return matching bookmarks
     */
    public Collection<Bookmark> getBookmarksByTags(Set<String> stringTags) {
        return bookmarkDao.findByTagSet(validateTagSet(stringTags));
    }

    /**
     * Checks and fixes the given tag set.
     *
     * @param tags Tags in a string set
     * @return Tags in a string set
     */
    public Set<String> validateTagSet(Set<String> tags) {
        Set<String> tagsSet = new HashSet<>();

        for (String tag : tags) {
            tag = validateTag(tag);

            if (tag != null) {
                tagsSet.add(tag);
            }
        }

        return tagsSet;
    }

    /***
     * Function for validating and cleaning a tag.
     * @param tag
     * @return A validated tag as a string that contains no extra spaces and 
     * has only allowed characters. Null if length is 0.
     * Currently Only allows alpha-numeric characters.
     */
    public String validateTag(String tag) {
        String cleanedTag = tag
            .toLowerCase()
            .replaceAll(" +", " ")
            .trim();

        return cleanedTag.length() == 0 ? null : cleanedTag;
    }

    /**
     * Finds existing {@link Tag} using the specified name, otherwise creates
     * a new one.
     *
     * @param name the name of the new tag to be created
     * @return reference to the matching or new tag
     */
    public Tag findOrCreateTag(String name) {
        Tag tag = tagDao.findByName(name);
        if (tag != null) {
            return tag;
        }

        String id = generateBookmarkId();

        tag = new Tag(id, name);

        tagDao.add(tag);

        return tag;
    }

    /**
     * Function for converting string tag set to a set of tag objects, asking
     * for new tags to be created if no matching ones are found. Validates
     * the given set of tags.
     *
     * @param tags set of tag strings
     * @return set of tag objects
     */
    public Set<Tag> findOrCreateTags(Set<String> tags) {
        Set<Tag> tagsSet = new HashSet<>();

        tags = validateTagSet(tags);

        for (String tagString : tags) {
            tagsSet.add(findOrCreateTag(tagString));
        }

        return tagsSet;
    }

    /**
     * Takes the URL given and returns the appropriate tag 
     * related to that URL as a string.
     *
     * @param url The URL of the bookmark
     * @return Name of the tag as string, empty if no match
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
