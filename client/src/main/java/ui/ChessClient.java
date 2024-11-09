package ui;

import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private UserData user = null;
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
            user = server.register(new UserData(params[1],params[2],params[3]));
            return String.format("You signed in as %s.", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname>");
    }

    private String logIn(String... params) {
        return null;
    }

    private String logOut() throws ResponseException {
        assertSignedIn();
        return null;
    }

    private String listGames() throws ResponseException {
        assertSignedIn();
        return null;
    }

    private String createGame(String... params) throws ResponseException {
        assertSignedIn();
        return null;
    }

    private String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        return null;
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
