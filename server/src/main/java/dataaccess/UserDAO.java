package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    void insertUser(UserData user) throws DataAccessException, ResponseException;

    UserData getUser(String username) throws DataAccessException, ResponseException;

    void clearUsers() throws DataAccessException, ResponseException;
}
