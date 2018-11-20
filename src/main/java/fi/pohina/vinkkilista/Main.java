package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.BookmarkService;

public class Main {
    public static void main(String[] args) {
        BookmarkService service = new BookmarkService();
        new App(service).startServer();
    }
}
