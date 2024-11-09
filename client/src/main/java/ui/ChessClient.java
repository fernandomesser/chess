package ui;

import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private String visitorName = null;
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
                case "register" -> register();
                case "login" -> logIn();
                case "logout" -> logOut();
                case "list" -> listGames();
                case "create" -> createGame();
                case "join" -> joinGame();
                case "observe" -> observeGame();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String register() {
        return null;
    }

    private String logIn() {
        return null;
    }

    private String logOut() {
        return null;
    }

    private String listGames() {
        return null;
    }

    private String createGame() {
        return null;
    }

    private String joinGame() {
        return null;
    }

    private String observeGame() {
        return null;
    }

    private String help() {
        return null;
    }


}
