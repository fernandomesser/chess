package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;

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
    void clearUser() throws ResponseException, DataAccessException {

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
        
    }

    @Test
    void positiveLogOut() {
    }

    @Test
    void negativeLogOut() {
    }

}