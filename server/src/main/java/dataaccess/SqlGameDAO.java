package dataaccess;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class SqlGameDAO extends BaseSqlDAO implements GameDAO {

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

    public SqlGameDAO() {
        super(CREATE_STATEMENTS);
    }

    @Override
    public int createGame(GameData game) throws DataAccessException, ResponseException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, SQLException {
        var conn = DatabaseManager.getConnection();
        String statement = "SELECT * FROM games WHERE gameID = ?";
        var ps = conn.prepareStatement(statement);
        ps.setInt(1, gameID);
        var rs = ps.executeQuery();
        if (rs.next()) {
            return readGame(rs);
        }


        return null;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        int gameID = rs.getInt("gameID");
        String whiteUsername = rs.getString("whiteUsername");
        String blackUsername = rs.getString("blackUsername");
        String gameName = rs.getString("gameName");
        ChessGame game = (ChessGame) rs.getObject("game");
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException, SQLException {
        var result = new ArrayList<GameData>();
        var conn = DatabaseManager.getConnection();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
        var ps = conn.prepareStatement(statement);
        var rs = ps.executeQuery();
        while (rs.next()) {
            result.add(readGame(rs));
        }
        return result;
    }

    @Override
    public void clearGames() throws DataAccessException, ResponseException {
        var statement = "DELETE FROM games";
        executeUpdate(statement);
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException, ResponseException, SQLException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, game = ? WHERE gameID = ?";

        Connection conn = DatabaseManager.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);

        stmt.setString(1, updatedGame.whiteUsername());
        stmt.setString(2, updatedGame.blackUsername());
        stmt.setString(3, updatedGame.gameName());
        stmt.setObject(4, updatedGame.game());
        stmt.setInt(5, gameID);

        stmt.executeUpdate();
    }
}
