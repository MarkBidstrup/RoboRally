/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.Gear;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {
    private PriorityAntenna antenna;
    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private List<CheckPoint> checkPoints = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private boolean stepMode;

    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }

        this.stepMode = false;

    }

    // @author Xiao Chen
    public void setCheckpoint(int x, int y) {
        int highestCheckpoint = CheckPoint.getHighestCheckPointNumber();
        CheckPoint cp1 = new CheckPoint(x, y, highestCheckpoint + 1);
        spaces[x][y].setCheckPoint(cp1);
        checkPoints.add(cp1);
    }

    // @author Xiao Chen
    public void sortCheckPointsInNumberOrder() {
        Comparator<CheckPoint> c = Comparator.comparingInt(CheckPoint::getCheckpointNumber);
        checkPoints.sort(c);
    }

    public Integer getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    public int getPlayersNumber() {
        return players.size();
    }

    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    public Player getCurrentPlayer() {
        return current;
    }

    public void setCurrentPlayer(Player player) {
        if (player != this.current && players.contains(player)) {
            this.current = player;
            notifyChange();
        }
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    public boolean isStepMode() {
        return stepMode;
    }

    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space   the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        if (space.getWalls().contains(heading)) {
            return null;
        }

        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        //return getSpace(x, y);
        Heading reverse = Heading.values()[(heading.ordinal() + 2) % Heading.values().length];
        Space result = getSpace(x, y);
        if (result != null) {
            if (result.getWalls().contains(reverse)) {
                return null;
            }
        }
        return result;
    }

    public String getStatusMessage() {
        // this is actually a view aspect, but for making assignment V1 easy for
        // the students, this method gives a string representation of the current
        // status of the game

        int checkpoint = getCurrentPlayer().getCheckPointReached();
        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep() +
                ", CheckPoints collected: " + checkpoint;
    }

    public PriorityAntenna getAntenna() {
        return antenna;
    }

    // @author Xiao Chen
    public void setPriorityAntenna(int x, int y, Heading faces) {
        // priority antennas are always on an edge of the board
        if ((x == 0 || x == spaces.length - 1) || (y == 0 || y == spaces[0].length - 1)) {
            // create the priority-antenna
            antenna = new PriorityAntenna(x, y, faces);

            // a priority antenna acts as if it has 4 walls around it
            spaces[x][y].setWalls(Heading.NORTH);
            spaces[x][y].setWalls(Heading.SOUTH);
            spaces[x][y].setWalls(Heading.EAST);
            spaces[x][y].setWalls(Heading.WEST);
        }
    }

    // @author Xiao Chen
    public int calculateDistanceToPriorityAntenna(Space space) {
        int x_distance = Math.abs(space.x - antenna.getPriorityAntenna_xcoord());
        int y_distance = Math.abs(space.y - antenna.getPriorityAntenna_ycoord());
        return x_distance + y_distance;
    }

    // @author Xiao Chen
    public void sortPlayersAccordingToPriority() {
        // sort players according to distance from antenna
        Comparator<Player> c = (o1, o2) -> {
            if (calculateDistanceToPriorityAntenna(o1.getSpace()) < calculateDistanceToPriorityAntenna(o2.getSpace()))
                return -1;
            else if (calculateDistanceToPriorityAntenna(o1.getSpace()) > calculateDistanceToPriorityAntenna(o2.getSpace()))
                return 1;
            else { // if two or more players have the same distance, sort them according to the sweep/rotate test
                if (antenna.getPriorityAntenna_heading() == Heading.NORTH) {
                    // if antenna faces north, it is on the bottom edge, and sweeps clockwise from left to right
                    if (o1.getSpace().x < o2.getSpace().x)
                        return -1;
                    else return 1;
                } else if (antenna.getPriorityAntenna_heading() == Heading.EAST) {
                    // if antenna faces east, it is on the left edge, and sweeps clockwise from up to down
                    if (o1.getSpace().y < o2.getSpace().y)
                        return -1;
                    else return 1;
                } else if (antenna.getPriorityAntenna_heading() == Heading.SOUTH) {
                    // if antenna faces south, it is on the top edge, and sweeps clockwise from right to left
                    if (o1.getSpace().x > o2.getSpace().x)
                        return -1;
                    else return 1;
                } else if (antenna.getPriorityAntenna_heading() == Heading.WEST) {
                    // if antenna faces west, it is on the right edge, and sweeps clockwise from down to up
                    if (o1.getSpace().y > o2.getSpace().y)
                        return -1;
                    else return 1;
                } else return 0; // this should technically not be possible
            }
        };
        players.sort(c);
    }

    public List<CheckPoint> getCheckPoints() {
        return checkPoints;
    }     // @author Xiao Chen

    public int getPlayerIndex(Player player) {
        return players.indexOf(player);
    }

    // @author Xiao Chen
    public void sortPlayersAccordingToName() {
        Comparator<Player> c = Comparator.comparing(Player::getName);
        players.sort(c);
    }

    // @author Xiao Chen
    public void replacePlayerAtPositionIndex(int index, Player player) {
        if (players.get(index) != player) {
            players.remove(index);
            players.add(index, player);
        }
    }
}
