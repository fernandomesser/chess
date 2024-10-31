package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.sql.SQLException;
import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException, ResponseException;

    GameData getGame(int gameID) throws DataAccessException, ResponseException, SQLException;

    Collection<GameData> listGames() throws DataAccessException, ResponseException, SQLException;

    void clearGames() throws DataAccessException, ResponseException;

    void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ResponseException, SQLException;
}
