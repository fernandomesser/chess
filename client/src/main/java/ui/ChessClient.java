package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private UserData user = null;
    private String auth = null;
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
                case "register" -> register(params);
                case "login" -> logIn(params);
                case "logout" -> logOut();
                case "list" -> listGames();
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            user = server.register(new UserData(params[1], params[2], params[3]));
            return String.format("You have been registered as %s.", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    private String logIn(String... params) throws ResponseException {
        if (params.length >= 1) {
            state = State.SIGNEDIN;
            auth = server.logIn(user).authToken();
            return String.format("You signed in as %s.", user.username());
        }
        throw new ResponseException(400, "Expected: <username> <password>");
    }

    private String logOut() throws ResponseException {
        assertSignedIn();
        server.logOut(auth);
        state = State.SIGNEDOUT;
        return String.format("%s signed out", user.username());
    }

    private String listGames() throws ResponseException {
        assertSignedIn();
        var games = server.listGames(auth);
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    private String createGame(String... params) throws ResponseException {
        assertSignedIn();
        server.createGame(new GameData(0, null, null, params[1], new ChessGame()), auth);
        return String.format("Game %s created", params[1]);
    }

    private String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        int id = Integer.parseInt(params[1]);
        String color = params[2].toUpperCase();
        server.joinGame(id, color, auth);
        return String.format("Joined %s team", color);
    } 

    private String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        return null;
    }

    private String help() {
        return null;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
