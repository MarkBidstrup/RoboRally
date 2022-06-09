package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// @author Xiao Chen
class PriorityAntennaTest {
    private GameController gameController;

    @BeforeEach
    void setUp() {
        Board board = LoadBoard.loadBoard("EasyIntro");
        gameController = new GameController(board);
        for (int i = 0; i < 2; i++) {
            Player player = new Player(board, null,"Player " + i);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    // UTC 11
    @Test
    void doActionTest() {
        Board board = gameController.board;
        Player current = board.getPlayer(0);
        board.sortPlayersAccordingToPriority();
        // check that player 1 goes first because clockwise sweep hits them first
        assertEquals(current, board.getCurrentPlayer());

        // after player 1 is moved far away, check that player 2 now goes first
        current.setSpace(board.getSpace(7,5));
        board.sortPlayersAccordingToPriority();
        assertEquals(board.getPlayer(1),board.getCurrentPlayer());
    }
}