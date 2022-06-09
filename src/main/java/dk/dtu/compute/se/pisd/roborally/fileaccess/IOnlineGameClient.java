package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.util.List;

/*
@author Golbas
 */
public interface IOnlineGameClient {
    boolean createGame(GameStateTemplate template);
    int getNumberOfJoinedPlayers(String boardname, int gameId);
    int getMaxNumberOfPlayers(String boardname, int gameId);
    List<String> getOnlineGames();
    boolean joinOnlineGame(String boardname, int gameId, String payerName);
    GameStateTemplate getOnlineGame(String boardname, int gameId);
}
