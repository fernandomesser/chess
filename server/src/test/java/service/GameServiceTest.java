package service;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private final GameDAO gameDataAccess = new MemoryGameDAO();
    private final UserDAO userDataAccess = new MemoryUserDAO();
    private final AuthDAO authDataAccess = new MemoryAuthDAO();
    GameService service = new GameService(gameDataAccess, authDataAccess);
    UserService userService = new UserService(userDataAccess, authDataAccess);

    @BeforeEach
    void reset() throws ResponseException, DataAccessException {
        userDataAccess.clearUsers();
        authDataAccess.clearAuth();
        gameDataAccess.clearGames();
    }


    @Test
    void positiveCreateGame() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, null, null, "Game", null);
        service.createGame(game, auth.authToken());
        game = gameDataAccess.getGame(1);
        assertNotNull(gameDataAccess.getGame(game.gameID()));
        assertEquals(gameDataAccess.getGame(1).gameName(), "Game");
    }

    @Test
    void negativeCreateGame() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, null, null, "", null);
        assertThrows(ResponseException.class, () -> {
            service.createGame(game, auth.authToken());
        });
    }

    @Test
    void positiveListGames() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        Collection<GameData> expected = new ArrayList<>();
        GameData game1 = new GameData(1, "", "", "Game1", null);
        GameData game2 = new GameData(2, "", "", "Game2", null);
        GameData game3 = new GameData(3, "", "", "Game3", null);
        expected.add(game1);
        expected.add(game2);
        expected.add(game3);
        service.createGame(game1, auth.authToken());
        service.createGame(game2, auth.authToken());
        service.createGame(game3, auth.authToken());
        Collection<GameData> listGames = service.listGames(auth.authToken());
        assertIterableEquals(expected, listGames);
    }

    @Test
    void negativeListGames() throws ResponseException, DataAccessException {
        assertThrows(ResponseException.class, () -> {
            service.listGames(null);
        });
    }

    @Test
    void positiveJoinGame() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0,null,null,"Game",null);
        service.createGame(game,auth.authToken());
        service.joinGame(1,"WHITE", auth.authToken());
        assertEquals("User", gameDataAccess.getGame(1).whiteUsername());

    }

    @Test
    void negativeJoinGame() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0,"Jhon",null,"Game",null);
        service.createGame(game,auth.authToken());
        assertThrows(ResponseException.class, () -> {
            service.joinGame(1,"WHITE", auth.authToken());;
        });
    }

    @Test
    void clearTest() throws ResponseException, DataAccessException {
        AuthData auth = userService.register(new UserData("User", "1234", "test@test.com"));
        service.createGame(new GameData(0, null, null, "Game1", null), auth.authToken());
        service.createGame(new GameData(0, null, null, "Game2", null), auth.authToken());
        service.createGame(new GameData(0, null, null, "Game3", null), auth.authToken());
        assertNotNull(gameDataAccess.getGame(1));
        assertNotNull(gameDataAccess.getGame(2));
        assertNotNull(gameDataAccess.getGame(3));
        service.clear();
        assertNull(gameDataAccess.getGame(1));
        assertNull(gameDataAccess.getGame(2));
        assertNull(gameDataAccess.getGame(3));
    }
}