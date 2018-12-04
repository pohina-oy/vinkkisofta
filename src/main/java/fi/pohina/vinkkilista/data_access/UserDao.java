package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.User;
<<<<<<< HEAD
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
}
=======

public interface UserDao {

    User findUserById(String id);

    User findUserByGithubId(int githubId);
}
>>>>>>> f4c82ca... Add initial user dao and in-memory implementation
