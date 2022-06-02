package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpaceTest {

    private GameController gameController;
    @BeforeEach
    void setWalls() {
        Board board = new Board(8, 8, "easyintro");
        gameController = new GameController(board);

        board.getSpace(1,1).setWalls(Heading.EAST);
    }

    @Test
    void getWalls() {
        Board board= gameController.board;
        Assertions.assertEquals( true, board.getSpace(1, 1).getWalls().contains(Heading.EAST),"should return true\"");
    }

    @Test
    void playerCannotGoThroughWall(){
        Board board= gameController.board;
        Player player = new Player(board, null,"Player " + 0);
        board.addPlayer(player);
        player.setSpace(board.getSpace(1, 1));
        player.setHeading(Heading.EAST);
        gameController.moveForward(player);

        Assertions.assertEquals( board.getSpace(1,1).getPlayer(), player,"since space (1,1) has wall in east side, so it should have the same player,\"");

    }
}