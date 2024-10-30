package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

public class SqlGameDAO extends BaseSqlDAO implements GameDAO{

        private static final String[] CREATE_STATEMENTS = {
                """
                CREATE TABLE IF NOT EXISTS games (
                    gameID INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(256),
                    blackUsername VARCHAR(256),
                    gameName VARCHAR(256),
                    game TEXT,
                    PRIMARY KEY (gameID)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                 """
    };

    public SqlGameDAO(){
        super(CREATE_STATEMENTS);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException, ResponseException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }
    private GameData readGame(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new GameData(authToken ,username);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public void clearGames() throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }
}
