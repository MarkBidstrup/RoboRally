package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;

// @author Deniz Isikli, Xiao Chen & Mark Bidstrup
public interface IGameState {
    public GameStateTemplate getGameStateTemplate(String boardname_gameID);
    public boolean updateGameStateTemplate(GameStateTemplate p);
    public Integer getProgrammingCounter(String gameID);
    public void incrementProgrammingCounter(String gameID);
    public void setProgrammingCounter(String gameID, Integer value);
}
