package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public void clear() throws ResponseException {
        try {
            userDataAccess.clearUsers();
            authDataAccess.clearAuth();
        } catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

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


    private boolean empty(UserData user) {
        return user.username() == null || user.password() == null || user.email() == null
                || user.username().isEmpty() || user.password().isEmpty() || user.email().isEmpty();
    }


}
