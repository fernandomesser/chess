package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;

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


    public int createGame(GameData game, String auth) throws ResponseException, DataAccessException {
        if (game.gameName()==null||game.gameName().isEmpty()){
            throw new ResponseException(400, "Error: bad request");
        }if (authDataAccess.getAuth(auth)==null||auth==null||auth.isEmpty()){
            throw new ResponseException(401, "Error: unauthorized");
        }try {
            return gameDataAccess.createGame(game);
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
