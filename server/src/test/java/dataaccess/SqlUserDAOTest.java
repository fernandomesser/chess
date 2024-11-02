package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlUserDAOTest {
    private UserDAO dataAccess = new SqlUserDAO();
    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clearUsers();
    }
    @Test
    void insertUser() throws DataAccessException, SQLException {
        UserData user = new UserData("John", "1234", "john@email.com");
        dataAccess.insertUser(user);
        UserData userResult = dataAccess.getUser("John");
        assertNotNull(userResult);
        assertEquals(user.username(), userResult.username());
        assertEquals(user.password(), userResult.password());
        assertEquals(user.email(), userResult.email());
    }
    @Test
    void negativeInsertUser() {
        UserData user = new UserData(null,null,null);
        assertThrows(DataAccessException.class, () -> {
            dataAccess.insertUser(user);
        });
    }

    @Test
    void getUser() throws DataAccessException, SQLException {
        UserData user = new UserData("John", "1234", "john@email.com");
        dataAccess.insertUser(user);
        UserData userResult = dataAccess.getUser("John");
        assertNotNull(userResult);
        assertEquals(user.username(), userResult.username());
        assertEquals(user.password(), userResult.password());
        assertEquals(user.email(), userResult.email());
    }
    @Test
    void negativeGetUser() throws SQLException, DataAccessException {
        assertNull(dataAccess.getUser("John"));
    }

    @Test
    void clearUsers() throws DataAccessException, SQLException {
        dataAccess.insertUser(new UserData("Jhon", "1234", "jhon@email.com"));
        dataAccess.insertUser(new UserData("Joe", "1234", "joe@email.com"));
        dataAccess.insertUser(new UserData("Julia", "1234", "julia@email.com"));
        dataAccess.clearUsers();
        assertNull(dataAccess.getUser("Jhon"));
        assertNull(dataAccess.getUser("Joe"));
        assertNull(dataAccess.getUser("Julia"));
    }
}