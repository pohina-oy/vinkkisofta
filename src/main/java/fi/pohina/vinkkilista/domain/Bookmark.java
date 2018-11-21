package fi.pohina.vinkkilista.domain;

/**
 *
 * @author porrasm
 */
public interface Bookmark {
    BookmarkType getBookmarkType();
    String getTitle();
    int getID();
}
