package service;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private static final GameDAO gameDataAccess = new MemoryGameDAO();
    private static final UserDAO userDataAccess = new MemoryUserDAO();
    private static final AuthDAO authDataAccess = new MemoryAuthDAO();
    static final GameService service = new GameService(gameDataAccess, authDataAccess);
    static final UserService userService = new UserService(userDataAccess, authDataAccess);

    @BeforeEach
    void clear() throws ResponseException {
        service.clear();
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

    }
}