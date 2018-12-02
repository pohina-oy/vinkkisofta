package fi.pohina.vinkkilista.domain;

import org.junit.Test;
import static org.junit.Assert.*;

public class UserTest {

    @Test
    public void constructorSetsIdCorrectly() {
        User user = new User(
            "foobar",
            null,
            null,
            0
        );

        String result = user.getId();

        assertEquals("foobar", result);
    }

    @Test
    public void constructorSetsEmailCorrectly() {
        User user = new User(
            "foobar",
            "john.bar@com.com",
            null,
            0
        );

        String result = user.getEmail();

        assertEquals("john.bar@com.com", result);
    }

    @Test
    public void constructorSetsUsernameCorrectly() {
        User user = new User(
            "foobar",
            "john.bar@com.com",
            "toasterbox",
            0
        );

        String result = user.getUsername();

        assertEquals("toasterbox", result);
    }

    @Test
    public void constructorSetsGithubIdCorrectly() {
        User user = new User(
            "foobar",
            "john.bar@com.com",
            "toasterbox",
            123
        );

        int result = user.getGithubId();

        assertEquals(123, result);
    }
}
