package ui;


import com.google.gson.Gson;
import model.GameData;
import ui.websocket.NotificationHandler;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.management.Notification;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

import static ui.EscapeSequences.*;

public class Repl implements NotificationHandler {
    private final ChessClient client;
    static AtomicReference<GameData> gameData = new AtomicReference<>();

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }

    @Override
    public void notify(String message) {
        ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
        switch (notification.getServerMessageType()){
            case ERROR -> {
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(errorMessage.getMessage());

            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(notificationMessage.getMessage());
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = new Gson().fromJson(message, LoadGameMessage.class);
                if (client.state == State.INGAME_WHITE){
                    gameData.set(loadGameMessage.getGame());
                    new DrawBoard(loadGameMessage.getGame().game(), "WHITE");
                }else if (client.state == State.INGAME_BLACK){
                    gameData.set(loadGameMessage.getGame());
                    new DrawBoard(loadGameMessage.getGame().game(), "BLACK");
                }else {
                    gameData.set(loadGameMessage.getGame());
                    new DrawBoard(loadGameMessage.getGame().game(), "WHITE");
                }
            }
        }
    }
}