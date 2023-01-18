import Server.Middlewares.AccessData;
import Server.Middlewares.MiddlewareRegister;
import Server.Middlewares.SessionManager;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MiddlewareTests {
    MiddlewareRegister middlewareRegister;
    SessionManager sessionManager;
    @Before
    public void setup() {
        middlewareRegister = new MiddlewareRegister();
        sessionManager = new SessionManager();
    }

    @Test
    public void testRegisterMiddleware() {
        middlewareRegister.register("session", sessionManager);
        assertNotNull(middlewareRegister.get("session"));
    }

    @Test
    public void testRegisterSession() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        assertTrue(sessionManager.register(token, domain, username));
        assertFalse(sessionManager.register(token, domain, username));
    }

    @Test
    public void testLogout() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        sessionManager.register(token, domain, username);
        String invalidToken = "invalidToken";
        assertTrue(sessionManager.logout(token));
        assertFalse(sessionManager.logout(invalidToken));
    }

    @Test
    public void testIsLoggedIn() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        sessionManager.register(token, domain, username);
        String invalidToken = "invalidToken";
        assertTrue(sessionManager.isLoggedIn(token));
        assertFalse(sessionManager.isLoggedIn(invalidToken));
    }

    @Test
    public void testHasAccess() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        sessionManager.register(token, domain, username);
        String invalidToken = "invalidToken";
        String invalidAccessDomain = "example.net";
        assertTrue(sessionManager.hasAccess(token, domain));
        assertFalse(sessionManager.hasAccess(token, invalidAccessDomain));
        assertFalse(sessionManager.hasAccess(invalidToken, domain));
    }

    @Test
    public void testSomeHasAccess() {
        String token = "token";
        String domain = "^example\\.(net|com)$";
        String username = "username";
        sessionManager.register(token, domain, username);
        String accessDomain1 = "example.com";
        String accessDomain2 = "example.net";
        String accessDomain3 = "example.pog";

        assertTrue(sessionManager.hasAccess(token, accessDomain1));
        assertTrue(sessionManager.hasAccess(token, accessDomain2));
        assertFalse(sessionManager.hasAccess(token, accessDomain3));

    }

    @Test
    public void testAnyHasAccess() {
        String token = "token";
        String domain = "\\S+";
        String username = "username";
        sessionManager.register(token, domain, username);
        String invalidToken = "invalidToken";
        String accessDomain1 = "example.com";
        String accessDomain2 = "example.net";
        String accessDomain3 = "example.pog";

        assertTrue(sessionManager.hasAccess(token, accessDomain1));
        assertTrue(sessionManager.hasAccess(token, accessDomain2));
        assertTrue(sessionManager.hasAccess(token, accessDomain3));
    }

    @Test
    public void testGetUsername() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        sessionManager.register(token, domain, username);
        String invalidToken = "invalidToken";
        assertEquals(username, sessionManager.getUsername(token));
        assertNull(sessionManager.getUsername(invalidToken));
    }

    @Test
    public void testUpdateAccessData() {
        String token = "token";
        String domain = "example.com";
        String username = "username";
        String newUsername = "newUsername";
        String newDomain = "example.net";
        sessionManager.register(token, domain, username);

        // set new domain and username
        sessionManager.getAccessData().get(token).setDomain(newDomain);
        sessionManager.getAccessData().get(token).setUsername(newUsername);

        AccessData accessData = sessionManager.getAccessData().get(token);
        assertEquals(newDomain, accessData.getDomain());
        assertEquals(newUsername, accessData.getUsername());
    }

    @Test
    public void testEmptyToken() {
        assertFalse(sessionManager.isLoggedIn(""));
    }

    @Test
    public void testNullToken() {
        assertFalse(sessionManager.isLoggedIn(null));
    }

    @Test
    public void testInvalidToken() {
        assertFalse(sessionManager.isLoggedIn("invalid_token"));
    }


}