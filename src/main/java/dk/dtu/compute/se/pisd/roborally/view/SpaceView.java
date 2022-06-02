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
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;


    public SpaceView(@NotNull Space space) {
        this.space = space;

        // XXX the following styling should better be done with styles
        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        // old code that makes the spaces black/white
//        if ((space.x + space.y) % 2 == 0) {
//            this.setStyle("-fx-background-color: white;");
//        } else {
//            this.setStyle("-fx-background-color: black;");
//        }

        // makes empty spaces look like metal plates
        String path= Objects.requireNonNull(this.getClass().getResource("../../../../../../../image/")).toString();
        String imgURL = path + "EmptySpace.png";
        this.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT+";");


//         updatePlayer();

        // This space view should listen to changes of the space
        space.attach(this);
        update(space);
    }

    private void updatePlayer() {
        this.getChildren().clear();

        Player player = space.getPlayer();
        if (player != null) {

            // old code that makes arrows instead of robots
//            Polygon arrow = new Polygon(0.0, 0.0,
//                    10.0, 20.0,
//                    20.0, 0.0 );

            // makes the robots look like actual robots
            String path= Objects.requireNonNull(this.getClass().getResource("../../../../../../../image/")).toString();
            String imgURL = path + player.getColor() + "robot.png";
            String repeat = "no-repeat";
            StackPane stackPane = new StackPane();

            String playerColor = player.getColor();

            switch (playerColor) {
                case "blue":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
                case "red":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
                case "green":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
                case "orange":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
                case "grey":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
                case "magenta":
                    stackPane.setStyle("-fx-background-image: url("+imgURL+"); -fx-background-size: "+SPACE_HEIGHT*0.80+"; -fx-background-repeat: "+repeat+";");
                    break;
            }

            // old code that colored the arrows
//            try {
//                arrow.setFill(Color.valueOf(player.getColor()));
//            } catch (Exception e) {
//                arrow.setFill(Color.MEDIUMPURPLE);
//            }

            // makes the robots rotate when they move
            stackPane.setRotate((90*player.getHeading().ordinal())%360);

            // old code that made the arrows rotate
//            arrow.setRotate((90*player.getHeading().ordinal())%360);
            this.getChildren().add(stackPane);

        }
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

}
