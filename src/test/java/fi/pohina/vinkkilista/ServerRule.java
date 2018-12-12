package fi.pohina.vinkkilista;

import org.junit.rules.ExternalResource;

public class ServerRule extends ExternalResource {

    private App app;

    @Override
    protected void before() throws Throwable {
        app = Main.start();
    }

    @Override
    protected void after() {
        app.stopServer();
    }

    App getApp() {
        return app;
    }
}
