package dataaccess;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import model.AuthData;

public class SqlAuthDAO extends BaseSqlDAO implements AuthDAO {

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                authToken VARCHAR(36) NOT NULL PRIMARY KEY,
                username VARCHAR(256) NOT NULL
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
            """
    };

    public SqlAuthDAO() {
        super(CREATE_STATEMENTS);
    }

    //Create a authToken and store in the database
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        String statement = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(statement, authToken, username);
        return new AuthData(authToken, username);
    }

    //get an authToken from the database
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, SQLException {
        var conn = DatabaseManager.getConnection();
        String statement = "SELECT authToken, username FROM auth WHERE authToken = ?";
        var ps = conn.prepareStatement(statement);
        ps.setString(1, authToken);
        var rs = ps.executeQuery();
        if (rs.next()) {
            return readAuth(rs);
        }
        return null;
    }

    private AuthData readAuth(ResultSet rs) throws SQLException {
        String authToken = rs.getString("authToken");
        String username = rs.getString("username");
        return new AuthData(authToken, username);
    }

    //deletes an authToken from the database
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String statement = "DELETE FROM auth WHERE authToken = ?";
        executeUpdate(statement, authToken);
    }

    //Deletes auth table
    @Override
    public void clearAuth() throws DataAccessException {
        var statement = "DELETE FROM auth";
        executeUpdate(statement);
    }
}
