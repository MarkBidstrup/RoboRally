package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

// @author Mark Bidstrup & Xiao Chen
public class SavedGamesClient implements IGamesService {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public List<String> getListOfSavedGames() { // Xiao Chen & Mark Bidstrup
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames"))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            result = result.replaceAll("]", "").replaceAll("\\[","").replaceAll("\"","");

            return new ArrayList<String>(Arrays.asList(result.split(",")));
        } catch (Exception e) {
            return null;
        }
    }

    public GameStateTemplate getGameStateTemplate(String boardname_gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/"+ boardname_gameID))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();
            return gson.fromJson(result, GameStateTemplate.class);
        } catch (Exception e) {
            return null;
        }
    }


    public boolean addGameStateTemplate(GameStateTemplate p) {
        try{
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();
            String boardJSON = gson.toJson(p);
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(boardJSON))
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames"))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("added");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean updateGameStateTemplate(GameStateTemplate p) {
        try{
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();
            String boardJSON = gson.toJson(p);
            String str = p.board.boardName + "_" + p.gameId;
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(boardJSON))
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/" + str))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("updated");
        } catch (Exception e) {
            return false;
        }    }

    @Override
    public boolean deleteGameStateTemplate(String boardname_gameID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/" + boardname_gameID))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("deleted");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<String> getAvailablePlayers(String boardname_gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/getAvailPlayers/" + boardname_gameID ))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            result = result.replaceAll("]", "").replaceAll("\\[","").replaceAll("\"","");
            return new ArrayList<String>(Arrays.asList(result.split(",")));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean joinLoadedGame(String boardname_gameID, String playerName) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(playerName))
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/joinLoadedGame/" + boardname_gameID  ))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("Joined");
        } catch (Exception e) {
            return false;
        }     }

    @Override
    public boolean allPlayersJoined(String boardname_gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/allJoinedStatus/" +boardname_gameID))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("Ready");
        } catch (Exception e) {
            return false;
        }    }

    @Override
    public void leaveJoinedGame(String boardname_gameID, String playerName) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(playerName))
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/leaveJoinedGame/" + boardname_gameID  ))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean addActiveGame(String boardname_gameID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(boardname_gameID))
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/activeGames"))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("Added");
        } catch (Exception e) {
            return false;
        }    }

    @Override
    public List<String> getActiveGames() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://"+Hostname.HOSTNAME +":8080/savedGames/activeGames"))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            result = result.replaceAll("]", "").replaceAll("\\[","").replaceAll("\"","");
            return new ArrayList<String>(Arrays.asList(result.split(",")));
        } catch (Exception e) {
            return null;
        }    }
}
