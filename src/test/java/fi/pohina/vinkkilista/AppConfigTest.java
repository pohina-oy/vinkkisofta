package fi.pohina.vinkkilista;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppConfigTest {
    private AppConfig config;

    @Test
    public void getGithubClientIdReturnsCorrectId() {
        config = new AppConfig("foobar", null, null);

        String result = config.getGithubClientId();

        assertEquals("foobar", result);
    }

    @Test
    public void getGithubClientSecretReturnsCorrectSecret() {
        config = new AppConfig("foobar", "barfoo", null);

        String result = config.getGithubClientSecret();

        assertEquals("barfoo", result);
    }
}
