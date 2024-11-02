package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

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
    void getGame() throws DataAccessException, SQLException {
        GameData game = new GameData(0, null, null, "Game", null);
        int id = dataAccess.createGame(game);
        GameData gameResult = dataAccess.getGame(id);
        assertNotNull(gameResult);
        assertEquals(dataAccess.getGame(id).gameName(), "Game");
    }
    @Test
    void negativeGetGame() throws SQLException, DataAccessException {
        assertNull(dataAccess.getGame(1));
    }

    @Test
    void listGames() throws DataAccessException, SQLException {
        Collection<GameData> expected = new ArrayList<>();
        GameData game1 = new GameData(1, "", "", "Game1", null);
        GameData game2 = new GameData(2, "", "", "Game2", null);
        GameData game3 = new GameData(3, "", "", "Game3", null);
        int id1 = dataAccess.createGame(game1);
        int id2 = dataAccess.createGame(game2);
        int id3 = dataAccess.createGame(game3);
        expected.add(dataAccess.getGame(id1));
        expected.add(dataAccess.getGame(id2));
        expected.add(dataAccess.getGame(id3));
        Collection<GameData> listGames = dataAccess.listGames();
        assertIterableEquals(expected, listGames);
    }

    @Test
    void negativeListGames() throws SQLException, DataAccessException {
        Collection<GameData> listGames = dataAccess.listGames();
        assertDoesNotThrow(() -> {
            dataAccess.listGames();
        });
        assertEquals(0,listGames.size());
    }

    @Test
    void clearGames() throws DataAccessException, SQLException {
        int id1 = dataAccess.createGame(new GameData(0,null,null,"Game1",null));
        int id2 = dataAccess.createGame(new GameData(0,null,null,"Game2",null));
        int id3 = dataAccess.createGame(new GameData(0,null,null,"Game3",null));
        dataAccess.clearGames();
        assertNull(dataAccess.getGame(id1));
        assertNull(dataAccess.getGame(id2));
        assertNull(dataAccess.getGame(id3));

    }

    @Test
    void updateGame() throws DataAccessException, SQLException {
        int id = dataAccess.createGame(new GameData(0,null,null,"Game",null));
        GameData updatedGame = new GameData(id,"White","Black","Game1",null);
        dataAccess.updateGame(id, updatedGame);
        assertEquals("White", dataAccess.getGame(id).whiteUsername());
        assertEquals("Black", dataAccess.getGame(id).blackUsername());
        assertEquals("Game1", dataAccess.getGame(id).gameName());
    }
    @Test
    void negativeUpdateGame() throws DataAccessException, SQLException {
        int id = dataAccess.createGame(new GameData(0,null,null,"Game",null));
        GameData updatedGame = new GameData(id,"White","Black","Game1",null);
        dataAccess.updateGame(0, updatedGame);
        assertNotEquals("White", dataAccess.getGame(id).whiteUsername());
        assertNotEquals("Black", dataAccess.getGame(id).blackUsername());
        assertNotEquals("Game1", dataAccess.getGame(id).gameName());
    }
}