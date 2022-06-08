package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
@author Golbas Haidari
 */
public class OnlineGameClient implements IOnlineGameClient {
    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public boolean createGame(GameStateTemplate template){
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();
        String boardJSON = gson.toJson(template);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(boardJSON))
                .uri(URI.create("http://"+Hostname.hostname+":8080/createGame/"))
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

    @Override
    public boolean createLobby(String boardname, String gameId, int nrOfPlayers){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/createLobby/"+boardname+"/"+gameId + "/" +nrOfPlayers))
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

    @Override
    public int getNumberOfJoinedPlayers(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/lobby/joinedPlayers/"+boardname+"/"+gameId))
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

    @Override
    public int getMaxNumberOfPlayers(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/lobby/maxPlayers/"+boardname+"/"+gameId))
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

    @Override
    public List<String> getListOfLobbyGames() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/lobbyGames"))
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

    @Override
    public boolean joinLobbyGame(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/joinLobby/"+boardname+"/"+gameId))
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

    @Override
    public GameStateTemplate getOnlineGame(String boardname, String gameId){
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+Hostname.hostname+":8080/lobby/"+boardname+"/"+gameId))
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
}
