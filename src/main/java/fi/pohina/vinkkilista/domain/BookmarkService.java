package fi.pohina.vinkkilista.domain;

import java.util.ArrayList;
import java.util.Collection;

public class BookmarkService {
    
    private Collection<Blog> blogs;
    
    public BookmarkService() {
        this.blogs = new ArrayList<>();
        blogs.add(new Blog(
            "GitHub Blog",
            "https://blog.github.com",
            "GitHub")
        );
        blogs.add(new Blog(
            "Domain Driven Design Weekly",
            "http://dddweekly.com"
        ));
        blogs.add(new Blog(
            "the morning paper",
            "https://blog.acolyer.org",
            "Adrian Colyer"
        ));
    }
    
    public void addBlog(Blog blog) {
        this.blogs.add(blog);
    }
    
    public Collection<Blog> getBlogs() {
        return this.blogs;
    }
}
