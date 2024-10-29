package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;

public class SqlUserDAO implements UserDAO {

    public SqlUserDAO() throws ResponseException, DataAccessException {
        configureDatabase();
    }

    @Override
    public void insertUser(UserData user) throws DataAccessException {
        String insertUser = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void clearUsers() throws DataAccessException {

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
