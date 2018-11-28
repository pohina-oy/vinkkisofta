package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.BookmarkService;
import java.util.*;
import spark.ModelAndView;
import spark.QueryParamsMap;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import com.google.common.base.Strings;
import static spark.Spark.*;


public class App {

    private final CommaSeparatedTagsParser tagParser
        = new CommaSeparatedTagsParser();

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
            Collection<Bookmark> bookmarks = this.bookmarks.getAllBookmarks();
            map.put("bookmarks", bookmarks);
            return render(map, "index");
        });

        get("/new", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return render(map, "new");
        });

        post("/new", (req, res) -> {
            if (validateAndCreateBookmark(req.queryMap())) {
                res.redirect("/");
                return "New bookmark added";
            } else {
                res.redirect("/new");
                return "Bookmark could not be created";
            }
        });
    }

    /**
     * Validates parameters and creates a bookmark.
     * Todo: Make a validator class.
     *
     * @param params the query parameters of the request.
     * @return <c>true</c> if the bookmark is successfully created,
     * otherwise <c>false</c>.
     */
    private boolean validateAndCreateBookmark(QueryParamsMap params) {
        String title = params.get("title").value();
        String url = params.get("url").value();
        String author = params.get("author").value();
        String commaSeparatedTags = params.get("tags").value();

        if (Strings.isNullOrEmpty(title) || Strings.isNullOrEmpty(url)) {
            return false;
        }

        Set<String> tags = tagParser.parse(commaSeparatedTags);

        bookmarks.createBookmark(
            title,
            url,
            author,
            tags
        );

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
