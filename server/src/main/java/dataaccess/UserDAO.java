package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void insertUser(UserData user) throws DataAccessException, ResponseException;

    UserData getUser(String username) throws DataAccessException, ResponseException, SQLException;

    void clearUsers() throws DataAccessException, ResponseException;
}
