package fi.pohina.vinkkilista.domain;

import com.google.common.base.Strings;
import fi.pohina.vinkkilista.data_access.BookmarkDao;
import java.util.*;

public class BookmarkService {

    private final BookmarkDao bookmarkDao;

    public BookmarkService(BookmarkDao bookmarkDao) {
        this.bookmarkDao = bookmarkDao;
    }

    /**
     * Creates a new {@link Bookmark} from the specified title, url, author,
     * creator and a string of tags.
     *
     * @param title Title of the bookmark.
     * @param url The website the bookmark is about.
     * @param author The author of the bookmark.
     * @param creator The creator of the bookmark.
     * @param tags Set of tag names.
     * @return Returns true if bookmark is successfully added.
     */
    public Bookmark createBookmark(String title, String url, String author, User creator, Set<Tag> tags) {

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(url)) {
            return null;
        }

        if (tags == null) {
            tags = new HashSet<>();
        }
        
        String id = generateBookmarkId();

        Bookmark bookmark = new Bookmark(id, title, url, author, creator, tags);

        bookmarkDao.add(bookmark);

        return bookmark;
    }

    public Collection<Bookmark> getAllBookmarks() {
        return bookmarkDao.findAll();
    }

    /**
     * Function for retrieving bookmarks with any of the tags specified as a set
     * of strings.
     *
     * @param stringTags set of tags as string
     * @return matching bookmarks
     */
    public Collection<Bookmark> getBookmarksByTags(Set<String> stringTags) {
        return bookmarkDao.findByTagSet(stringTags);
    }

    private String generateBookmarkId() {
        return UUID.randomUUID().toString();
    }
}
