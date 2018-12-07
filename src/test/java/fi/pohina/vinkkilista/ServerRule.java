package fi.pohina.vinkkilista;

import org.junit.rules.ExternalResource;
import spark.Spark;

public class ServerRule extends ExternalResource {

    private App app;

    @Override
    protected void before() throws Throwable {
        app = Main.start();
    }

    @Override
    protected void after() {
        Spark.stop();
    }

    App getApp() {
        return app;
    }
}
