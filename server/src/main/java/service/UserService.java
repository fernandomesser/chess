package service;

import dataaccess.AuthDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.UserData;
import passoff.exception.ResponseParseException;

public class UserService {
    private final UserDAO userDataAccess;

    public UserService(UserDAO userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public UserDAO register(UserDAO user) throws ResponseException {


        return null;
    }
}
