package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlAuthDAOTest {
    private AuthDAO dataAccess = new SqlAuthDAO();
    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clearAuth();
    }

    @Test
    void createAuth() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("John");
        assertNotNull(auth);
    }
    @Test
    void negativeCreateAuth() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("");
        assertNotNull(auth);
    }

    @Test
    void getAuth() throws DataAccessException, SQLException {
        AuthData auth = dataAccess.createAuth("John");
        AuthData authResult = dataAccess.getAuth(auth.authToken());
        assertNotNull(authResult);
        assertEquals(auth.authToken(),authResult.authToken());
        assertEquals(auth.username(),authResult.username());
    }
    @Test
    void negativeGetAuth() throws SQLException, DataAccessException {
        assertNull(dataAccess.getAuth(""));
    }

    @Test
    void deleteAuth() throws DataAccessException, SQLException {
        AuthData auth = dataAccess.createAuth("John");
        assertNotNull(auth);
        dataAccess.deleteAuth(auth.authToken());
        assertNull(dataAccess.getAuth(auth.authToken()));
    }
    @Test
    void negativeDeleteAuth() throws DataAccessException {
        AuthData auth = dataAccess.createAuth("John");
        assertDoesNotThrow(() -> {
            dataAccess.deleteAuth("Joe");
        });
        assertNotNull(auth);
    }

    @Test
    void clearAuth() throws DataAccessException, SQLException {
        AuthData auth1 = dataAccess.createAuth("John");
        AuthData auth2 = dataAccess.createAuth("Joe");
        AuthData auth3 = dataAccess.createAuth("Luke");
        dataAccess.clearAuth();
        assertNull(dataAccess.getAuth(auth1.authToken()));
        assertNull(dataAccess.getAuth(auth2.authToken()));
        assertNull(dataAccess.getAuth(auth3.authToken()));
    }
}