package fi.pohina.vinkkilista;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        port(getPort());

        get("/", (req, res) -> {
            return "Hello world!";
        });
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }
}
