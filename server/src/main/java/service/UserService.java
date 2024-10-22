package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {
    private UserDAO userDataAccess;
    private AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    //Clear data
    public void clear() throws ResponseException {
        try {
            userDataAccess.clearUsers();
            authDataAccess.clearAuth();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    //Create a new User and handle error
    public AuthData register(UserData user) throws ResponseException, DataAccessException {
        if (empty(user)) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (userDataAccess.getUser(user.username()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        try {
            userDataAccess.insertUser(user);
            return authDataAccess.createAuth(user.username());
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    //Authenticate the user and return AuthData, handle errors
    public AuthData logIn(UserData user) throws ResponseException, DataAccessException {
        UserData userData = userDataAccess.getUser(user.username());
        if (userData == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        if (!userData.password().equals(user.password())) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            return authDataAccess.createAuth(user.username());
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    //Delete AuthData for the user and handle errors
    public void logOut(String auth) throws ResponseException, DataAccessException {
        if (authDataAccess.getAuth(auth) == null || auth == null || auth.isEmpty()) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            authDataAccess.deleteAuth(auth);
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }


    //Check if UserData has any empty fields
    private boolean empty(UserData user) {
        return user.username() == null || user.password() == null || user.email() == null
                || user.username().isEmpty() || user.password().isEmpty() || user.email().isEmpty();
    }


}
