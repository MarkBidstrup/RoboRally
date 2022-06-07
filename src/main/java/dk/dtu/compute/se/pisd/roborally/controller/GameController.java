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

import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The GameController is responsible for handling the all
 * game request ,logic ,update the model and returns the view that should be changed.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;
    private List<Player> rebootedThisStep = new ArrayList<>();

    /**
     * This constructor takes a board as input.
     *
     * @param board the board which game should be played on.
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space) {
//        Commented out as it is not a part of the game rules
//        if (space != null && space.board == board) {
//            Player currentPlayer = board.getCurrentPlayer();
//            if (currentPlayer != null && space.getPlayer() == null) {
//                currentPlayer.setSpace(space);
//                int playerNumber = (board.getPlayerNumber(currentPlayer) + 1) % board.getPlayersNumber();
//                board.setCurrentPlayer(board.getPlayer(playerNumber));
//            }
//        }

    }

    /**
     * This methode allows the player to get some random cammand cards where players can program their robot with.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = player.getSPAMDamageCount() ; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
                for (int j = 0; j < player.getSPAMDamageCount(); j++) { // @author Deniz Isikli
                    CommandCardField field = player.getCardField(j);
                    field.setCard(new CommandCard(Command.SPAM_DAMAGE));
                    field.setVisible(true);
                }
            }
        }
    }

    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * (commands.length-1));
        return new CommandCard(commands[random]);
    }

    /**
     * This method allows the players to finish the programing phase, and
     * activates the activation phase, "Execute Program" and "Execute Current Register" buttons
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        // update player priority based on distance from priority antenna
        // this line is only in case priority was not updated at the end of the last register - i.e. at start of game
        board.sortPlayersAccordingToPriority(); // @author Xiao Chen - sorts players according to distance from priority antenna
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    public void playerFinishProgramming(Player player) { // @author Deniz Isikli
        // if the player programmed any SPAM damage cards, their SPAM-count is reduced
        int count = 0;
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            if(player.getProgramField(i).getCard() != null && player.getProgramField(i).getCard().command == Command.SPAM_DAMAGE)
                count++;
        }
        player.setSPAMDamageCount(player.getSPAMDamageCount() - count);
        finishProgrammingPhase(); // TODO - update this for web version - only switch phase after all players done
    }

    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * This methodes  executes the all programs card of all robots.
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * This methode executes the current map of the current robot,
     * so in this way the player click step by step throughout the program
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    public void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if (command.isInteractive()) {
                        board.setPhase(Phase.PLAYER_INTERACTION);
                        return;
                    } else {
                        executeCommand(currentPlayer, command);
                        if (command == Command.SPAM_DAMAGE) {
                            executeCommand(currentPlayer, command);
                            return;
                        }
                    }
                }
                switchTurnAndRegister(currentPlayer, step);
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // @author Xiao Chen
    public void executeCommandOptionAndContinue(Command command) {
        Player currentPlayer = board.getCurrentPlayer();
        board.setPhase(Phase.ACTIVATION);
        executeCommand(currentPlayer, command);
        int step = board.getStep();
        switchTurnAndRegister(currentPlayer, step);
        while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
            executeNextStep();
        }
    }

    // @author Xiao Chen
    public void switchTurnAndRegister(Player currentPlayer, int step) {
        int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
        // put the rebooted robots on the reboot field
        for (Player p : rebootedThisStep) {
            while (board.getSpace(0,3).getPlayer() != null) {
                try {
                    moveToSpace(board.getSpace(0,3).getPlayer(),board.getNeighbour(board.getSpace(0,3),Heading.EAST),Heading.EAST);
                } catch (ImpossibleMoveException e) {
                    try {
                        Heading[] headings = Heading.values();
                        int random = (int) (Math.random() * (headings.length));
                        Heading head = headings[random];
                        moveToSpace(board.getSpace(0,3).getPlayer(),board.getNeighbour(board.getSpace(0,3),head),head);
                    } catch (ImpossibleMoveException impossibleMoveException) {
                        impossibleMoveException.printStackTrace();
                    }
                    e.printStackTrace(); }
            }
            p.setSpace(board.getSpace(0,3));
            p.setHeading(Heading.EAST);
        }
        rebootedThisStep.clear();
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            // this is the end of a register
            // board elements activate at the end of each register
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Space space = board.getPlayer(i).getSpace();
                robotLaser(board.getPlayer(i), space);
                for(FieldAction action: space.getActions()){ action.doAction(this, space); }
            }
            // at the end of a register after all board elements have activated, check if any player gets a checkpoint token
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                if (board.getPlayer(i).getSpace().collectCheckpointToken()) {
                    if (board.getPlayer(i).getCheckPointReached() != CheckPoint.getHighestCheckPointNumber()) {
                        Alert a = new Alert(Alert.AlertType.NONE);
                        a.setTitle("Checkpoint token collected!");
                        a.setContentText(board.getPlayer(i).getName() + " has collected a checkpoint token!");
                        ButtonType type = new ButtonType("Ok");
                        a.getDialogPane().getButtonTypes().add(type);
                        a.show();
                    }
                }
            }
            // check if any player has won the game
            checkForWinner();
            step++;
            // update player priority based on distance from priority antenna
            board.sortPlayersAccordingToPriority();
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
        // TODO - update gamestate on server using "PUT"
    }

    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                case U_TURN:
                    this.uTurn(player);
                    break;
                case SPEED_ROUTINE:
                    this.speedRoutine(player);
                    break;
                case BACKWARD:
                    this.moveBackward(player);
                    break;
                case SPAM_DAMAGE:
                    this.spamDamage(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    /**
     * This methode moves the player's robot to the specified space
     */
    public void moveToSpace(@NotNull Player player, @NotNull Space space, @NotNull Heading heading) throws ImpossibleMoveException {
        assert board.getNeighbour(player.getSpace(), heading) == space; // make sure the move to here is possible in principle
        Player other = space.getPlayer();
        if (other != null){
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                // XXX Note that there might be additional problems with
                //     infinite recursion here (in some special cases)!
                //     We will come back to that!
                if (fallOverEdge(other, target, heading)) // checks if the pushed robot would fall over the edge
                    rebootRobot(other);
                else
                    moveToSpace(other, target, heading);

                // Note that we do NOT embed the above statement in a try catch block, since
                // the thrown exception is supposed to be passed on to the caller

                assert target.getPlayer() == null : target; // make sure target is free now
            } else {
                throw new ImpossibleMoveException(player, space, heading);
            }
        }
        if (fallOverEdge(player, space, heading)) // checks if the robot falls over the edge
            rebootRobot(player);
        else
            player.setSpace(space);
    }

    // @author Xiao Chen & Deniz Isikli
    public boolean fallOverEdge(@NotNull Player player, Space space, Heading heading) {
        if (heading == Heading.SOUTH && space.y < player.getSpace().y)
            return true;
        else if (heading == Heading.NORTH && space.y > player.getSpace().y)
            return true;
        else if (heading == Heading.WEST && space.x > player.getSpace().x)
            return true;
        else return heading == Heading.EAST && space.x < player.getSpace().x;
    }

    public void rebootRobot(@NotNull Player player) { // @author Deniz Isikli
        rebootedThisStep.add(player);
        player.incrementSPAMDamageCount();
        player.incrementSPAMDamageCount();
        // player loses their program and cannot move until the next round
        for (int i = board.getStep() + 1; i < Player.NO_REGISTERS; i++)
            player.setProgram(i, null);
    }

    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

    /**
     * This methode moves the player's robot 1 cell to the forwarded in the headed direction
     * @param player the player whose robots should be moved forward.
     */
    public void moveForward(@NotNull Player player) {
        Space space = player.getSpace();
        if (player != null && player.board == board && space != null) {
            Heading heading = player.getHeading();
            Space target = board.getNeighbour(space, heading);
            if (target != null) {
                try {
                    moveToSpace(player, target, heading);
                } catch (ImpossibleMoveException e) {
                    // we don't do anything here  for now; we just catch the
                    // exception so that we do no pass it on to the caller
                    // (which would be very bad style).
                }
                //target.setPlayer(player);
            }
        }
    }

    public void moveBackward(@NotNull Player player) { // @author Deniz Isikli
        uTurn(player);
        moveForward(player);
        if (!rebootedThisStep.contains(player))
            uTurn(player);
    }

    /**
     * This methode moves the player's robot 2 cell to the forwarded in the headed direction
     * @param player the player whose robots should be moved forward.
     */
    public void fastForward(@NotNull Player player) { // @author Deniz Isikli
        moveForward(player);
        if (!rebootedThisStep.contains(player))
            moveForward(player);
    }

    /**
     * This methode turns the player's robot 90 degree to the right
     * @param player the player whose robots should be turned.
     */
    public void turnRight(@NotNull Player player) { // @author Deniz Isikli
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().next());
        }
    }

    /**
     * This methode turns the player's robot 90 degree to the left
     * @param player the player whose robots should be turned.
     */
    public void turnLeft(@NotNull Player player) { // @author Deniz Isikli
        if (player != null && player.board == board) {
            player.setHeading(player.getHeading().prev());
        }
    }

    public void uTurn(@NotNull Player player) { // @author Deniz Isikli
        if(player != null && player.board == board) {
            turnRight(player);
            turnRight(player);
        }
    }

    public void speedRoutine(Player player) { // @author Deniz Isikli
        if(player != null && player.board == board) {
            moveForward(player);
            if (!rebootedThisStep.contains(player)) // if a robot rebooted, it should not keep moving
                moveForward(player);
            if (!rebootedThisStep.contains(player))
                moveForward(player);
        }
    }

    public void spamDamage(Player player) { // @author Deniz Isikli
        if(player != null && player.board == board) {
            int step = board.getStep();
            CommandCard temp = generateRandomCommandCard();
            player.setProgram(step, temp);
        }
    }


    // @author Xiao Chen & Deniz Isikli
    public void robotLaser(@NotNull Player player, @NotNull Space space) {
        Space firstTarget = board.getNeighbour(space, player.getHeading());

        while(firstTarget != null && firstTarget != space && !fallOverEdge(player, firstTarget, player.getHeading())) {
            if(firstTarget.getPlayer() != null && firstTarget.getPlayer() != player) {
                firstTarget.getPlayer().incrementSPAMDamageCount();
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setTitle("Player is hit by a laser!");
                a.setContentText((firstTarget.getPlayer().getName()) + " has been hit by " + player.getName() + "'s robot laser!" );                ButtonType type = new ButtonType("Ok");
                a.getDialogPane().getButtonTypes().add(type);
                a.show();

                return;
            }
            firstTarget = board.getNeighbour(firstTarget, player.getHeading());
        }
    }

    /**
     * This method allows moves the card between the commandCards fields
     * @param source the source where the card should be moved from.
     * @param target the target where the card should be moved to
     * @return return true if card can be moved successfully, otherwise return false.
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    // TODO - pull from gameStateServer
    public void updateGameServerPull() {
    }

    public void checkForWinner() { // @author Mark Bidstrup
        // if a player has collected the last token, they have won
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            if (board.getPlayer(i).getCheckPointReached() != 0
                    && board.getPlayer(i).getCheckPointReached() == CheckPoint.getHighestCheckPointNumber()) {
                board.setPhase(Phase.INITIALISATION);

                Alert a = new Alert(Alert.AlertType.NONE);
                a.setTitle("Player " + (i+1) + " wins!");
                a.setContentText("Player "+ (i+1) + " has won the game!");
                ButtonType type = new ButtonType("Ok");
                a.getDialogPane().getButtonTypes().add(type);
                a.show();

            }
        }
    }
}
