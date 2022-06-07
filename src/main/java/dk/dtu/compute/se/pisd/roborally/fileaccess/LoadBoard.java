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
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.roborally.model.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String SAVEDGAMESFOLDER = "savedgames";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(String boardname) {
        if (boardname == null) {
            boardname = DEFAULTBOARD;
        }

        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + boardname + "." + JSON_EXT);
        if (inputStream == null) {
            return null;
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        Board result;
        JsonReader reader = null;
        try {
            // @author Xiao Chen
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);
            result = new Board(template.width, template.height, template.boardName);
            result.setPriorityAntenna(template.antenna.x, template.antenna.y,template.antenna.heading);
            CheckPoint.setHighestCheckPointNumber(0);
            for (CheckPointTemplate checkPointTemplate: template.checkPoints) {
                if (result.getSpace(checkPointTemplate.x, checkPointTemplate.y) != null)
                    result.setCheckpoint(checkPointTemplate.x, checkPointTemplate.y);
            }
            for (SpaceTemplate spaceTemplate: template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    space.getWalls().addAll(spaceTemplate.walls);
                }
            }
            reader.close();
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }

    // @author Xiao Chen
    public static void saveGame(Board board) {
        GameStateTemplate template = createGameStateTemplate(board);
        // use a client to save the game to the server
        SavedGamesClient client = new SavedGamesClient();
        client.addGameStateTemplate(template);
    }

    // @author Xiao Chen
    public static GameStateTemplate createGameStateTemplate(Board board) {
        GameStateTemplate template = new GameStateTemplate();
        template.board = createBoardTemplate(board, board.boardName);
        template.gameId = board.getGameId();
        template.step = board.getStep();
        template.phase = board.getPhase();
        template.currentPlayerIndex = board.getPlayerIndex(board.getCurrentPlayer());

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            PlayerTemplate playerTemplate = new PlayerTemplate();
            Player player = board.getPlayer(i);
            playerTemplate.playerColor = player.getColor();
            playerTemplate.playerName = player.getName();
            playerTemplate.x = player.getSpace().x;
            playerTemplate.y = player.getSpace().y;
            playerTemplate.heading = player.getHeading();
            playerTemplate.checkPointTokenReached = player.getCheckPointReached();
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardFieldTemplate commandCardFieldTemplate = new CommandCardFieldTemplate();
                if (player.getProgramField(j).getCard() != null)
                    commandCardFieldTemplate.command = player.getProgramField(j).getCard().command;
                else
                    commandCardFieldTemplate.command = null;
                commandCardFieldTemplate.visible = player.getProgramField(j).isVisible();
                playerTemplate.program.add(commandCardFieldTemplate);
            }
            for (int j = 0; j < Player.NO_CARDS; j++) {
                CommandCardFieldTemplate commandCardFieldTemplate = new CommandCardFieldTemplate();
                if (player.getCardField(j).getCard() != null)
                    commandCardFieldTemplate.command = player.getCardField(j).getCard().command;
                else
                    commandCardFieldTemplate.command = null;
                commandCardFieldTemplate.visible = player.getCardField(j).isVisible();
                playerTemplate.cards.add(commandCardFieldTemplate);
            }
            playerTemplate.SPAMDamageCards = player.getSPAMDamageCount();
            template.players.add(playerTemplate);
        }
        return template;
    }

    // @author Xiao Chen
    private static BoardTemplate createBoardTemplate(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.boardName = name;
        template.width = board.width;
        template.height = board.height;
        AntennaTemplate antennaTemplate = new AntennaTemplate();
        antennaTemplate.x = board.getAntenna().getPriorityAntenna_xcoord();
        antennaTemplate.y = board.getAntenna().getPriorityAntenna_ycoord();
        antennaTemplate.heading = board.getAntenna().getPriorityAntenna_heading();
        template.antenna = antennaTemplate;
        board.sortCheckPointsInNumberOrder();
        List<CheckPoint> checkpoints = board.getCheckPoints();
        for (int i = 0; i < board.getCheckPoints().size(); i++) {
            CheckPointTemplate checkPointTemplate = new CheckPointTemplate();
            checkPointTemplate.checkPointNumber = checkpoints.get(i).getCheckpointNumber();
            checkPointTemplate.x = checkpoints.get(i).getXcoordinate();
            checkPointTemplate.y = checkpoints.get(i).getYcoordinate();
            template.checkPoints.add(checkPointTemplate);
        }

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    template.spaces.add(spaceTemplate);
                }
            }
        }
        return template;
    }

    // following method only adds/updates game state information, but does not reload the board itself
    public static Board updateGameState(@NotNull Board board) {
        ClassLoader classLoader = LoadBoard.class.getClassLoader();
        // TODO - update file location/ file name
        InputStream inputStream = classLoader.getResourceAsStream(SAVEDGAMESFOLDER + "/" + (board.boardName+"_"+board.getGameId()) + "." + JSON_EXT);
        if (inputStream == null) {
            return null;
        }

        // In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
        Gson gson = simpleBuilder.create();

        // FileReader fileReader = null;
        JsonReader reader = null;
        try {
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            GameStateTemplate template = gson.fromJson(reader, GameStateTemplate.class);
            reader.close();
            // update the player information
            List<Player> temp = new ArrayList<>();
            for (PlayerTemplate playerTemplate: template.players) {
                Player player = board.getPlayer(playerTemplate.playerName);
                player.setCheckPointReached(playerTemplate.checkPointTokenReached);
                player.setSpace(board.getSpace(playerTemplate.x, playerTemplate.y));
                player.setHeading(playerTemplate.heading);
                for (int i = 0; i < Player.NO_REGISTERS; i++) {
                    CommandCardFieldTemplate commandCardFieldTemplate = playerTemplate.program.get(i);
                    if (commandCardFieldTemplate.command != null)
                        player.getProgramField(i).setCard(new CommandCard(commandCardFieldTemplate.command));
                    player.getProgramField(i).setVisible(commandCardFieldTemplate.visible);
                }
                for (int i = 0; i < Player.NO_CARDS; i++) {
                    CommandCardFieldTemplate commandCardFieldTemplate = playerTemplate.cards.get(i);
                    if (commandCardFieldTemplate.command != null)
                        player.getCardField(i).setCard(new CommandCard(commandCardFieldTemplate.command));
                    player.getCardField(i).setVisible(commandCardFieldTemplate.visible);
                }
                temp.add(player);
            }
            for (int i = 0; i < temp.size(); i++) // following loop puts the players in the correct order (according to loaded data)
                board.replacePlayerAtPositionIndex(i, temp.get(i));
            board.setCurrentPlayer(board.getPlayer(template.currentPlayerIndex));
            board.setPhase(template.phase);
            board.setStep(template.step);
            return board;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {}
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {}
            }
        }
        return null;
    }
}
