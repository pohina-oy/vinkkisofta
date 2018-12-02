package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.User;
import java.util.*;

/**
 * Provides an in-memory implementation of the {@link UserDao} interface,
 * backed by a {@link List<User>}.
 */
public class InMemoryUserDao implements UserDao {

    private final List<User> usersDB;

    public InMemoryUserDao() {
        this(new ArrayList<>());
    }

    public InMemoryUserDao(List<User> initialUsers) {
        this.usersDB = initialUsers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findById(String id) {
        for (User user : usersDB) {
            if (user.getId().equals(id)) {
                return user;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User findByGithubId(int githubId) {
        for (User user : usersDB) {
            if (user.getGithubId() == githubId) {
                return user;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> findAll() {
        // return a new ArrayList so that the consumer cannot change our
        // internal copy of the bookmark list
        return new ArrayList<>(usersDB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(User user) {
        usersDB.add(user);
    }
}
