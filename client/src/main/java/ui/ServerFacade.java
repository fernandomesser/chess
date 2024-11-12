package ui;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.*;
import java.net.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData user) throws ResponseException {
        var path = "/user";
        return makeRequest("POST", path, user, AuthData.class, null);
    }

    public AuthData logIn(UserData user) throws ResponseException {
        var path = "/session";
        return makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logOut(String auth) throws ResponseException {
        var path = "/session";
        makeRequest("DELETE", path, null, null, auth);
    }

    public Collection<GameData> listGames(String auth) throws ResponseException {
        var path = "/game";
        record listGameResponse(Collection<GameData> games) {
        }
        var response = this.makeRequest("GET", path, null, listGameResponse.class, auth);
        return response.games();
    }

    public String createGame(GameData game, String auth) throws ResponseException {
        var path = "/game";
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", auth);
        JsonElement response = this.makeRequest("POST", path, game, JsonElement.class, auth);
        return new Gson().toJson(response);
    }

    public void joinGame(int gameID, String playerColor, String auth) throws ResponseException {
        var path = "/game";
        var request = new HashMap<String, Object>();
        request.put("gameID", gameID);
        request.put("playerColor", playerColor);
        this.makeRequest("PUT", path, request, null, auth);
    }

    public void clearApp() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    //add methods


    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String auth) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (auth != null) {
                http.setRequestProperty("Authorization", auth);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
