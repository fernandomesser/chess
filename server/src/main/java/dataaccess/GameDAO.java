package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException, SQLException;

    Collection<GameData> listGames() throws DataAccessException, SQLException;

    void clearGames() throws DataAccessException;

    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
}
