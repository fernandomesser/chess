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
        if (params.length > 2) {
            state = State.SIGNEDIN;
            auth = server.register(new UserData(params[0], params[1], params[2]));
            return String.format("You have been registered as %s.", params[0]);
        }
        return "Expected: <username> <password> <email>";
    }

    public String logIn(String... params) throws ResponseException {
        try {
            if (params.length > 1) {
                state = State.SIGNEDIN;
                auth = server.logIn(new UserData(params[0], params[1], null));
                return String.format("You signed in as %s.", params[0]);
            }
            System.out.println("Expected: <username> <password>");
        }catch (ResponseException e){
            System.out.println("test");
            getErrorMessage(e);
        }
        return "";
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
        for (var game : games) {
            result.append(String.format("ID: %s   Game Name: %s   White: %s   Black: %s\n", game.gameID(),
                    game.gameName(),
                    (game.whiteUsername() != null) ? game.whiteUsername() : "none",
                    (game.blackUsername() != null) ? game.blackUsername() : "none"));
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
        try {
            assertSignedIn();
            int id = Integer.parseInt(params[0]);
            String color = params[1].toUpperCase();
            server.joinGame(id, color, auth.authToken());
            return String.format("Joined %s team", color);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
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

    private void getErrorMessage(ResponseException e) {
        switch (e.statusCode()) {
            case 401 -> System.out.println("Unauthorized");
            case 403 -> System.out.println("Already Taken");
            case 404 -> System.out.println("The requested resource was not found");
            case 500 -> System.out.println("Internal server error: " + e.getMessage());
            default -> System.out.println("Error");
        };
    }
}
