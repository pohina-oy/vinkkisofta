package fi.pohina.vinkkilista;

import org.junit.rules.ExternalResource;
import spark.Spark;

public class ServerRule extends ExternalResource {

    @Override
    protected void before() throws Throwable {
        Main.main(null);
    }

    @Override
    protected void after() {
        Spark.stop();
    }
    
}
