package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// @author Xiao Chen
class GearTest {
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

    // UTC 10
    @Test
    void doActionTest() {
        Board board = gameController.board;
        Player current = board.getPlayer(1);
        current.setSpace(board.getSpace(5,1));
        Heading expected = current.getHeading().next();
        gameController.switchTurnAndRegister(current,1);
        assertEquals(expected,current.getHeading());
    }
}