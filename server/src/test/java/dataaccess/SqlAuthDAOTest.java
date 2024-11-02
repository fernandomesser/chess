package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    void getAuth() {
    }
    @Test
    void negativeGetAuth() {
    }

    @Test
    void deleteAuth() {
    }
    @Test
    void negativeDeleteAuth() {
    }

    @Test
    void clearAuth() {
    }
}