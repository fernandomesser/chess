package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.ResponseException;

public class GameService {

    private final GameDAO gameDataAccess;
    private final AuthDAO authDataAccess;

    public GameService(GameDAO gameDataAccess, AuthDAO authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }
    public void clear() throws ResponseException {
        try {
            gameDataAccess.clearGames();
            
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
