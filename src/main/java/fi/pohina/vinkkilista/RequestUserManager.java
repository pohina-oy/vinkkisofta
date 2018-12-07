package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import spark.Request;
import spark.Session;

public class RequestUserManager {

    private static final String SESSION_ATTRIBUTE_NAME_USERID = "github-user";

    private final UserService userService;

    public RequestUserManager(UserService userService) {
        this.userService = userService;
    }

    public User getSignedInUser(Request req) {
        String userId = getUserIdFromCookie(req.session());
        if (userId == null) {
            return null;
        }
        return userService.findById(userId);
    }

    public void setSignedInUser(Request req, User user) {
        Session createdSession = req.session(true);
        setUserIdToCookie(createdSession, user.getId());
    }

    private String getUserIdFromCookie(Session session) {
        return session.attribute(SESSION_ATTRIBUTE_NAME_USERID);
    }

    private void setUserIdToCookie(Session session, String userId) {
        session.attribute(SESSION_ATTRIBUTE_NAME_USERID, userId);
    }
}
