package client;

import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
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

    @BeforeAll
    public static void init() {
        server = new Server();
        service = new ServerFacade("http://localhost:8080");
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
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
    public void logIn(){

    }
    @Test
    public void logOut(){}
    @Test
    public void listGames(){}
    @Test
    public void createGame(){}
    @Test
    public void joinGame(){}
    @Test
    public void clearApp(){}

}
