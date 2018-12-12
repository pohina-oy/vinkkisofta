package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.data_access.UserDao;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public User findOrCreateByGithubUser(GithubUser githubUser) {
        User user = userDao.findByGithubId(githubUser.getId());
        if (user != null) {
            return user;
        }

        return createUserFromGithubUser(githubUser);
    }

    public User findById(String id) {
        return userDao.findById(id);
    }

    /**
     * Marks a specified bookmark for a specified user as read with current
     * date time.
     *
     * @param userId id of user for whom a bookmark is to be marked
     * @param bookmarkId id of bookmark to be marked
     */
    public void markBookmarkAsRead(String userId, String bookmarkId) {
        LocalDateTime dateRead = LocalDateTime.now(ZoneOffset.UTC);

        userDao.addBookmarkReadDate(userId, bookmarkId, dateRead);
    }

    /**
     * Clears a specified bookmark's read status for a specified user.
     *
     * @param userId id of user for whom a bookmark is to be unmarked
     * @param bookmarkId id of bookmark to be unmarked
     */
    public void unmarkBookmarkAsRead(String userId, String bookmarkId) {
        userDao.removeBookmarkReadDate(userId, bookmarkId);
    }

    private User createUserFromGithubUser(GithubUser githubUser) {
        User user = new User(
            generateUserId(),
            githubUser.getEmail(),
            githubUser.getLogin(),
            githubUser.getId()
        );

        userDao.add(user);
        return user;
    }

    private static String generateUserId() {
        return UUID.randomUUID().toString();
    }
}