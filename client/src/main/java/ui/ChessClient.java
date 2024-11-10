package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private AuthData auth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private State state = State.SIGNEDOUT;

    public ChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register", "r" -> register(params);
                case "login", "l" -> logIn(params);
                case "logout" -> logOut();
                case "list" -> listGames();
                case "create", "c" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                case "clear" -> clearApp();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String clearApp() throws ResponseException {
        server.clearApp();
        return "Database Cleared";
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            auth = server.register(new UserData(params[0], params[1], params[2]));
            return String.format("You have been registered as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    public String logIn(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            auth = server.logIn(new UserData(params[0],params[1],null ));
            return String.format("You signed in as %s.", params[0]);
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    public String logOut() throws ResponseException {
        assertSignedIn();
        server.logOut(auth.authToken());
        state = State.SIGNEDOUT;
        return String.format("%s signed out", auth.username());
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(auth.authToken());
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        String id = server.createGame(new GameData(0, null, null, params[0], null), auth.authToken());
        String numbers = id.replaceAll("[^0-9]", "");
        return String.format("Game %s created. Game Id: %s", params[0], numbers);
    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        int id = Integer.parseInt(params[0]);
        String color = params[1].toUpperCase();
        server.joinGame(id, color, auth.authToken());
        return String.format("Joined %s team", color);
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        return null;
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    Options:
                    - Log in as an existing user: "l", "login" <USERNAME> <PASSWORD>
                    - Register a new user: "r", "register"
                    - Exit the program: "q", "quit"
                    - Print this message: "h", "help"
                    """;
        }
        return """
                Options:
                - List current games: "l", "list" 
                - Create a new game: "c", "create" <GAME NAME>
                - Join a game: "j", "join" <GAME ID> <COLOR>
                - Watch a game: "w", "watch" <GAME ID>
                - Logout: "logout"
                - Exit the program: "q", "quit"
                - Print this message: "h", "help"
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
