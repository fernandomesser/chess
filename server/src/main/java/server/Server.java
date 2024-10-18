package server;

import com.google.gson.Gson;
import dataaccess.UserDAO;
import exception.ResponseException;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;

    public Server(UserService service) {
        this.userService = service;
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
    }

    private Object register(Request req, Response res) throws ResponseException {
        var user = new Gson().fromJson(req.body(), UserDAO.class);
        user = userService.register(user);
        return new Gson().toJson(user);
    }

    private Object logIn(Request req, Response res) throws ResponseException {
        return null;
    }

    private Object logOut(Request req, Response res) throws ResponseException {
        return null;
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
