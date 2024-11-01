package dataaccess;

import model.UserData;

import java.sql.SQLException;

public interface UserDAO {
    void insertUser(UserData user) throws DataAccessException;

    UserData getUser(String username) throws DataAccessException, SQLException;

    void clearUsers() throws DataAccessException;
}
