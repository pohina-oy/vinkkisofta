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

    public App(BookmarkService bookmarks) {
        this.bookmarks = bookmarks;
    }

    /**
     * Configures and ignites the Spark server, binding to the specified port.
     *
     * @param portNumber The port to which the HTTP server will be bound to.
     */
    public void startServer(int portNumber) {
        staticFileLocation("/static");
        port(portNumber);

        get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            Collection<Blog> blogs = this.bookmarks.getBlogs();
            map.put("blogs", blogs);
            return render(map, "index");
        });
        
        get("/new", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return render(map, "new");
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
}
