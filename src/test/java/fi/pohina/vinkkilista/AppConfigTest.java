package fi.pohina.vinkkilista;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class AppConfigTest {
    private AppConfig config;

    @Test
    public void getGithubClientIdReturnsCorrectId() {
        config = new AppConfig("foobar", null, "production");

        String result = config.getGithubClientId();

        assertEquals("foobar", result);
    }

    @Test
    public void getGithubClientSecretReturnsCorrectSecret() {
        config = new AppConfig("foobar", "barfoo", null);

        String result = config.getGithubClientSecret();

        assertEquals("barfoo", result);
    }

    @Test
    public void getStageReturnsCorrectStage() {
        config = new AppConfig("githubClientId", "githubClientSecret", "test");

        String result = config.getStage();

        assertEquals("test", result);
    }
}
