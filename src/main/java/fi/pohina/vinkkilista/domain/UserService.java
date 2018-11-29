package fi.pohina.vinkkilista.domain;

import java.util.*;

public class UserService {

    private List<User> users = new ArrayList<>();

    public User findOrCreateByGithubUser(GithubUser githubUser) {
        User user = findUserByGithubId(githubUser.getId());
        if (user != null) {
            System.out.println("UserService::findOrCreateByGithubUser found user " + user);
            return user;
        }

        return createUserFromGithubUser(githubUser);
    }

    private User findUserByGithubId(int githubId) {
        return users
            .stream()
            .filter(user -> user.getGithubId() == githubId)
            .findFirst()
            .orElse(null);
    }

    private User createUserFromGithubUser(GithubUser githubUser) {
        String userId = UUID.randomUUID().toString();
        User user = new User(
            userId,
            githubUser.getEmail(),
            githubUser.getLogin(),
            githubUser.getId()
        );

        this.users.add(user);

        System.out.println("UserService::createUserFromGithubUser created user " + user);
        return user;
    }

    public User findById(String userId) {
        return users.stream()
            .filter(user -> user.getId().equals(userId))
            .findFirst()
            .orElse(null);
    }
}
