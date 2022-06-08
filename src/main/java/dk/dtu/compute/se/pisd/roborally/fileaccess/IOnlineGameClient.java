package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

import java.util.List;

/*
@author Golbas
 */
public interface IOnlineGameClient {
    boolean createGame(GameStateTemplate template);
    int getNumberOfJoinedPlayers(String boardname, String gameId);
    int getMaxNumberOfPlayers(String boardname, String gameId);
    List<String> getOnlineGames();
    boolean joinOnlineGame(String boardname, String gameId, int payerNr);
    GameStateTemplate getOnlineGame(String boardname, String gameId);
}
