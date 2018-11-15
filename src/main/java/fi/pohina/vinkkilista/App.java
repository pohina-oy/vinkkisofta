package fi.pohina.vinkkilista;

import spark.ModelAndView;
import java.util.HashMap;
import java.util.Map;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(getPort());

        get("/", (req, res) -> {
            Map<String, Object> map = new HashMap<>();
            return render(map, "index");
        });
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
