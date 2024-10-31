package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    int createGame(GameData game) throws DataAccessException, ResponseException;

    GameData getGame(int gameID) throws DataAccessException, ResponseException;

    Collection<GameData> listGames() throws DataAccessException, ResponseException;

    void clearGames() throws DataAccessException, ResponseException;

    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;
}
