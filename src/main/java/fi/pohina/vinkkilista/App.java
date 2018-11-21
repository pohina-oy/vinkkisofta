package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.Blog;
import fi.pohina.vinkkilista.domain.BookmarkService;
import fi.pohina.vinkkilista.domain.BookmarkType;
import java.util.Collection;
import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import spark.QueryParamsMap;
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
            map.put("types", BookmarkType.values());
            return render(map, "new");
        });

        post("/new", (req, res) -> {
            String type = req.queryParams("type");
            boolean created = false;
            if (type.equals("Blog")) {
                created = newBlog(req.queryMap());
            }

            if (created) {
                res.redirect("/");
                return "New bookmark added";
            } else {
                res.redirect("/new");
                return "Bookmark could not be created";
            }
        });
    }

    /**
     * Validates parameters and creates blog.
     * Todo: Make a validator class.
     * @param params Contains the parameters from request.
     * @return Returns true if blog is added, otherwise false.
     */
    private boolean newBlog(QueryParamsMap params) {
        String title = params.get("title").value();
        String url = params.get("url").value();
        String author = params.get("author").value();

        if (title.isEmpty() || url.isEmpty()) {
            return false;
        }

        if (author.isEmpty()) {
            bookmarks.addBlog(new Blog(title, url));
        } else {
            bookmarks.addBlog(new Blog(title, url, author));
        }
        return true;
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
