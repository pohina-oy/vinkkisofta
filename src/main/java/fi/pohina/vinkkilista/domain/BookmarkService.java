package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
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
        String author,
        Set<String> tags
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
}
