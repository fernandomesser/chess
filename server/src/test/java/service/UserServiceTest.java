package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private static final UserDAO userDataAccess = new MemoryUserDAO();
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();
    static final UserService service = new UserService(userDataAccess, authDataAccess);

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
    }

    @Test
    void positiveRegister() throws ResponseException, DataAccessException {
        UserData user = new UserData("Jhon","1234","jhon@email.com");
        AuthData authData = service.register(user);
        assertEquals(user,userDataAccess.getUser("Jhon"));
        assertNotNull(authData);
    }

    @Test
    void negativeRegister() throws ResponseException, DataAccessException {
        UserData user = new UserData("","1234","jhon@email.com");
        assertThrows(ResponseException.class, () -> {
            service.register(user);
    });
    }

    @Test
    void positiveLogIn() throws ResponseException, DataAccessException {
        UserData user = new UserData("Jhon","1234","jhon@email.com");
        service.register(user);
        AuthData authData = service.logIn(user);
        assertNotNull(authData);
    }

    @Test
    void negativeLogIn() {
        UserData user = new UserData("","1234","jhon@email.com");
        assertThrows(ResponseException.class, () -> {
            service.logIn(user);
        });
    }

    @Test
    void positiveLogOut() throws ResponseException, DataAccessException {
        AuthData expected = service.register(new UserData("Jhon","1234","jhon@email.com"));
        assertEquals(authDataAccess.getAuth(expected.authToken()).authToken(),expected.authToken());
        service.logOut(expected.authToken());
        assertNull(authDataAccess.getAuth(expected.authToken()));
    }

    @Test
    void negativeLogOut() {
        assertThrows(ResponseException.class, () -> {
            service.logOut(null);
        });
        assertThrows(ResponseException.class, () -> {
            service.logOut("");
        });
    }

    @Test
    void clearTest() throws ResponseException, DataAccessException {
        service.register(new UserData("Jhon","1234","jhon@email.com"));
        service.register(new UserData("Joe","1234","joe@email.com"));
        service.register(new UserData("Julia","1234","julia@email.com"));
        service.clear();
        assertNull(userDataAccess.getUser("Jhon"));
        assertNull(userDataAccess.getUser("Joe"));
        assertNull(userDataAccess.getUser("Julia"));
        assertNull(authDataAccess.getAuth("Jhon"));
        assertNull(authDataAccess.getAuth("Joe"));
        assertNull(authDataAccess.getAuth("Julia"));
    }

}