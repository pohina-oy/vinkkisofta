package fi.pohina.vinkkilista.api;

import org.junit.Before;
import org.junit.Test;
import static fi.pohina.vinkkilista.api.Utils.testSetterAndGetter;

public class GithubEmailTest {

    private GithubEmail email;

    @Before
    public void setUp() {
        email = new GithubEmail();
    }

    @Test
    public void getEmailReturnsCorrectEmail() {
        testSetterAndGetter(
            "foo.bar@com.com",
            email::setEmail,
            email::getEmail
        );
    }

    @Test
    public void isPrimaryReturnsTrueCorrectly() {
        testSetterAndGetter(
            true,
            email::setPrimary,
            email::isPrimary
        );
    }

    @Test
    public void isPrimaryReturnsFalseCorrectly() {
        testSetterAndGetter(
            false,
            email::setPrimary,
            email::isPrimary
        );
    }
}
