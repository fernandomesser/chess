package ui;

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
        try {
            if (params.length > 2) {
                state = State.SIGNEDIN;
                auth = server.register(new UserData(params[0], params[1], params[2]));
                return String.format("You have been registered as '%s'.", params[0]);
            }
            return "Expected: <USERNAME> <PASSWORD> <EMAIL>";
        } catch (ResponseException e) {
            return "Username already taken";
        }
    }

    public String logIn(String... params) throws ResponseException {
        try {
            if (params.length > 1) {
                state = State.SIGNEDIN;
                auth = server.logIn(new UserData(params[0], params[1], null));
                return String.format("You signed in as '%s'", params[0]);
            }
            return "Expected: <USERNAME> <PASSWORD>";
        } catch (ResponseException e) {
            return "Wrong Username or Password";
        }
    }

    public String logOut() throws ResponseException {
        assertSignedIn();
        server.logOut(auth.authToken());
        state = State.SIGNEDOUT;
        return String.format("'%s' signed out", auth.username());
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        try {
            var games = server.listGames(auth.authToken());
            var result = new StringBuilder();
            for (var game : games) {
                result.append(String.format("ID: %s   Game Name: %s   White: %s   Black: %s\n", game.gameID(),
                        game.gameName(),
                        (game.whiteUsername() != null) ? game.whiteUsername() : "none",
                        (game.blackUsername() != null) ? game.blackUsername() : "none"));
            }
            if (result.length() == 0) {
                return "No games to display";
            }
            return result.toString();
        } catch (ResponseException e) {
            return "Internal Server Error";
        }

    }

    public String createGame(String... params) throws ResponseException {
        assertSignedIn();
        try {
            String id = server.createGame(new GameData(0, null, null, params[0], null), auth.authToken());
            String numbers = id.replaceAll("[^0-9]", "");
            return String.format("Game '%s' created. Game Id: %s", params[0], numbers);
        } catch (Exception e) {
            return "Expected: <GAME NAME>";
        }

    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        try {
            if (params.length > 1) {
                int id = Integer.parseInt(params[0]);
                String color = params[1].toUpperCase();
                if (color.equalsIgnoreCase("white") || color.equalsIgnoreCase("black")) {
                    server.joinGame(id, color, auth.authToken());
                    return String.format("Joined %s team", color);
                } else {
                    return "Please enter a valid color <WHITE|BLACK>";
                }
            }
            return "Expected: <GAME ID> <COLOR>";
        } catch (ResponseException e) {
            if (e.getMessage().equals("failure: 403")) {
                return "Already taken";
            } else if (e.getMessage().equals("failure: 400")) {
                return "Game " + params[0] + " does not exist";
            }
            System.out.println(e.getMessage());
            return "Error";
        }
    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        return null;
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    Options:
                    - Register a new user: "register"
                    - Log in as an existing user: "login" <USERNAME> <PASSWORD>
                    - Exit the program: "quit"
                    - Print this message: "help"
                    """;
        }
        return """
                Options:
                - List current games: "list" 
                - Create a new game: "create" <GAME NAME>
                - Join a game: "join" <GAME ID> <COLOR>
                - Watch a game: "watch" <GAME ID>
                - Logout: "logout"
                - Exit the program: "quit"
                - Print this message: "help"
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

}
