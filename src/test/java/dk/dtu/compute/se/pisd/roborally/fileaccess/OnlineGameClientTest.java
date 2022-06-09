package dk.dtu.compute.se.pisd.roborally.fileaccess;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.GameStateTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OnlineGameClientTest {

    OnlineGameClient onlineGameClient;
    RoboRally roboRally;
    AppController appController;
    Board board;
    Player player;
    GameStateTemplate template;
    List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    String boardname;
    int gameId;

    @BeforeEach
    void setUp() {
        onlineGameClient = new OnlineGameClient();
        roboRally= new RoboRally();
        appController = new AppController(roboRally);
        boardname= "EasyIntro";
        gameId=1;
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createGame() {
        board = LoadBoard.loadBoard(boardname);
        board.setGameId(gameId);

        for(int i=0; i<2; i++){
            player= new Player(board, PLAYER_COLORS.get(i),"null");
            board.addPlayer(player);
            player.setSpace(board.getSpace(i % board.width, i));
        }

        template= LoadBoard.createGameStateTemplate(board);

        boolean created =onlineGameClient.createGame(template);
        assertEquals(true, created);
    }

    @Test
    void getOnlineGame() {
        //createGame();
        List<String> list= onlineGameClient.getOnlineGames();
        assertEquals(1,list.size());
    }

    @Test
    void getMaxNumberOfPlayers() {
        int maxNr = onlineGameClient.getMaxNumberOfPlayers(boardname, gameId);
        assertEquals(2, maxNr);
    }

    @Test
    void getNumberOfJoinedPlayers_1() {
        int joinedNr = onlineGameClient.getNumberOfJoinedPlayers(boardname, gameId);
        assertEquals(0, joinedNr);
    }

    @Test
    void joinOnlineGame() {
        boolean joined = onlineGameClient.joinOnlineGame(boardname,gameId,"Golbas");
        assertEquals(true, joined);
    }

    @Test
    void getNumberOfJoinedPlayers_2() {
        int joinedNr = onlineGameClient.getNumberOfJoinedPlayers(boardname, gameId);
        assertEquals(1, joinedNr);
    }





}