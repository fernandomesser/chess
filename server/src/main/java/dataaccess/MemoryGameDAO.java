package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 1;
    private HashMap<Integer, GameData> games = new HashMap<>();

    //Creates a new chess game and stores the corresponding GameData.
    @Override
    public int createGame(GameData game) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        game = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        games.put(game.gameID(), game);
        return game.gameID();
    }

    //Retrieves the GameData associated with the specified game ID.
    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    // Retrieves a collection of all stored GameData objects.
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    //Clears all stored game data.
    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }

    //Updates the game given the game ID
    @Override
    public void updateGame(int gameID, GameData updateGame) {
        games.put(gameID, updateGame);
    }
}
