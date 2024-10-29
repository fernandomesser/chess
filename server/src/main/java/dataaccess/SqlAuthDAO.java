package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class SqlAuthDAO extends BaseSqlDAO implements AuthDAO{

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(36) NOT NULL PRIMARY KEY,
                username VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };
    public SqlAuthDAO(){
        super(CREATE_STATEMENTS);
    }
    @Override
    public AuthData createAuth(String username) throws ResponseException {
        String authToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return new AuthData(authToken, username);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, ResponseException {
            try (var conn = DatabaseManager.getConnection()) {
                String statement = "SELECT authToken, username FROM auth WHERE authToken=?";
                try (var ps = conn.prepareStatement(statement)) {
                    ps.setString(1, authToken);
                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return readAuth(rs);
                        }
                    }
                }
            } catch (Exception e) {
                throw new ResponseException(500, String.format("Unable to read user data: %s", e.getMessage()));
            }
            return null;
    }


    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken ,username);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException, ResponseException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException, ResponseException {
        var statement = "DELETE FROM auth";
        executeUpdate(statement);
    }
}
