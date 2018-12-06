package fi.pohina.vinkkilista.data_access;

import java.util.ArrayList;
import fi.pohina.vinkkilista.domain.User;
import java.time.LocalDateTime;
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
        // internal copy of the user list
        return new ArrayList<>(usersDB);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(User user) {
        usersDB.add(user);
    }

    @Override
    public void addBookmarkReadDate(String userId, String bookmarkId, LocalDateTime dateRead) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeBookmarkReadDate(String userId, String bookmarkId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
