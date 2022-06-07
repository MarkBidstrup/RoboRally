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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SavedGamesClient;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.*;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * The AppController is responsible for handling  implementation logic
 * about the App such as start,load,save,exit the gameApp.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> BOARD_OPTIONS = Arrays.asList("EasyIntro","ConveyorBeltMayhem","CheckpointChallenge");
    private String boardname;
    private String userChoice;

    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");

    final private RoboRally roboRally;

    private GameController gameController;

    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * this methode allows the player to start the new game
     */
    public void newGame() {
        ChoiceDialog<Integer> dialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialog.setTitle("Player number");
        dialog.setHeaderText("Select number of players");
        Optional<Integer> result = dialog.showAndWait();

        gameBoardDialog();

        if (result.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }

            Board board = LoadBoard.loadBoard(boardname);
            int gameId = (int) (Math.random()*10);
            // TODO - can make it so that gameId is a user choice
            board.setGameId(gameId);

            int no = result.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }


            boolean created= new SavedGamesClient().createLobby(boardname, String.valueOf(gameId), no);
            if(created == true) {
                GameStateTemplate template= LoadBoard.createGameStateTemplate(board);
                created= new SavedGamesClient().createGame(template);
                if(created==true){
                    showInfo("Info","Game created successfully.","BoardName: "+boardname+" - GameID: "+gameId);
                }else {
                    showInfo("Error","creation of game failed","Please try again!");
                }
                //gameController = new GameController(board);
               // gameController.startProgrammingPhase();
               // roboRally.createBoardView(gameController);
            }
        }
    }

    private void showInfo(String title,String header, String msg){
        AlertType type;
        if(title.equals("Info")){
            type= AlertType.INFORMATION;
        }else if(title.equals("Error")){
            type= AlertType.ERROR;
        }else{
            type= AlertType.WARNING;
        }
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    public void joinGame(){
        SavedGamesClient savedGamesClient = new SavedGamesClient();
        List<String> lobbyGamesList = savedGamesClient.getListOfLobbyGames();

        ChoiceDialog<String> gameBoard = new ChoiceDialog<>(lobbyGamesList.get(0), lobbyGamesList);
        gameBoard.setTitle("Choose game");
        gameBoard.setHeaderText("Select a game to join");
        Optional<String> gameBoardResult = gameBoard.showAndWait();

        gameBoardResult.ifPresent(s -> userChoice = s);

        String[]choice = userChoice.split(" - ");
        String boardname=choice[0].replaceAll("Board: ","");
        String gameId=choice[1].replaceAll("GameID: ","");
        boolean joined= savedGamesClient.joinLobbyGame(boardname, gameId);
        if(joined == true) {

            showInfo("Info","You joined the lobby.","Please wait for other players to join.");
            int joinedplayers = SavedGamesClient.getNumberOfJoinedPlayers(boardname, gameId);
            int totalNumber= SavedGamesClient.getMaxNumberOfPlayers(boardname, gameId);;
            int count=0;
            while(joinedplayers != totalNumber && count <10){
                joinedplayers= SavedGamesClient.getNumberOfJoinedPlayers(boardname, gameId);
                count ++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(joinedplayers == totalNumber) {
                GameStateTemplate template= SavedGamesClient.getOnlineGame(boardname,gameId);
                Board board= setupBoardFromState(template);
                gameController = new GameController(board);
                gameController.startProgrammingPhase();
                roboRally.createBoardView(gameController);
            }else{
                showInfo("Warning","players did not join","Please try again later.");
            }

        }

    }

    /**
     * this methode should provide the player to save the game
     */
    public void saveGame() {
        LoadBoard.saveGame(gameController.board);
    }

    /**
     * this methode should provide the player to load the game
     */
    public void loadGame() {             // @author Xiao Chen & Mark Bidstrup
        // use loadBoard if only loading a board (no game state info) - otherwise use loadGame
        loadGameDialog();

        // TODO - below should only run after enough players have joined lobby
        SavedGamesClient client = new SavedGamesClient();
        GameStateTemplate gameStateTemplate = client.getGameStateTemplate(userChoice);

        Board board = setupBoardFromState(gameStateTemplate);
        if (board != null && board.getPlayersNumber() > 0) {
            //TODO - POST/save gamestate to server/currentGames
            List<Player> temp = new ArrayList<>();
            for (int i = 0; i < board.getPlayersNumber(); i++)
                temp.add(board.getPlayer(i));
            board.sortPlayersAccordingToName(); // put the players in order 1,2,3,4 (as json saves players in order according to player-turn order)
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);
            for (int i = 0; i < temp.size(); i++) // following loop puts the players back in the original order (according to their turns based on priority antenna distance)
                board.replacePlayerAtPositionIndex(i, temp.get(i));
        }
        else if (board == null) // this should not happen
            newGame();
        else { // this should not happen in the gameflow - only happens if only a board without players is loaded (for testing)
            for (int i = 0; i < 4; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);
        }
    }

    private void gameBoardDialog() {             // @author Mark Bidstrup
        ChoiceDialog<String> gameBoard = new ChoiceDialog<>(BOARD_OPTIONS.get(0), BOARD_OPTIONS);
        gameBoard.setTitle("Choose board");
        gameBoard.setHeaderText("Select a game board");
        Optional<String> gameBoardResult = gameBoard.showAndWait();

        gameBoardResult.ifPresent(s -> boardname = s);
    }

    private void loadGameDialog() {             // @author Mark Bidstrup
        SavedGamesClient savedGamesClient = new SavedGamesClient();
        List<String> savedGamesList = savedGamesClient.getListOfSavedGames();

        ChoiceDialog<String> gameBoard = new ChoiceDialog<>(savedGamesList.get(0), savedGamesList);
        gameBoard.setTitle("Choose game");
        gameBoard.setHeaderText("Select a game to load");
        Optional<String> gameBoardResult = gameBoard.showAndWait();

        gameBoardResult.ifPresent(s -> userChoice = s);
    }

    private Board setupBoardFromState(GameStateTemplate template) { // @author Mark Bidstrup
        Board result;
        result = new Board(template.board.width, template.board.height, template.board.boardName);
        result.setPriorityAntenna(template.board.antenna.x, template.board.antenna.y,template.board.antenna.heading);
        CheckPoint.setHighestCheckPointNumber(0);
        for (CheckPointTemplate checkPointTemplate: template.board.checkPoints) {
            if (result.getSpace(checkPointTemplate.x, checkPointTemplate.y) != null)
                result.setCheckpoint(checkPointTemplate.x, checkPointTemplate.y);
        }
        for (SpaceTemplate spaceTemplate: template.board.spaces) {
            Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
            if (space != null) {
                space.getActions().addAll(spaceTemplate.actions);
                space.getWalls().addAll(spaceTemplate.walls);
            }
        }
        if (template.gameId != null)
            result.setGameId(template.gameId);
        for (PlayerTemplate playerTemplate: template.players) {
            Player player = new Player(result, playerTemplate.playerColor, playerTemplate.playerName);
            player.setCheckPointReached(playerTemplate.checkPointTokenReached);
            player.setSpace(result.getSpace(playerTemplate.x, playerTemplate.y));
            player.setHeading(playerTemplate.heading);
            player.setSPAMDamageCount(playerTemplate.SPAMDamageCards);
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
            result.addPlayer(player);
        }
        result.setCurrentPlayer(result.getPlayer(template.currentPlayerIndex));
        result.setPhase(template.phase);
        result.setStep(template.step);
        return result;
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {
            // here we save the game (without asking the user).
            //saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * this methode should provide the player to close the app
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    /**
     * this methode checks if the game is running
     * @return the gameController of the running game ,otherwise return the false
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
