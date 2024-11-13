package client;

import chess.ChessGame;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import org.mindrot.jbcrypt.BCrypt;
import server.Server;
import ui.ServerFacade;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade service;
    private UserDAO userDataAccess = new SqlUserDAO();
    private GameDAO gameDataAccess = new SqlGameDAO();
    private AuthDAO authDataAccess = new SqlAuthDAO();

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        service = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }
    @BeforeEach
    public void clear() throws ResponseException {
        service.clearApp();
    }

    @AfterAll
    static void stopServer() throws ResponseException {
        server.stop();
    }

    @Test
    public void register() throws SQLException, DataAccessException, ResponseException {
        UserData user = new UserData("John", "1234", "john@email.com");
        AuthData authData = service.register(user);
        UserData userResult = userDataAccess.getUser("John");
        assertNotNull(userResult);
        assertEquals(user.username(), userResult.username());
        assertTrue(BCrypt.checkpw(user.password(), userResult.password()));
        assertEquals(user.email(), userResult.email());
        assertNotNull(authData);
    }
    @Test
    void negativeRegister() throws ResponseException, DataAccessException {
        UserData user = new UserData("", "1234", "jhon@email.com");
        assertThrows(ResponseException.class, () -> {
            service.register(user);
        });
    }
    @Test
    void logIn() throws ResponseException, DataAccessException, SQLException {
        UserData user = new UserData("Jhon", "1234", "jhon@email.com");
        service.register(user);
        AuthData authData = service.logIn(user);
        assertNotNull(authData);
    }
    @Test
    void negativeLogIn() {
        UserData user = new UserData("", "123", "luke@email.com");
        assertThrows(ResponseException.class, () -> {
            service.logIn(user);
        });
    }
    @Test
    void logOut() throws ResponseException, DataAccessException, SQLException {
        AuthData expected = service.register(new UserData("Luke", "1234", "jhon@email.com"));
        assertEquals(authDataAccess.getAuth(expected.authToken()).authToken(), expected.authToken());
        service.logOut(expected.authToken());
        assertNull(authDataAccess.getAuth(expected.authToken()));
    }
    @Test
    void negativeLogOut() {
        assertThrows(ResponseException.class, () -> {
            service.logOut(null);
        });
    }
    @Test
    public void listGames(){}
    @Test
    void createGame() throws ResponseException, DataAccessException, SQLException {
        AuthData auth = service.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, null, null, "Game", null);
        int gameId = Integer.parseInt(service.createGame(game, auth.authToken()).replaceAll("[^0-9]", ""));
        game = gameDataAccess.getGame(gameId);
        assertNotNull(gameDataAccess.getGame(game.gameID()));
        assertEquals(gameDataAccess.getGame(gameId).gameName(), "Game");
    }
    @Test
    void negativeCreateGame() throws ResponseException, DataAccessException, SQLException {
        AuthData auth = service.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, null, null, "", null);
        assertThrows(ResponseException.class, () -> {
            service.createGame(game, auth.authToken());
        });
    }
    @Test
    void joinGame() throws ResponseException, DataAccessException, SQLException {
        AuthData auth = service.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, null, null, "Game", null);
        int gameId = Integer.parseInt(service.createGame(game, auth.authToken()).replaceAll("[^0-9]", ""));
        service.joinGame(gameId, "WHITE", auth.authToken());
        assertEquals("User", gameDataAccess.getGame(gameId).whiteUsername());

    }
    @Test
    void negativeJoinGame() throws ResponseException, DataAccessException, SQLException {
        AuthData auth = service.register(new UserData("User", "1234", "test@test.com"));
        GameData game = new GameData(0, "Jhon", null, "Game", null);
        int gameId = Integer.parseInt(service.createGame(game, auth.authToken()).replaceAll("[^0-9]", ""));
        assertThrows(ResponseException.class, () -> {
            service.joinGame(gameId, "WHITE", auth.authToken());
            ;
        });
    }
    @Test
    void clearTest() throws ResponseException, DataAccessException, SQLException {
        AuthData auth = service.register(new UserData("User", "1234", "test@test.com"));
        int gameId1 = Integer.parseInt(service.createGame(new GameData(0,"","","Game1",new ChessGame()), auth.authToken()).replaceAll("[^0-9]", ""));
        int gameId2 = Integer.parseInt(service.createGame(new GameData(0,"","","Game2",new ChessGame()), auth.authToken()).replaceAll("[^0-9]", ""));
        int gameId3 = Integer.parseInt(service.createGame(new GameData(0,"","","Game3",new ChessGame()), auth.authToken()).replaceAll("[^0-9]", ""));

        assertNotNull(gameDataAccess.getGame(gameId1));
        assertNotNull(gameDataAccess.getGame(gameId2));
        assertNotNull(gameDataAccess.getGame(gameId3));
        service.clearApp();
        assertNull(gameDataAccess.getGame(gameId1));
        assertNull(gameDataAccess.getGame(gameId2));
        assertNull(gameDataAccess.getGame(gameId3));
    }

}
