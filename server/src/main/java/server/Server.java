package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        MemoryAuthDAO authDAO = new MemoryAuthDAO();
        MemoryUserDAO userDAO = new MemoryUserDAO();
        MemoryGameDAO gameDAO = new MemoryGameDAO();

        this.userService = new UserService(userDAO,authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user",this::register);
        Spark.post("/session",this::logIn);
        Spark.delete("/session",this::logOut);
        Spark.get("/game", this::listGames);
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);
        Spark.delete("/db",this::clearApp);
        Spark.exception(ResponseException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private <T extends Exception> void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        ex.printStackTrace(System.out);
    }

    private Object register(Request req, Response res) throws ResponseException, DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData response = userService.register(user);
        return new Gson().toJson(response);
    }

    private Object logIn(Request req, Response res) throws ResponseException, DataAccessException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData response = userService.logIn(user);
        return new Gson().toJson(response);
    }

    private Object logOut(Request req, Response res) throws ResponseException, DataAccessException {
        String auth = req.headers("Authorization");
        userService.logOut(auth);

        res.status(200);
        return new Gson().toJson(Map.of());
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        return null;
    }

    private Object createGame(Request req, Response res) throws ResponseException, DataAccessException {
        String auth = req.headers("Authorization");
        GameData game = new Gson().fromJson(req.body(), GameData.class);
        int gameID = gameService.createGame(game, auth);
        var response = new HashMap<String, Integer>();
        response.put("gameID", gameID);
        return new Gson().toJson(response);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        return null;
    }

    private Object clearApp(Request req, Response res) throws ResponseException {
        userService.clear();
        gameService.clear();

        res.status(200);
        return new Gson().toJson(Map.of());
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
