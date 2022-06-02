package dk.dtu.compute.se.pisd.roborally.model;

// @author Xiao Chen
public class CheckPoint {
    private int CHECKPOINTNUMBER;
    private static int highestCheckPointNumber;
    private final int x;
    private final int y;

    public CheckPoint(int x, int y, int checkpointNumber) {
        this.x = x;
        this.y = y;
        CHECKPOINTNUMBER = checkpointNumber;
        if (checkpointNumber > highestCheckPointNumber)
            highestCheckPointNumber = checkpointNumber;
    }

    public int getCheckpointNumber() {
        return CHECKPOINTNUMBER;
    }

    public int getXcoordinate() {
        return x;
    }

    public int getYcoordinate() {
        return y;
    }

    public static int getHighestCheckPointNumber() {
        return highestCheckPointNumber;
    }

    public static void setHighestCheckPointNumber(int highestCheckPointNumber) {
        CheckPoint.highestCheckPointNumber = highestCheckPointNumber;
    }
}
