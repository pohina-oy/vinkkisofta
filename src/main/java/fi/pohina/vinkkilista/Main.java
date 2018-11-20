package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.BookmarkService;

public class Main {
    public static void main(String[] args) {
        BookmarkService service = new BookmarkService();
        int port = getPort();

        new App(service).startServer(port);
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        return port == null ? 4567 : Integer.parseInt(port);
    }
}
