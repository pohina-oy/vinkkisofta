package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import spark.Request;
import spark.Session;

/**
 * Provides user login status management with sessions. Essentially extension
 * methods to use with {@link Request Requests}.
 */
class RequestUserManager {

    private static final String SESSION_ATTRIBUTE_NAME_USERID = "github-user";

    private final UserService userService;

    RequestUserManager(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the logged in user associated with the request. This method
     * fetches the user's ID from the request's session (cookies), then finds
     * the user from the {@link UserService}.
     *
     * @param req the Spark request whose user to retrieve.
     * @return the signed in {@link User} making the request, or <c>null</c>.
     */
    User getSignedInUser(Request req) {
        String userId = getUserIdFromCookie(req.session());
        if (userId == null) {
            return null;
        }
        return userService.findById(userId);
    }

    /**
     * Associates the specified {@link User} to the request by assigning
     * the user's ID to the request's session (cookie).
     *
     * @param req  the request with which the session will be created.
     * @param user the user to sign in.
     */
    void setSignedInUser(Request req, User user) {
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
