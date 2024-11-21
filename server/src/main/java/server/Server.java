package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private UserService userService;
    private GameService gameService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        AuthDAO authDAO = new SqlAuthDAO();
        UserDAO userDAO = new SqlUserDAO();
        GameDAO gameDAO = new SqlGameDAO();

        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);
    }

    public int run(int desiredPort) {

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::register);
        Spark.post("/session", this::logIn);
        Spark.delete("/session", this::logOut);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clearApp);
        Spark.exception(ResponseException.class, this::exceptionHandler);


        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    //Handles the exceptions
    private <T extends Exception> void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.statusCode());
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
        ex.printStackTrace(System.out);
    }

    //Create a new User and return the AuthData
    private Object register(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData response = userService.register(user);
        return new Gson().toJson(response);
    }

    //Takes a User and returns the AuthData
    private Object logIn(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        UserData user = new Gson().fromJson(req.body(), UserData.class);
        AuthData response = userService.logIn(user);
        return new Gson().toJson(response);
    }

    //Remove a user AuthData
    private Object logOut(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        String auth = req.headers("Authorization");
        userService.logOut(auth);

        res.status(200);
        return new Gson().toJson(Map.of());
    }

    //List all games in the database
    private Object listGames(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        String auth = req.headers("Authorization");
        Collection<GameData> gamesList = gameService.listGames(auth);
        Map<String, Collection<GameData>> response = new HashMap<>();
        response.put("games", gamesList);
        return new Gson().toJson(response);
    }

    //Creates a new game
    private Object createGame(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        String auth = req.headers("Authorization");
        GameData game = new Gson().fromJson(req.body(), GameData.class);
        int gameID = gameService.createGame(game, auth);
        var response = new HashMap<String, Integer>();
        response.put("gameID", gameID);
        return new Gson().toJson(response);
    }

    //Updates the game in the database
    private Object joinGame(Request req, Response res) throws ResponseException, DataAccessException, SQLException {
        String auth = req.headers("Authorization");
        JsonObject rqBdy = new Gson().fromJson(req.body(), JsonObject.class);
        String plyrC = (rqBdy.has("playerColor") && !rqBdy.get("playerColor").isJsonNull()) ? rqBdy.get("playerColor").getAsString() : "";
        int gID = (rqBdy.has("gameID") && !rqBdy.get("gameID").isJsonNull()) ? rqBdy.get("gameID").getAsInt() : -1;
        gameService.joinGame(gID, plyrC, auth);

        res.status(200);
        return new Gson().toJson(Map.of());
    }

    //Clear the DataBase
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
