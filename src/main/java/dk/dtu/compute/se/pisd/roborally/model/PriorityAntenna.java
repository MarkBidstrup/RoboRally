package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import java.util.ArrayList;
import java.util.List;

// @author Xiao Chen
public class PriorityAntenna {
    private int priorityAntenna_xcoord;
    private int priorityAntenna_ycoord;
    private Heading priorityAntenna_heading;

    public PriorityAntenna(int x, int y, Heading heading) {
        // set the space that has the priority-antenna
        priorityAntenna_xcoord = x;
        priorityAntenna_ycoord = y;
        priorityAntenna_heading = heading;
    }

    public Heading getPriorityAntenna_heading() {
        return priorityAntenna_heading;
    }

    public int getPriorityAntenna_xcoord() {
        return priorityAntenna_xcoord;
    }

    public int getPriorityAntenna_ycoord() {
        return priorityAntenna_ycoord;
    }

}
