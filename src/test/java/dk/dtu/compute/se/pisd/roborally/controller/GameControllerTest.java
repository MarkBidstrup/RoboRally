package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = new Board(TEST_WIDTH, TEST_HEIGHT, "test");
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    // @author Deniz Isikli
    @Test
    void moveBackward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);
        gameController.moveBackward(current);

        Assertions.assertEquals(current, board.getSpace(0,1).getPlayer(), "Player " + current.getName() + " should be on Space (0,1)!");
        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should be heading NORTH!");
        Assertions.assertNull(board.getSpace(0,0).getPlayer(), "Space (0,0) should be empty!");
    }

    // @author Deniz Isikli
    @Test
    void moveUturn() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);

        Assertions.assertEquals(Heading.NORTH, current.getHeading(), "Player should be facing NORTH!");
    }

    // @author Deniz Isikli
    @Test
    void moveRight() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);
        gameController.turnRight(current);

        Assertions.assertEquals(Heading.EAST, current.getHeading(), "Player should be facing EAST!");
    }

    // @author Deniz Isikli
    @Test
    void moveLeft() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.uTurn(current);
        gameController.turnLeft(current);

        Assertions.assertEquals(Heading.WEST, current.getHeading(), "Player should be facing WEST!");
    }

    // @author Deniz Isikli
    @Test
    void moveFastFoward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);
        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 2).getPlayer(), "Player " + current.getName() + " should on Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    // @author Deniz Isikli
    @Test
    void moveSpeedRoutine() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);
        gameController.moveForward(current);
        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 3).getPlayer(), "Player " + current.getName() + " should on Space (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }

    // @author Deniz Isikli
    @Test
    void checkForWinner() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        boolean winner = board.getCurrentPlayer().getCheckPointReached() == CheckPoint.getHighestCheckPointNumber();

        Assertions.assertTrue(winner, current.getName() + " has won the game!");
    }

}