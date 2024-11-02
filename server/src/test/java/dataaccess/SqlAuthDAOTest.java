package dataaccess;

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
    void createAuth() {
    }
    @Test
    void negativeCreateAuth() {
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