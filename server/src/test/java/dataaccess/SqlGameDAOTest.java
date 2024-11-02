package dataaccess;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlGameDAOTest {

    private GameDAO dataAccess = new SqlGameDAO();
    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clearGames();
    }

    @Test
    void createGame() throws DataAccessException, SQLException {
        GameData game = new GameData(0, null, null, "Game", null);
        int id = dataAccess.createGame(game);
        GameData gameResult = dataAccess.getGame(id);
        assertNotNull(gameResult);
        assertEquals(dataAccess.getGame(id).gameName(), "Game");
    }
    @Test
    void negativeCreateGame() {
        GameData game = new GameData(0,null,null, null, null);
        assertThrows(DataAccessException.class, () -> {
            dataAccess.createGame(game);
        });
    }

    @Test
    void getGame() {
    }
    @Test
    void negativeGetGame() {
    }

    @Test
    void listGames() {
    }
    @Test
    void negativeListGames() {
    }

    @Test
    void clearGames() {
    }

    @Test
    void updateGame() {
    }
    @Test
    void negativeUpdateGame() {
    }
}