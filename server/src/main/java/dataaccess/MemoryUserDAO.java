package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private HashMap<String, UserData> users = new HashMap<>();

    //Inserts a new user into the data storage.
    @Override
    public void insertUser(UserData user) throws DataAccessException {
        user = new UserData(user.username(), user.password(), user.email());
        users.put(user.username(), user);
    }

    //Retrieves a user based on username
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

    //Clears users hashMap
    @Override
    public void clearUsers() throws DataAccessException {
        users.clear();
    }
}
