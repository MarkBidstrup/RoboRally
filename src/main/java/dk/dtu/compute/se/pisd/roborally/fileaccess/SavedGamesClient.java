package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.io.IOException;
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

    //Golbas
    public boolean createGame(GameStateTemplate template){
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();
        String boardJSON = gson.toJson(template);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(boardJSON))
                .uri(URI.create("http://localhost:8080/createGame/"))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("created");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Golbas
    public boolean createLobby(String boardname, String gameId, int nrOfPlayers){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/createLobby/"+boardname+"/"+gameId + "/" +nrOfPlayers))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("created");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Golbas
    public static int getNumberOfJoinedPlayers(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobby/joinedPlayers/"+boardname+"/"+gameId))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Integer.parseInt(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Golbas
    public static int getMaxNumberOfPlayers(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobby/maxPlayers/"+boardname+"/"+gameId))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return Integer.parseInt(response.body());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //Golbas
    public List<String> getListOfLobbyGames() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobbyGames"))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String result= response.body().replaceAll("]", "").replaceAll("\\[","").replaceAll("\"","");
            List<String> list= new ArrayList<String>(Arrays.asList(result.split(",")));
            return list;
        } catch (Exception e) {
            return null;
        }
    }

    //Golbas
    public boolean joinLobbyGame(String boardname, String gameId){

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/joinLobby/"+boardname+"/"+gameId))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().equals("joined");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //Golbas
    public static GameStateTemplate getOnlineGame(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/lobby/"+boardname+"/"+gameId))
                .setHeader("User-Agent", "HttpClient Bot") // add request header
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();
            return gson.fromJson(response.body(), GameStateTemplate.class);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<String> getListOfSavedGames() { // Xiao Chen & Mark Bidstrup
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8080/savedGames"))
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
                    .uri(URI.create("http://localhost:8080/savedGames/"+ boardname_gameID))
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
                    .uri(URI.create("http://localhost:8080/savedGames"))
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
                    .uri(URI.create("http://localhost:8080/savedGames/" + str))
                    .setHeader("User-Agent", "Games Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            System.out.println(result);
            return result.equals("updated");
        } catch (Exception e) {
            return false;
        }    }

    @Override
    public boolean deleteGameStateTemplate(String boardname_gameID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .DELETE()
                    .uri(URI.create("http://localhost:8080/savedGames/" + boardname_gameID))
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
}
