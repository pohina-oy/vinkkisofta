package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import java.util.Collection;

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

    public Bookmark addTagByUrl(Bookmark bookmark) {
        String tag = getTagByUrl(bookmark.getUrl());
        if (tag != null) {
            bookmark.addTag(tag); // Tags saved as a set?
        }
        return bookmark;
    }

    public Tag getTagByUrl(String url) {
        String[] videoUrls = {"youtube.com","vimeo.com"};
        String[] blogUrls = {"blogger.com","blogs.helsinki.fi","wordpress.org"};
        String[] bookUrls = {null}; // Missing book related URLs
        String[] scienceUrls = {"dl.acm.org","ieeexplore.ieee.org"};

        for (String videoUrl : videoUrls) {
            if (url.contains(videoUrl)) {
                return new Tag(,"Video");
            }
        }

        for (String blogUrl : blogUrls) {
            if (url.contains(blogUrl)) {
                return new Tag(,"Blog");
            }
        }

        for (String bookUrl : bookUrls) {
            if (url.contains(bookUrl)) {
                return new Tag(,"Book");
            }
        }

        for (String scienceUrl : scienceUrls) {
            if (url.contains(scienceUrl)) {
                return new Tag(,"Scientific Publication");
            }
        }

        return null;
    }
}
