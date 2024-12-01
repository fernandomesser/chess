package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Connection>> connections = new ConcurrentHashMap<>();

    public void add(int gameID, String auth, Session session) {
        Set<Connection> players;
        if (connections.containsKey(gameID)) {
            players = connections.get(gameID);
        } else {
            players = new HashSet<>();
        }
        players.add(new Connection(auth, session));
        connections.put(gameID, players);
    }

    public void remove(int gameID, String auth) {
        Set<Connection> players = connections.get(gameID);
        players.removeIf(player -> player.auth.equals(auth));
        connections.put(gameID, players);
    }

    public void broadcast(int gameID, String auth, ServerMessage notification, boolean all) throws IOException {
        var removeList = new ArrayList<Connection>();
        Set<Connection> players = connections.get(gameID);
        if (!all){
            for (Connection c : players) {
                if (c.session.isOpen()) {
                    if (!c.auth.equals(auth)) {
                        c.send(new Gson().toJson(notification));
                    }
                } else {
                    removeList.add(c);
                }
            }
        }else {
            for (Connection c : players) {
                if (c.session.isOpen()) {
                    c.send(new Gson().toJson(notification));
                } else {
                    removeList.add(c);
                }
            }
        }



        // Clean up any connections that were left open.
        for (var c : removeList) {
            players.remove(c);
            connections.put(gameID, players);
        }
    }
}
