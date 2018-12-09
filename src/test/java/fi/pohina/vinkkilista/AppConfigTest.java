package fi.pohina.vinkkilista;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void isProductionReturnsTrueWhenStageIsProd() {
        config = new AppConfig("githubClientId", "githubClientSecret", "production");
        assertTrue(config.isProduction());
    }

    @Test
    public void isProductionReturnsTrueWhenStageIsNullOrEmpty() {
        config = new AppConfig("githubClientId", "githubClientSecret", null);
        assertFalse(config.isProduction());
        config = new AppConfig("githubClientId", "githubClientSecret", "");
        assertFalse(config.isProduction());
    }

    @Test
    public void isProductionReturnsTrueWhenStageIsDevelopmentOrTest() {
        config = new AppConfig("githubClientId", "githubClientSecret", "test");
        assertFalse(config.isProduction());
        config = new AppConfig("githubClientId", "githubClientSecret", "development");
        assertFalse(config.isProduction());
    }
}
