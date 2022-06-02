package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Phase;

import java.util.ArrayList;
import java.util.List;

public class GameStateTemplate { // @author Xiao Chen
    public BoardTemplate board;
    public Integer gameId;
    public List<PlayerTemplate> players = new ArrayList<>();
    public int currentPlayerIndex;
    public Phase phase;
    public int step;
}
