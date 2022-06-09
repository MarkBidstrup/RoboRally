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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Gear;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BoardView extends VBox implements ViewObserver {

    private Board board;

    private GridPane mainBoardPane;
    private SpaceView[][] spaces;

    private PlayersView playersView;

    private Label statusLabel;

    private SpaceEventHandler spaceEventHandler;

    public BoardView(@NotNull GameController gameController) {
        board = gameController.board;

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");

        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);

        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(gameController);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                drawObstacle(space, spaces[x][y]);
                drawConveyorBelt(space, spaces[x][y]);
                drawCheckPoint(space, spaces[x][y]);
                drawGear(space, spaces[x][y]);
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }
        drawAntena();
        board.attach(this);
        update(board);
    }

    // @author Golbas Haidari & Mark Bidstrup
    private void drawConveyorBelt(Space space, SpaceView spaceView){
        ConveyorBelt cb = (ConveyorBelt) space.getActions().stream().filter((FieldAction fa)-> fa.getClass().getSimpleName().equals("ConveyorBelt")).findAny().orElse(null);
        if(cb != null){
            String imgURL, beltColor;
            String path= this.getClass().getResource("../../../../../../../image/").toString();

            if (cb.getColor().equals("BLUE")) { beltColor="Blue"; }
            else{ beltColor="Green"; }

            if (cb.getHeading() == Heading.NORTH) { imgURL = path + beltColor +"Up.png"; }
            else if (cb.getHeading() == Heading.SOUTH) { imgURL = path + beltColor + "Down.png"; }
            else if (cb.getHeading() == Heading.EAST) { imgURL = path  + beltColor + "Right.png"; }
            else { imgURL = path + beltColor + "Left.png"; }

            String bgColor= "black";
            if((space.x + space.y) % 2 == 0){ bgColor= "white";}
            spaceView.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-color: "+bgColor+";-fx-background-repeat: no-repeat; -fx-background-size: "+SpaceView.SPACE_HEIGHT+"; -fx-background-position: center center;");
        }
    }

    // @author Golbas Haidari
    private void drawObstacle(Space space, SpaceView spaceView){

        BorderStrokeStyle top, right, down, left;
        top= BorderStrokeStyle.NONE;
        right= BorderStrokeStyle.NONE;
        down= BorderStrokeStyle.NONE;
        left= BorderStrokeStyle.NONE;

        if(space.getWalls().contains(Heading.NORTH)){ top= BorderStrokeStyle.SOLID;  }
        if(space.getWalls().contains(Heading.SOUTH)){ down= BorderStrokeStyle.SOLID; }
        if(space.getWalls().contains(Heading.EAST)) { right= BorderStrokeStyle.SOLID;}
        if(space.getWalls().contains(Heading.WEST)) { left=BorderStrokeStyle.SOLID;  }

        spaceView.setBorder(new Border(new BorderStroke(Color.DARKRED, Color.DARKRED, Color.DARKRED, Color.DARKRED,
                                                        top, right, down, left,
                                                        CornerRadii.EMPTY, new BorderWidths(5), Insets.EMPTY)));
    }

    // @author Golbas Haidari & Mark Bidstrup
    private void drawAntena(){
        int x= board.getAntenna().getPriorityAntenna_xcoord();
        int y= board.getAntenna().getPriorityAntenna_ycoord();
        String path=  this.getClass().getResource("../../../../../../../image/").toString()+ "antenna.png";
        spaces[x][y].setStyle("-fx-background-image: url("+path+"); -fx-background-repeat: no-repeat; -fx-background-size: "+SpaceView.SPACE_HEIGHT+" ; -fx-background-position:center center;");
    }

    // @author Golbas Haidari & Mark Bidstrup
    private void drawCheckPoint(Space space, SpaceView spaceView){
        if(space.getCheckPoint() != null){
            String path = this.getClass().getResource("../../../../../../../image/").toString() + "Checkpoint1.png";
            int checkPointNumber = space.getCheckPoint().getCheckpointNumber();
            switch (checkPointNumber) {
                case 2 -> path = this.getClass().getResource("../../../../../../../image/").toString() + "Checkpoint2.png";
                case 3 -> path = this.getClass().getResource("../../../../../../../image/").toString() + "Checkpoint3.png";
                case 4 -> path = this.getClass().getResource("../../../../../../../image/").toString() + "Checkpoint4.png";
            }
            spaceView.setStyle("-fx-background-image: url("+path+"); -fx-background-repeat: no-repeat; -fx-background-size: "+SpaceView.SPACE_HEIGHT+"; -fx-background-position:center center;");
        }
    }

    // @author Golbas Haidari & Mark Bidstrup
    private void drawGear(Space space, SpaceView spaceView) {
        Gear gear = (Gear) space.getActions().stream().filter((FieldAction fa) -> fa.getClass().getSimpleName().equals("Gear")).findAny().orElse(null);
        if (gear != null) {
            String path;
            if (gear.getHeading() == Heading.EAST)
                path = this.getClass().getResource("../../../../../../../image/").toString() + "counterclockwise.png";
            else
                path = this.getClass().getResource("../../../../../../../image/").toString() + "clockwise.png";
            spaceView.setStyle("-fx-background-image: url(" + path + "); -fx-background-repeat: no-repeat; -fx-background-size: "+SpaceView.SPACE_HEIGHT+"; -fx-background-position:center center;");
        }
    }


    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Phase phase = board.getPhase();
            statusLabel.setText(board.getStatusMessage());
        }
    }

    // XXX this handler and its uses should eventually be deleted! This is just to help test the
    //     behaviour of the game by being able to explicitly move the players on the board!
    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView) {
                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    gameController.moveCurrentPlayerToSpace(space);
                    event.consume();
                }
            }
        }

    }

}
