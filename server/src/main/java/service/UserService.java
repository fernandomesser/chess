package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

public class UserService {
    private final UserDAO userDataAccess;
    private final AuthDAO userAuth;

    public UserService(UserDAO userDataAccess, AuthDAO userAuth) {
        this.userDataAccess = userDataAccess;
        this.userAuth = userAuth;
    }

    public AuthData register(UserData user) throws ResponseException, DataAccessException {
        if(empty(user)){
            throw new ResponseException(400, "Error: bad request");
        }if (userDataAccess.getUser(user.username()) != null){
            throw new ResponseException(403, "Error: already taken");
        }
        try {
            userDataAccess.createUser(user);
            return userAuth.createAuth(user.username());
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }




    private boolean empty(UserData user){
        return user.username()==null||user.password()==null||user.email()==null
                || user.username().isEmpty()||user.password().isEmpty()||user.email().isEmpty();
    }
}
