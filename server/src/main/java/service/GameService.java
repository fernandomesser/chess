package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import exception.ResponseException;
import model.GameData;

import java.util.Collection;
import java.util.stream.Collectors;

public class GameService {

    private GameDAO gameDataAccess;
    private AuthDAO authDataAccess;

    public GameService(GameDAO gameDataAccess, AuthDAO authDataAccess) {
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    //Clear Data
    public void clear() throws ResponseException {
        try {
            gameDataAccess.clearGames();
            authDataAccess.clearAuth();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }


    //Create a new game with error handling
    public int createGame(GameData game, String auth) throws ResponseException, DataAccessException {
        if (game.gameName() == null || game.gameName().isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (authDataAccess.getAuth(auth) == null || auth == null || auth.isEmpty()) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            return gameDataAccess.createGame(game);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    //List all games and handle errors
    public Collection<GameData> listGames(String auth) throws ResponseException, DataAccessException {
        if (authDataAccess.getAuth(auth) == null || auth == null || auth.isEmpty()) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            return gameDataAccess.listGames().stream().map(gameData -> new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    null
            )).collect(Collectors.toList());

        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    //updates the game and handle errors
    public void joinGame(int gameID, String playerColor, String auth) throws ResponseException, DataAccessException {
        GameData game = gameDataAccess.getGame(gameID);
        GameData updatedGame;
        if (authDataAccess.getAuth(auth) == null || auth == null || auth.isEmpty()) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (game == null || gameID == -1 || playerColor == null || playerColor.isEmpty()) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (playerColor.equalsIgnoreCase("WHITE") && game.whiteUsername() != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        if (playerColor.equalsIgnoreCase("BLACK") && game.blackUsername() != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        try {
            if (playerColor.equalsIgnoreCase("WHITE")) {
                updatedGame = new GameData(gameID, authDataAccess.getAuth(auth).username(), game.blackUsername(), game.gameName(), game.game());
                gameDataAccess.updateGame(gameID, updatedGame);
            }
            if (playerColor.equalsIgnoreCase("BLACK")) {
                updatedGame = new GameData(gameID, game.whiteUsername(), authDataAccess.getAuth(auth).username(), game.gameName(), game.game());
                gameDataAccess.updateGame(gameID, updatedGame);
            }
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }
}
