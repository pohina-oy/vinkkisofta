package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.api.GithubUser;
import fi.pohina.vinkkilista.data_access.UserDao;
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