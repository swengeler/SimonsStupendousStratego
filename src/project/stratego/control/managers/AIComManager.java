package project.stratego.control.managers;

import project.stratego.ai.search.ExpectiNegamaxAI;
import project.stratego.ai.search.RandomAI;
import project.stratego.ai.search.AbstractAI;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;

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
        setAIMode("expectinegamax", 1);
    }

    private AbstractAI currentAI;
    private boolean isActive;

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
        isActive = false;
    }

    void configureSinglePlayer() {
        isActive = true;
    }

    void setAIMode(String aiType, int playerIndex) {
        if (aiType.equals("random")) {
            currentAI = new RandomAI(playerIndex);
        } else if (aiType.equals("expectinegamax")) {
            currentAI = new ExpectiNegamaxAI(playerIndex);
        }
    }

    public void tryBoardSetup(GameState state) {
        if (isActive && currentAI != null) {
            currentAI.copyOpponentSetup(state);
            currentAI.makeBoardSetup(state);
        }
    }

    public void tryNextMove(Move move) {
        if (isActive && currentAI != null && move.getPlayerIndex() != currentAI.getPlayerIndex()) {
            Move nextMove = currentAI.getNextMove(move);
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (isActive && currentAI != null) {
            currentAI.applyMove(move);
        }
    }

    public void reset() {
        setAIMode("expectinegamax", 1);
    }

}
