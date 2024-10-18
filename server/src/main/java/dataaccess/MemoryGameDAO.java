package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;
import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private int nextId = 1;
    private HashMap<Integer, GameData> games = new HashMap<>();

    @Override
    public int createGame(GameData game) throws DataAccessException {
        ChessGame chessGame = new ChessGame();
        game = new GameData(nextId++, game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        games.put(game.gameID(),game);
        return game.gameID();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values();
    }

    @Override
    public void clearGames() throws DataAccessException {
        games.clear();
    }

    @Override
    public void updateGame(int gameID, GameData updateGame) {
        games.put(gameID,updateGame);
    }
}
