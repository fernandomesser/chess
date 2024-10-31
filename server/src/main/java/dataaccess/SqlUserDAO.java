package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlUserDAO extends BaseSqlDAO implements UserDAO {
    private static final String[] CREATE_STATEMENTS = {"""
            CREATE TABLE IF NOT EXISTS  users (
               username VARCHAR(256) NOT NULL PRIMARY KEY,
               password VARCHAR(256) NOT NULL,
               email VARCHAR(256)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """};

    public SqlUserDAO() {
        super(CREATE_STATEMENTS);
    }

    @Override
    public void insertUser(UserData user) throws ResponseException {

        String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(statement, user.username(), user.password(), user.email());
    }

    public UserData getPassword(String username) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readUser(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read user data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public UserData getUser(String username) throws ResponseException, SQLException, DataAccessException {
        var conn = DatabaseManager.getConnection();
        String statement = "SELECT username, password, email FROM users WHERE username=?";
        var ps = conn.prepareStatement(statement);
        ps.setString(1, username);
        var rs = ps.executeQuery();
        if (rs.next()) {
            return readUser(rs);
        }
        return null;
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        String username = rs.getString("username");
        String password = rs.getString("password");
        String email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public void clearUsers() throws ResponseException {
        var statement = "DELETE FROM users";
        executeUpdate(statement);
    }

}
