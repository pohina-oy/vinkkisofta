package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.Blog;
import fi.pohina.vinkkilista.domain.BookmarkService;
import java.util.Collection;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import static spark.Spark.*;

public class App {
    private final BookmarkService bookmarks;

    public static void main(String[] args) {
        BookmarkService service = new BookmarkService();
        new App(service).startServer();
    }

    public App(BookmarkService bookmarks) {
        this.bookmarks = bookmarks;
    }

    /**
     * Configures and ignites the Spark server.
     */
    public void startServer() {
        port(getPort());

        get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Collection<Blog> blogs = this.bookmarks.getBlogs();
            map.put("blogs", blogs);
            return render(map, "index");
        });
    }

    /**
     * Stops the Spark server.
     */
    public void stopServer() {
        stop();
    }

    private static String render(
        Map<String, Object> model,
        String templatePath
    ) {
        return new ThymeleafTemplateEngine().render(
            new ModelAndView(model, templatePath)
        );
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }
}
