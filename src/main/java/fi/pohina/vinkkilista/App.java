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
        return 4567;
    }
}
