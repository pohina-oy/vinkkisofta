package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.User;
import java.time.LocalDateTime;
import java.util.*;

public interface UserDao {

    /**
     * Finds and returns a User by the specified ID, or <c>null</c> if not
     * found.
     *
     * @param id the id of the user.
     * @return the user if found, otherwise <c>null</c>.
     */
    User findById(String id);

    /**
     * Finds and returns a saved user by the specified GithubID, if found,
     * otherwise returns <c>null</c>.
     *
     * @param githubId the GithubID of the user.
     * @return the user if found, otherwise <c>null</c>.
     */
    User findByGithubId(int githubId);

    /**
     * Get all saved users as a list.
     *
     * @return the list of users in the DAO.
     */
    List<User> findAll();

    /**
     * Add a new specified user to the DAO.
     *
     * @param user new user to be saved.
     */
    void add(User user);

    /**
     * Adds date a specified bookmark has been read for a specified user to
     * the DAO.
     *
     * @param userId the id of the user.
     * @param bookmarkId the id of the bookmark.
     * @param dateRead the time to annotate bookmark as read.
     */
    void addBookmarkReadDate(String userId, String bookmarkId, LocalDateTime dateRead);

    /**
     * Removes the date read annotations from a specified bookmark for a
     * specified user from the DAO.
     *
     * @param userId the id of the user.
     * @param bookmarkId the id of the bookmark.
     */
    void removeBookmarkReadDate(String userId, String bookmarkId);
}
