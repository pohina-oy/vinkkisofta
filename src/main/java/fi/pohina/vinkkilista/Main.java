package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.data_access.BookmarkDao;
import fi.pohina.vinkkilista.data_access.InMemoryBookmarkDao;
import fi.pohina.vinkkilista.domain.Blog;
import fi.pohina.vinkkilista.domain.BookmarkService;

public class Main {
    public static void main(String[] args) {
        BookmarkDao dao = new InMemoryBookmarkDao();
        addMockBlogs(dao);
        BookmarkService service = new BookmarkService(dao);
        int port = getPort();

        new App(service).startServer(port);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }

    private static void addMockBlogs(BookmarkDao blogs) {
        blogs.add(new Blog(
            "GitHub Blog",
            "https://blog.github.com",
            "GitHub"
        ));
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
}
