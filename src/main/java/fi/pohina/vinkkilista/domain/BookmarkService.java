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
}
