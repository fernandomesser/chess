package dataaccess;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() {
        try {
            configureDatabase();
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException("Failed to initialize SqlUserDAO: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertUser(UserData user) throws ResponseException {
        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public void clearUsers() throws DataAccessException {

    }

    private int executeUpdate(String statement, Object... params) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    } else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, java.sql.Types.NULL);
                    }
                }
                return ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new ResponseException(500, String.format("Unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
               username VARCHAR(256) NOT NULL PRIMARY KEY,
               password VARCHAR(256) NOT NULL,
               email VARCHAR(256)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    private void configureDatabase() throws ResponseException, DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new ResponseException(500, String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

}
