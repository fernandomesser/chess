package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface AuthDAO {
    AuthData createAuth(String username) throws DataAccessException, ResponseException;

    AuthData getAuth(String authToken) throws DataAccessException, ResponseException;

    void deleteAuth(String authToken) throws DataAccessException, ResponseException;

    void clearAuth() throws DataAccessException, ResponseException;
}
