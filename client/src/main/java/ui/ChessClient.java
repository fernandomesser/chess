package ui;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.SqlGameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ChessClient {
    SqlGameDAO gameDAO = new SqlGameDAO();
    private AuthData auth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;
    public static GameData currentGame = null;
    private String teamColor = null;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.notificationHandler = notificationHandler;
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

                case "redraw" -> redraw();
                case "move" -> makeMove(params);
                case "highlight" -> highlight(params);
                case "resign" -> resign();
                case "leave" -> leaveGame();

                case "quit", "exit" -> {
                    System.out.println("Chess Program Closed");
                    System.exit(0);
                    yield "";
                }
                case "clear" -> clearApp();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    private String redraw() {
        return "";
    }

    private String leaveGame() {
        return "";
    }

    private String makeMove(String... params) {
        return "";
    }

    private String resign() {
        return "";
    }

    private String highlight(String... params) {
        return "";
    }

    private String clearApp() throws ResponseException {
        server.clearApp();
        return "Database Cleared";
    }

    public String register(String... params) throws ResponseException {
        try {
            if (params.length == 3) {
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
            if (params.length == 2) {
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
            Collection<GameData> games = server.listGames(auth.authToken());
            var result = new StringBuilder();
            int i = 1;
            for (var game : games) {
                result.append(String.format("%d.   Game Name: %s   White: %s   Black: %s\n", i++,
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
            if (params.length == 1) {
                server.createGame(new GameData(0, null, null, params[0], new ChessGame()), auth.authToken());
                return String.format("Game '%s' created.", params[0]);
            }
            return "Expected: <GAME NAME>";
        } catch (Exception e) {
            return "Expected: <GAME NAME>";
        }

    }

    public String joinGame(String... params) throws ResponseException {
        assertSignedIn();
        try {
            if (params.length == 2) {
                List<GameData> games = (List<GameData>) server.listGames(auth.authToken());
                int id = games.get(Integer.parseInt(params[0]) - 1).gameID();
                currentGame = gameDAO.getGame(id);
                String color = params[1].toUpperCase();
                if (color.equalsIgnoreCase("white") || color.equalsIgnoreCase("black")) {
                    server.joinGame(id, color, auth.authToken());
                    teamColor = color;
                    if (color.equalsIgnoreCase("WHITE")) {
                        displayBoardWhiteSide(currentGame.game());
                    } else {
                        displayBoardBlackSide(currentGame.game());
                    }
                    state = State.INGAME;
                    ws = new WebSocketFacade(serverUrl, notificationHandler);
                    ws.connect(auth.authToken(), id);
                    return String.format("Joined %s team", color);
                } else {
                    return "Please enter a valid color <WHITE|BLACK>";
                }
            }
            return "Expected: <GAME INDEX> <COLOR>";
        } catch (ResponseException e) {
            if (e.getMessage().equals("failure: 403")) {
                return "Already taken";
            } else if (e.getMessage().equals("failure: 400")) {
                return "Game " + params[0] + " does not exist";
            }
            return "Error";
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return "Please provide a valid number";
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

    }

    public String observeGame(String... params) throws ResponseException {
        assertSignedIn();
        try {
            if (params.length == 1) {
                List<GameData> games = (List<GameData>) server.listGames(auth.authToken());
                int id = games.get(Integer.parseInt(params[0]) - 1).gameID();
                currentGame = gameDAO.getGame(id);
                ChessGame board = currentGame.game();
                displayBoardWhiteSide(board);
                return "";
            }
            return "Expected: <GAME INDEX>";
        } catch (ResponseException e) {
            return "Error";
        } catch (NumberFormatException | IndexOutOfBoundsException ex) {
            return "Please provide a valid number";
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }

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
        } else if (state == State.SIGNEDIN) {
            return """
                    Options:
                    - List current games: "list" 
                    - Create a new game: "create" <GAME NAME>
                    - Join a game: "join" <GAME INDEX> <COLOR>
                    - Observe a game: "observe" <GAME INDEX>
                    - Logout: "logout"
                    - Exit the program: "quit"
                    - Print this message: "help"
                    """;
        } else {
            return """
                    Options:
                    - Redraw Chess Board: "redraw" 
                    - Leave game: "leave"
                    - Make Move: "move" <START POSITION> <END POSITION>
                    - Resign: "resign"
                    - Highlight Legal Moves: "highlight" <POSITION>
                    - Print this message: "help"
                    """;
        }

    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        } else if (state == State.INGAME) {
            throw new ResponseException(400, "You must leave the game first");
        }
    }
    private void assertInGame() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        } else if (state == State.SIGNEDIN) {
            throw new ResponseException(400, "You must join a game first");
        }
    }

    private void displayBoardWhiteSide(ChessGame game) {
        new DrawBoard(game, "WHITE");
    }

    private void displayBoardBlackSide(ChessGame game) {
        new DrawBoard(game, "BLACK");
    }

}
