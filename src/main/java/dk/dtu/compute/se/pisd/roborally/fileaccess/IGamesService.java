package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.util.List;

// @author Xiao Chen
public interface IGamesService {
    public GameStateTemplate getGameStateTemplate(String boardname_gameID);
    public boolean addGameStateTemplate(GameStateTemplate p);
    public boolean updateGameStateTemplate(GameStateTemplate p);
    public boolean deleteGameStateTemplate(String boardname_gameID);
    public List<String> getAvailablePlayers(String boardname_gameID);
    public boolean joinLoadedGame(String boardname_gameID, String playerName);
    public boolean allPlayersJoined(String boardname_gameID);
    public void leaveJoinedGame(String boardname_gameID, String playerName);
    public boolean addActiveGame(String boardname_gameID);
    public List<String> getActiveGames();
}
