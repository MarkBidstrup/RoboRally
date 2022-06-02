package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.Heading;

import java.util.ArrayList;
import java.util.List;

public class PlayerTemplate { // @author Xiao Chen
    public String playerName;
    public String playerColor;
    public int checkPointTokenReached;

    public int x;
    public int y;
    public Heading heading;

    public List<CommandCardFieldTemplate> program = new ArrayList<>();
    public List<CommandCardFieldTemplate> cards = new ArrayList<>();
}
