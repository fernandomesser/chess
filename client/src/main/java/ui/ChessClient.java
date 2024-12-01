package ui;

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.SqlGameDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import ui.websocket.NotificationHandler;
import ui.websocket.WebSocketFacade;
import websocket.commands.UserGameCommand;

import static ui.InGameHelper.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

public class ChessClient {
    Scanner in = new Scanner(System.in);
    SqlGameDAO gameDAO = new SqlGameDAO();
    private AuthData auth = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    public State state = State.SIGNEDOUT;
    static AtomicReference<GameData> gameData = new AtomicReference<>();
    private String teamColor = null;
    private int currentGameID = 0;

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
        } catch (ResponseException | InvalidMoveException | IOException ex) {
            return ex.getMessage();
        }
    }

    private String redraw() {

        return "";
    }

    private String leaveGame() {
        state = State.SIGNEDIN;
        ws.leave(auth.authToken(), currentGameID);
        new PrintStream(System.out, true, StandardCharsets.UTF_8).print(EscapeSequences.ERASE_SCREEN);
        return "";
    }

    private String makeMove(String... params) throws ResponseException, InvalidMoveException {
        ChessGame currentGame = gameData.get().game();
        ChessMove move;
        ChessGame.TeamColor color = teamColor.equalsIgnoreCase("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (!currentGame.getTeamTurn().equals(color)) {
            throw new ResponseException(400, "It is not your turn");
        }
        try {
            move = moveValidation(params[0], params[1], null, currentGame, color);
        } catch (Exception e) {
            return e.getMessage();
        }
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = currentGame.getBoard().getPiece(start);
        if (!piece.getTeamColor().equals(color)) {
            return "You can only move pieces on your team";
        }
        //Check if is promotion
        if (piece.getPieceType().equals(ChessPiece.PieceType.PAWN) || end.getRow() == 8 || end.getRow() == 1) {
            ChessPiece.PieceType promotionPiece = getPromotion(in, color);
            move.setPromotionPiece(promotionPiece);

        }
        ws.makeMove(auth.authToken(), gameData.get().gameID(), move);
        return "";
    }

    private String resign() throws ResponseException, IOException {
        ws.resign(auth.authToken(), currentGameID);
        state = State.SIGNEDIN;
        return "Resigned. The game is over";
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
                currentGameID = games.get(Integer.parseInt(params[0]) - 1).gameID();
                gameData.set(gameDAO.getGame(currentGameID));
                String color = params[1].toUpperCase();
                if (color.equalsIgnoreCase("white") || color.equalsIgnoreCase("black")) {
                    server.joinGame(currentGameID, color, auth.authToken());
                    teamColor = color;
                    state = color.equalsIgnoreCase("WHITE") ? State.INGAME_WHITE : State.INGAME_BLACK;
                    ws = new WebSocketFacade(serverUrl, notificationHandler);
                    ws.connect(auth.authToken(), currentGameID);
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
                currentGameID = games.get(Integer.parseInt(params[0]) - 1).gameID();
                gameData.set(gameDAO.getGame(currentGameID));
                ChessGame board = gameData.get().game();
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
        } else if (state == State.INGAME_WHITE || state == State.INGAME_BLACK || state == State.INGAME_OBSERVER) {
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
