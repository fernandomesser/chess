package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final UserService userService;
    private final GameService gameService;

    public Server(UserService service, GameService gameService) {
        this.userService = service;
        this.gameService = gameService;
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
        var user = new Gson().fromJson(req.body(), UserData.class);
       AuthData response = userService.register(user);
        return new Gson().toJson(response);
    }

    private Object logIn(Request req, Response res) throws ResponseException, DataAccessException {
        var user = new Gson().fromJson(req.body(), UserData.class);
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

    private Object createGame(Request req, Response res) throws ResponseException {
        return null;
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        return null;
    }

    private Object clearApp(Request req, Response res) throws ResponseException {
        return null;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
