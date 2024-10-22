package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> authData = new HashMap<>();


    //Creates a new authentication token for the specified user.
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authData.put(auth.authToken(), auth);
        return auth;
    }

    //Retrieves the AuthData associated with the specified authentication token.
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authData.get(authToken);
    }

    //Deletes the AuthData associated with the specified authentication token.
    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authData.remove(authToken);
    }

    //Clears auth HashMap
    @Override
    public void clearAuth() throws DataAccessException {
        authData.clear();
    }
}
