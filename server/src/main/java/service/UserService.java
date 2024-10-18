package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
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
            userDataAccess.insertUser(user);
            return userAuth.createAuth(user.username());
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public AuthData logIn(UserData user) throws ResponseException, DataAccessException {
        if(userDataAccess.getUser(user.username()) == null){
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            return userAuth.createAuth(user.username());
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }

    public void logOut(String auth) throws ResponseException, DataAccessException{
        if (userAuth.getAuth(auth)==null||auth==null||auth.isEmpty()){
            throw new ResponseException(401, "Error: unauthorized");
        }
        try {
            userAuth.deleteAuth(auth);
        }catch (Exception e) {
            throw new ResponseException(500, "Error: " + e.getMessage());
        }
    }



    private boolean empty(UserData user){
        return user.username()==null||user.password()==null||user.email()==null
                || user.username().isEmpty()||user.password().isEmpty()||user.email().isEmpty();
    }


}
