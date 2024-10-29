package dataaccess;

import exception.ResponseException;
import model.AuthData;

public class SqlAuthDAO extends BaseSqlDAO implements AuthDAO{

    private static final String[] CREATE_STATEMENTS = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
               authtoken VARCHAR(256) NOT NULL PRIMARY KEY,
               username VARCHAR(256) NOT NULL,
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };
    public SqlAuthDAO(){
        super(CREATE_STATEMENTS);
    }
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clearAuth() throws DataAccessException {

    }
}
