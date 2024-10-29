package dataaccess;

import exception.ResponseException;

import java.sql.SQLException;

public abstract class BaseSqlDAO {

    protected BaseSqlDAO(String[] createStatements) {
        try {
            configureDatabase(createStatements);
        } catch (ResponseException | DataAccessException e) {
            throw new RuntimeException("Failed to initialize DAO: " + e.getMessage(), e);
        }
    }

    protected int executeUpdate(String statement, Object... params) throws ResponseException {
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

    protected void configureDatabase(String[] createStatements) throws ResponseException, DataAccessException {
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

