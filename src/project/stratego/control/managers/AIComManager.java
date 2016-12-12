package project.stratego.control.managers;

import project.stratego.ai.search.ExpectiNegamaxAI;
import project.stratego.ai.search.RandomAI;
import project.stratego.ai.search.AbstractAI;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PlayerType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AIComManager implements Runnable {

    public static final BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>();
    public static final BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();

    private static AIComManager instance;

    public static AIComManager getInstance() {
        if (instance == null) {
            instance = new AIComManager();
            //instance.run();
        }
        return instance;
    }

    public static void putMove(Move move) {
        try {
            moveQueue.put(move);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void putCommand(String command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private AIComManager() {
        setPrimaryAI("expectinegamax", 1);
    }

    private AbstractAI primaryAI, secondaryAI;

    private GameMode gameMode;

    @Override
    public void run() {
        while(true) {
            String cmd = "";
            Move move;
            while ((move = moveQueue.poll()) != null || (cmd = commandQueue.poll()) != null) {
                if (move != null) {

                } else if (cmd != null) {

                }
            }
        }
    }

    void configureMultiPlayer() {
        gameMode = GameMode.MULTIPLAYER;
    }

    void configureSinglePlayer() {
        gameMode = GameMode.SINGLEPLAYER;
    }

    void configureAIMatch() {
        gameMode = GameMode.AIMATCH;
    }

    void setPrimaryAI(String aiType, int playerIndex) {
        if (aiType.equals("random")) {
            primaryAI = new RandomAI(playerIndex);
        } else if (aiType.equals("expectinegamax")) {
            primaryAI = new ExpectiNegamaxAI(playerIndex);
        }
    }

    void setSecondaryAI(String aiType, int playerIndex) {
        if (aiType.equals("random")) {
            secondaryAI = new RandomAI(playerIndex);
        } else if (aiType.equals("expectinegamax")) {
            secondaryAI = new ExpectiNegamaxAI(playerIndex);
        }
    }

    public void tryCopySetup(GameState state) {
        // needs to be changed or method may not be necessary at all
        if (gameMode == GameMode.SINGLEPLAYER && primaryAI != null) {
            primaryAI.makeBoardSetup(state);
        } else if (gameMode == GameMode.AIMATCH) {
            primaryAI.makeBoardSetup(state);
            secondaryAI.makeBoardSetup(state);
        }
    }

    public void tryBoardSetup(GameState state) {
        if (gameMode == GameMode.SINGLEPLAYER && primaryAI != null) {
            primaryAI.makeBoardSetup(state);
            primaryAI.copyOpponentSetup(state);
        } else if (gameMode == GameMode.AIMATCH) {
            primaryAI.makeBoardSetup(state);
            secondaryAI.makeBoardSetup(state);
            primaryAI.copyOpponentSetup(state);
            secondaryAI.copyOpponentSetup(state);
        }
    }

    public void tryNextMove(Move move) {
        if (gameMode != GameMode.MULTIPLAYER && primaryAI != null && move.getPlayerIndex() != primaryAI.getPlayerIndex()) {
            Move nextMove = primaryAI.getNextMove(move);
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (gameMode != GameMode.MULTIPLAYER && primaryAI != null) {
            primaryAI.applyMove(move);
        }
        if (gameMode == GameMode.AIMATCH && secondaryAI != null && move.getPlayerIndex() != secondaryAI.getPlayerIndex()) {
            Move nextMove = secondaryAI.getNextMove(move);
            ModelComManager.getInstance().requestBoardTileSelected(-1, secondaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, secondaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (gameMode == GameMode.AIMATCH && secondaryAI != null) {
            secondaryAI.applyMove(move);
        }
    }

    public void gameOver(StrategoGame game) {
        // if AI matches and shit are running, then document the one that just ended and start a new one
    }

    public void start() {
        if (gameMode == GameMode.AIMATCH) {
            // start game by feeding starting move to North AI
            findAI(PlayerType.NORTH.ordinal()).getNextMove(new AIMove(-1, -1, -1, -1, -1, false));
        }
    }

    public void reset() {
        setPrimaryAI("expectinegamax", 1);
    }

    private AbstractAI findAI(int playerIndex) {
        if (playerIndex == primaryAI.getPlayerIndex()) {
            return primaryAI;
        } else if (playerIndex == secondaryAI.getPlayerIndex()) {
            return secondaryAI;
        }
        return null;
    }

}
