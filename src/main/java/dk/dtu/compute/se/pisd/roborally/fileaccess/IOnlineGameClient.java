package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.util.List;

/*
@author Golbas
 */
public interface IOnlineGameClient {
    boolean createGame(GameStateTemplate template);
    boolean createLobby(String boardname, String gameId, int nrOfPlayers);
    int getNumberOfJoinedPlayers(String boardname, String gameId);
    int getMaxNumberOfPlayers(String boardname, String gameId);
    List<String> getListOfLobbyGames();
    boolean joinLobbyGame(String boardname, String gameId);
    GameStateTemplate getOnlineGame(String boardname, String gameId);
}
