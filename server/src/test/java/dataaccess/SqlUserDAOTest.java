package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDAOTest {
    private UserDAO userDataAccess = new SqlUserDAO();
    @BeforeEach
    void clear() throws DataAccessException {
        userDataAccess.clearUsers();
    }
    @Test
    void insertUser() throws DataAccessException, SQLException {
        UserData user = new UserData("John", "1234", "john@email.com");
        userDataAccess.insertUser(user);
        UserData userResult = userDataAccess.getUser("John");
        assertNotNull(userResult);
        assertEquals(user.username(), userResult.username());
        assertEquals(user.password(), userResult.password());
        assertEquals(user.email(), userResult.email());
    }
    @Test
    void negativeInsertUser() {
        UserData user = new UserData(null,null,null);
        assertThrows(DataAccessException.class, () -> {
            userDataAccess.insertUser(user);
        });
    }

    @Test
    void getUser() throws DataAccessException, SQLException {
        UserData user = new UserData("John", "1234", "john@email.com");
        userDataAccess.insertUser(user);
        UserData userResult = userDataAccess.getUser("John");
    }
    @Test
    void negativeGetUser() {
    }

    @Test
    void clearUsers() {
    }
}