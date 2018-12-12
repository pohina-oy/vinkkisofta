package fi.pohina.vinkkilista;

import fi.pohina.vinkkilista.domain.User;
import fi.pohina.vinkkilista.domain.UserService;
import org.junit.Before;
import org.junit.Test;
import spark.Request;
import spark.Session;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RequestUserManagerTest {

    private UserService mockUserService;
    private Request mockRequest;
    private Session mockSession;
    private RequestUserManager manager;
    private User user;

    @Before
    public void setUp() {
        mockUserService = mock(UserService.class);
        mockRequest = mock(Request.class);
        mockSession = mock(Session.class);
        // return the mock session when a read-only session is requested
        when(mockRequest.session())
            .thenReturn(mockSession);
        when(mockRequest.session(anyBoolean()))
            .thenReturn(mockSession);

        manager = new RequestUserManager(mockUserService);

        user = new User(
            "id123",
            "foo@bar.com",
            "cxcorp",
            123
        );
    }

    @Test
    public void getSignedInUserFetchesCorrectUserFromService() {
        when(mockSession.attribute(anyString()))
            .thenReturn(user.getId());
        when(mockUserService.findById(user.getId()))
            .thenReturn(user);

        User result = manager.getSignedInUser(mockRequest);

        assertEquals(user, result);
        verify(mockUserService, times(1))
            .findById(user.getId());
    }

    @Test
    public void getSignedInUserReturnsNullIfUserIdNotFoundFromSession() {
        when(mockSession.attribute(anyString()))
            .thenReturn(null);

        User result = manager.getSignedInUser(mockRequest);

        assertNull(result);
    }

    @Test
    public void getSignedInUserReturnsNullIfUserIdIsNotFoundFromService() {
        when(mockSession.attribute(anyString()))
            .thenReturn(user.getId());
        when(mockUserService.findById(user.getId()))
            .thenReturn(null);

        User result = manager.getSignedInUser(mockRequest);

        assertNull(result);
    }

    @Test
    public void setSignedInUserSetsUserIdToSession() {
        manager.setSignedInUser(mockRequest, user);

        verify(mockSession, times(1))
            .attribute(anyString(), eq(user.getId()));
    }
}
