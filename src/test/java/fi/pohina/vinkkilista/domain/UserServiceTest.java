package fi.pohina.vinkkilista.domain;

import com.google.common.base.Strings;
import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.data_access.UserDao;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserDao mockUserDao;
    private UserService service;
    private GithubUser githubUser;

    @Before
    public void setUp() {
        mockUserDao = mock(UserDao.class);
        service = new UserService(mockUserDao);
        githubUser = new GithubUser();
    }

    @Test
    public void findByIdReturnsNullIfNotFound() {
        String id = "foobar";
        when(mockUserDao.findById(id)).thenReturn(null);

        User result = service.findById(id);

        assertNull(result);
    }

    @Test
    public void findByIdReturnsCorrectUserWhenIdIsFound() {
        String userId = "user-id";
        when(mockUserDao.findById(userId)).thenReturn(
            new User(userId, "foo@bar.com", "user", 123)
        );

        User result = service.findById(userId);

        assertEquals(userId, result.getId());
    }

    @Test
    public void findOrCreateByGithubUserFindsCorrectUser() {
        int githubUserId = 123;
        String githubUsername = "meitsi";
        githubUser = createGithubUser(githubUserId, githubUsername);
        String userId = "fooooo";

        when(mockUserDao.findByGithubId(123))
            .thenReturn(new User(
                    userId,
                    "foovar@com.com",
                    githubUsername,
                    githubUserId
                )
            );

        User result = service.findOrCreateByGithubUser(githubUser);

        assertEquals(userId, result.getId());
        assertEquals(githubUsername, result.getUsername());
    }

    @Test
    public void findOrCreateByGithubUserDoesNotCreateUserIfFound() {
        int githubUserId = 123;
        String githubUsername = "meitsi";
        githubUser = createGithubUser(githubUserId, githubUsername);

        when(mockUserDao.findByGithubId(123))
            .thenReturn(new User(
                    "id_1",
                    "foovar@com.com",
                    githubUsername,
                    githubUserId
                )
            );

        service.findOrCreateByGithubUser(githubUser);

        verify(mockUserDao, times(0)).add(any());
    }

    @Test
    public void findOrCreateByGithubUserCreatesUserIfNotFound() {
        int githubUserId = 123;
        String githubUsername = "meitsi";
        String githubEmail = "foo@john.com";
        githubUser = createGithubUser(
            githubUserId,
            githubUsername,
            githubEmail
        );

        when(mockUserDao.findByGithubId(anyInt()))
            .thenReturn(null);

        User user = service.findOrCreateByGithubUser(githubUser);

        verify(mockUserDao, times(1)).add(any());
        assertEquals(githubUserId, user.getGithubId());
        assertEquals(githubUsername, user.getUsername());
        assertEquals(githubEmail, user.getEmail());
    }

    @Test
    public void findOrCreateGithubUserGeneratesIdForUser() {
        githubUser = createGithubUser(123, "foobar");

        when(mockUserDao.findByGithubId(123))
            .thenReturn(null);

        User user = service.findOrCreateByGithubUser(githubUser);

        assertFalse(Strings.isNullOrEmpty(user.getId()));
    }

    @Test
    public void markingBookmarkAsReadWorksCorrectly() {
        String userId = "user-id";
        String bookmarkId = "bookmark-id";
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(
            String.class
        );
        ArgumentCaptor<String> bookmarkIdCaptor = ArgumentCaptor.forClass(
            String.class
        );

        service.markBookmarkAsRead(userId, bookmarkId);

        verify(mockUserDao, times(1)).addBookmarkReadDate(
            userIdCaptor.capture(),
            bookmarkIdCaptor.capture(),
            any(LocalDateTime.class)
        );

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(bookmarkId, bookmarkIdCaptor.getValue());
    }

    @Test
    public void markingBookmarkAsReadUsesCurrentTime() {
        String userId = "user-id";
        String bookmarkId = "bookmark-id";
        ArgumentCaptor<LocalDateTime> readDateCaptor = ArgumentCaptor.forClass(
            LocalDateTime.class
        );

        LocalDateTime timeBefore = LocalDateTime.now(ZoneOffset.UTC);
        service.markBookmarkAsRead(userId, bookmarkId);
        LocalDateTime timeAfter = LocalDateTime.now(ZoneOffset.UTC);

        verify(mockUserDao).addBookmarkReadDate(
            any(),
            any(),
            readDateCaptor.capture()
        );

        assertTrue(timeBefore.compareTo(readDateCaptor.getValue()) <= 0);
        assertTrue(timeAfter.compareTo(readDateCaptor.getValue()) >= 0);
    }

    @Test
    public void unmarkingBookmarkAsReadWorksCorrectly() {
        String userId = "user-id";
        String bookmarkId = "bookmark-id";
        ArgumentCaptor<String> userIdCaptor = ArgumentCaptor.forClass(
            String.class
        );
        ArgumentCaptor<String> bookmarkIdCaptor = ArgumentCaptor.forClass(
            String.class
        );

        service.unmarkBookmarkAsRead(userId, bookmarkId);

        verify(mockUserDao, times(1)).removeBookmarkReadDate(
            userIdCaptor.capture(),
            bookmarkIdCaptor.capture()
        );

        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(bookmarkId, bookmarkIdCaptor.getValue());
    }

    private static GithubUser createGithubUser(int id, String username) {
        return createGithubUser(id, username, "");
    }

    private static GithubUser createGithubUser(
        int id,
        String username,
        String email
    ) {
        GithubUser user = new GithubUser();
        user.setId(id);
        user.setLogin(username);
        user.setEmail(email);
        return user;
    }
}
