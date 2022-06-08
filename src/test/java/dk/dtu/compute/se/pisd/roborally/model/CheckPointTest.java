package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckPointTest {
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

    @Test
    void doActionTest() {
        Board board = gameController.board;
        Player current = board.getPlayer(1);
        current.setSpace(board.getSpace(7,5));
        assertEquals(0, current.getCheckPointReached());
        current.getSpace().collectCheckpointToken();
        assertEquals(1,current.getCheckPointReached());
    }
}