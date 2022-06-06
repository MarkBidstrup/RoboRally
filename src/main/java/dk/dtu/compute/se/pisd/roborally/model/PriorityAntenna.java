package dk.dtu.compute.se.pisd.roborally.model;

public class PriorityAntenna { // @author Xiao Chen
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
