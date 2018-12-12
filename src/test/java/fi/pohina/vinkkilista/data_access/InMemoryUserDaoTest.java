package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.User;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class InMemoryUserDaoTest {

    private InMemoryUserDao userDao;

    @Before
    public void setUp() {
        userDao = new InMemoryUserDao();

        User first = new User(
            "first-id",
            "first.person@some.site",
            "first",
            12345
        );

        User second = new User(
            "second-id",
            "bookwormss@other.site",
            "bookworm",
            21212
        );

        userDao.add(first);
        userDao.add(second);
    }

    @Test
    public void findByIdFindsInitiallyAddedUserById() {
        String expectedId = "first-id";

        User foundUser = userDao.findById(expectedId);

        assertEquals(expectedId, foundUser.getId());
    }

    @Test
    public void findByIdDoesNotFindNonExistentId() {
        User foundUser = userDao.findById("intellij-is-cool");

        assertNull(foundUser);
    }

    @Test
    public void findByGithubIdFindsInitiallyAddedUserById() {
        String expectedId = "second-id";
        int expectedGithubId = 21212;

        User foundUser = userDao.findByGithubId(expectedGithubId);

        assertEquals(expectedId, foundUser.getId());
        assertEquals(expectedGithubId, foundUser.getGithubId());
    }

    @Test
    public void findByGithubIdDoesNotFindNonExistentId() {
        User foundUser = userDao.findByGithubId(00100);

        assertNull(foundUser);
    }

    @Test
    public void findAllCorrectlyFindsInitiallyAddedUsers() {
        List<String> foundIds = userDao.findAll()
            .stream()
            .map(User::getId)
            .collect(Collectors.toList());

        assertEquals(2, foundIds.size());
        assertThat(foundIds, hasItems("first-id", "second-id"));
    }

    @Test
    public void findAllFindsPreviousUsersAfterAdd() {
        String newId = "new-123";
        User newEntry = new User(
            newId,
            "firstname.lastname@uni.org",
            "firstname",
            33333
        );

        userDao.add(newEntry);

        List<String> userIds = userDao.findAll()
            .stream()
            .map(User::getId)
            .collect(Collectors.toList());

        assertThat(userIds, hasItem("first-id"));
        assertThat(userIds, hasItem("second-id"));
        assertThat(userIds, hasItem(newId));
    }

    @Test
    public void addCorrectlyAddsNewUser() {
        String addedId = "user-3";
        User newEntry = new User(
            addedId,
            "ab5@soft.com",
            "ab",
            45654
        );

        userDao.add(newEntry);

        assertEquals(addedId, userDao.findById(addedId).getId());
        assertThat(userDao.findAll(), hasItem(newEntry));
    }

    @Test
    public void newUserDoesNotHaveBookmarkRead() {
        String addedId = "user-3";
        String bookmarkId = "bookmark-123";
        User newEntry = new User(
            addedId,
            "ab5@soft.com",
            "ab",
            45654
        );

        userDao.add(newEntry);

        LocalDateTime result = userDao.findById(addedId)
            .getBookmarkReadStatus(bookmarkId);

        assertEquals(null, result);
    }

    @Test
    public void addBookmarkReadDateCorrectlyAddsReadTime() {
        String userId = "first-id";
        String bookmarkId = "bookmark-123";
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        userDao.addBookmarkReadDate(userId, bookmarkId, dateRead);

        assertTrue(dateRead.equals(
            userDao.findById(userId).getBookmarkReadStatus(bookmarkId)
        ));
    }

    @Test
    public void addBookmarkReadDateIgnoresNonexistentUser() {
        String userId = "new-user";
        String bookmarkId = "bookmark-123";
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        userDao.addBookmarkReadDate(userId, bookmarkId, dateRead);
    }

    @Test
    public void removeBookmarkReadDateWorksCorrectly() {
        String userId = "first-id";
        String bookmarkId = "bookmark-123";
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        assertEquals(
            null,
            userDao.findById(userId).getBookmarkReadStatus(bookmarkId)
        );

        userDao.addBookmarkReadDate(userId, bookmarkId, dateRead);

        assertTrue(dateRead.equals(
            userDao.findById(userId).getBookmarkReadStatus(bookmarkId)
        ));

        userDao.removeBookmarkReadDate(userId, bookmarkId);

        assertEquals(
            null,
            userDao.findById(userId).getBookmarkReadStatus(bookmarkId)
        );
    }

    @Test
    public void removeBookmarkReadDateIgnoresNonexistentUser() {
        String userId = "new-user";
        String bookmarkId = "bookmark-123";

        userDao.removeBookmarkReadDate(userId, bookmarkId);
    }
}
