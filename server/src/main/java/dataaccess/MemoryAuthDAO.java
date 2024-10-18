package dataaccess;
import java.util.HashMap;
import java.util.UUID;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO{
    private HashMap<String, AuthData> authData = new HashMap<>();
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData auth = new AuthData(UUID.randomUUID().toString(), username);
        authData.put(auth.authToken(), auth);
        return auth;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authData.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authData.remove(authToken);
    }

    @Override
    public void clearAuth() throws DataAccessException {
        authData.clear();
    }
}
