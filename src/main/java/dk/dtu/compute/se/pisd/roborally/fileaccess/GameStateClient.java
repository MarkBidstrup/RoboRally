package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class GameStateClient implements IGameState{
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    // @author Deniz Isikli, Xiao Chen
    @Override
    public GameStateTemplate getGameStateTemplate(String boardname_gameID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8080/gameState/" + boardname_gameID))
                    .setHeader("User-Agent", "Game State Client")
                    .header("Content-Type", "application/json")
                    .build();

            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

            if(result.equals("null")) {
                return null;
            } else {
                GsonBuilder simpleBuilder = new GsonBuilder().
                        registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
                Gson gson = simpleBuilder.create();
                return gson.fromJson(result, GameStateTemplate.class);
            }
        } catch (Exception e){
            return null;
        }
    }

    // @author Xiao Chen & Mark Bidstrup
    @Override
    public boolean updateGameStateTemplate(GameStateTemplate gameStateTemplate) {
        try{
            GsonBuilder simpleBuilder = new GsonBuilder().
                    registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();
            String boardJSON = gson.toJson(gameStateTemplate);
            String boardname_ID = gameStateTemplate.board.boardName + "_" + gameStateTemplate.gameId;
            HttpRequest request;
            if (getGameStateTemplate(boardname_ID) == null) {
                request = HttpRequest.newBuilder()
                        .POST(HttpRequest.BodyPublishers.ofString(boardJSON))
                        .uri(URI.create("http://localhost:8080/gameState/"))
                        .setHeader("User-Agent", "Game State Client")
                        .header("Content-Type", "application/json")
                        .build();
            } else {
                request = HttpRequest.newBuilder()
                        .PUT(HttpRequest.BodyPublishers.ofString(boardJSON))
                        .uri(URI.create("http://localhost:8080/gameState/" + boardname_ID))
                        .setHeader("User-Agent", "Game State Client")
                        .header("Content-Type", "application/json")
                        .build();
            }
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
            return result.equals("updated");
        } catch (Exception e) {
            return false;
        }
    }

    // @author Xiao Chen & Mark Bidstrup
    @Override
    public Integer getProgrammingCounter(String gameID) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create("http://localhost:8080/gameState/"+gameID+"/programmingCounter"))
                    .setHeader("User-Agent", "Game State Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);

            return Integer.parseInt(result);
        } catch (Exception e) {
            return null;
        }
    }

    // @author Xiao Chen & Mark Bidstrup
    @Override
    public void incrementProgrammingCounter(String gameID) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(gameID))
                    .uri(URI.create("http://localhost:8080/gameState/"+gameID+"/programmingCounter/increment"))
                    .setHeader("User-Agent", "Game State Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

    // @author Xiao Chen & Mark Bidstrup
    @Override
    public void setProgrammingCounter(String gameID, Integer value) {
        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(value.toString()))
                    .uri(URI.create("http://localhost:8080/gameState/"+gameID+"/programmingCounter/set"))
                    .setHeader("User-Agent", "Game State Client")
                    .header("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            String result = response.thenApply((r)->r.body()).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
    }

}
