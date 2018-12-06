package fi.pohina.vinkkilista.domain;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        User user = prepareTestUser();

        int result = user.getGithubId();

        assertEquals(123, result);
    }

    @Test
    public void setBookmarkReadDateWorksCorrectly() {
        User user = prepareTestUser();
        String bookmarkId = "bookmark-123";
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        user.setBookmarkReadStatus(bookmarkId, dateRead);
        LocalDateTime result = user.getBookmarkReadStatus(bookmarkId);

        assertEquals(dateRead, result);
    }

    @Test
    public void removeBookmarkReadDateWorksCorrectly() {
        User user = prepareTestUser();
        String bookmarkId = "bookmark-123";
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        user.setBookmarkReadStatus(bookmarkId, dateRead);
        LocalDateTime result = user.getBookmarkReadStatus(bookmarkId);

        assertEquals(dateRead, result);

        user.removeBookmarkReadStatus(bookmarkId);
        result = user.getBookmarkReadStatus(bookmarkId);

        assertEquals(null, result);
    }

    private User prepareTestUser() {
        return new User(
            "foobar",
            "john.bar@com.com",
            "toasterbox",
            123
        );
    }
}
