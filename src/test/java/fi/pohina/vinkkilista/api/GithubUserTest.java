package fi.pohina.vinkkilista.api;

import org.junit.Before;
import org.junit.Test;
import static fi.pohina.vinkkilista.api.Utils.testSetterAndGetter;

public class GithubUserTest {

    private GithubUser user;

    @Before
    public void setUp() {
        user = new GithubUser();
    }

    @Test
    public void getLoginGetsCorrectLogin() {
        testSetterAndGetter(
            "cxcorp",
            user::setLogin,
            user::getLogin
        );
    }

    @Test
    public void getIdGetsCorrectId() {
        testSetterAndGetter(
            1234,
            user::setId,
            user::getId
        );
    }

    @Test
    public void getEmailGetsCorrectEmail() {
        testSetterAndGetter(
            "foo.bar@com.com",
            user::setEmail,
            user::getEmail
        );
    }
}
