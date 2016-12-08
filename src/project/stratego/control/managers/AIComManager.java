package project.stratego.control.managers;

import javafx.application.Platform;
import project.stratego.ai.search.ExpectiNegamaxAI;
import project.stratego.ai.search.RandomAI;
import project.stratego.ai.search.AbstractAI;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PlayerType;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AIComManager implements Runnable {

    private static final BlockingQueue<Move> moveQueue = new LinkedBlockingQueue<>();
    private static final BlockingQueue<String> commandQueue = new LinkedBlockingQueue<>();
    private static GameState gameState;

    private static AIComManager instance;

    private boolean listenForMoves = true;

    public static void instantiate() {
        if (instance == null) {
            instance = new AIComManager();
            instance.configureMultiPlayer();
            new Thread(instance).start();
        }
    }

    static void clear() {
        moveQueue.clear();
        commandQueue.clear();
    }

    static void putMove(Move move) {
        System.out.println("CHECK 4");
        try {
            System.out.println("CHECK 3");
            moveQueue.put(move);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void putCommand(String command) {
        try {
            commandQueue.put(command);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static void putGameState(GameState state) {
        gameState = state;
    }

    private AIComManager() {
        gameMode = GameMode.SINGLEPLAYER;
        primaryAI = new ExpectiNegamaxAI(PlayerType.SOUTH.ordinal());
        new Thread(() -> {
            while (listenForMoves) {
                Move move;
                while ((move = moveQueue.poll()) != null) {
                    System.out.println("CHECK 2");
                    tryNextMove(move);
                }
            }
        }).start();
    }

    private AbstractAI primaryAI, secondaryAI;
    private GameMode gameMode;

    @Override
    public void run() {
        while (true) {
            System.out.println("CHECK LOOP");
            String cmd;
            Move move;
            Object polledObject;
            /*while ((move = moveQueue.poll()) != null) {
                System.out.println("CHECK 2");
                tryNextMove(move);
            }*/
            while ((cmd = commandQueue.poll()) != null) {
                System.out.println("CHECK 345: " + cmd);
                String[] splitCmd = cmd.split(" ");
                if (cmd.equals("configure singleplayer")) {
                    configureSinglePlayer();
                } else if (cmd.equals("configure multiplayer")) {
                    gameMode = GameMode.MULTIPLAYER;
                    instance = null;
                    return;
                } else if (cmd.equals("configure aimatch")) {
                    configureAIMatch();
                } else if (splitCmd[0].equals("setprimary")) {
                    // should probably make sure that either no game is running, that the game is reset
                    // or that different AIs can just pass the current game state around when switching
                    if (splitCmd[1].equals("random")) {
                        primaryAI = new RandomAI(PlayerType.SOUTH.ordinal());
                    } else if (splitCmd[1].equals("expectinegamax")) {
                        primaryAI = new ExpectiNegamaxAI(PlayerType.SOUTH.ordinal());
                    }
                } else if (splitCmd[0].equals("setsecondary") && gameMode != GameMode.SINGLEPLAYER) {
                    // should probably make sure that either no game is running, that the game is reset
                    // or that different AIs can just pass the current game state around when switching
                    if (splitCmd[1].equals("random")) {
                        secondaryAI = new RandomAI(PlayerType.SOUTH.ordinal());
                    } else if (splitCmd[1].equals("expectinegamax")) {
                        secondaryAI = new ExpectiNegamaxAI(PlayerType.SOUTH.ordinal());
                    }
                } else if (cmd.equals("boardsetup")) {
                    tryBoardSetup(gameState);
                } else if (cmd.equals("boardcopy")) {
                    tryCopySetup(gameState);
                } else if (cmd.equals("firstmove")) {
                    if (gameMode == GameMode.AIMATCH) {
                        tryNextMove(new AIMove(PlayerType.SOUTH.ordinal(), -1, -1, -1, -1, false));
                    }
                } else if (cmd.equals("quit")) {
                    instance = null;
                    moveQueue.clear();
                    commandQueue.clear();
                    listenForMoves = false;
                    return;
                }
            }
        }
    }

    void configureMultiPlayer() {
        gameMode = GameMode.MULTIPLAYER;
    }

    private void configureSinglePlayer() {
        System.out.println("Configure singlepalyer");
        gameMode = GameMode.SINGLEPLAYER;
        primaryAI = new ExpectiNegamaxAI(PlayerType.SOUTH.ordinal());
    }

    private void configureAIMatch() {
        gameMode = GameMode.AIMATCH;
        primaryAI = new ExpectiNegamaxAI(PlayerType.SOUTH.ordinal());
        secondaryAI = new ExpectiNegamaxAI(PlayerType.NORTH.ordinal());
    }

    private void tryCopySetup(GameState state) {
        System.out.println("CHEF*VHEPFGEAHTPÖ)§WQZHTR");
        if (primaryAI != null) {
            primaryAI.copySetup(state);
        }
        if (secondaryAI != null) {
            secondaryAI.copySetup(state);
        }
    }

    private void tryBoardSetup(GameState state) {
        if (primaryAI != null) {
            primaryAI.makeBoardSetup(state);
        }
        if (secondaryAI != null) {
            secondaryAI.makeBoardSetup(state);
        }
    }

    private void tryNextMove(Move move) {
        tryCopySetup(gameState);
        System.out.println("primaryAI: " + primaryAI);
        if (primaryAI != null && move.getPlayerIndex() != primaryAI.getPlayerIndex()) {
            System.out.println("CHECK 1");
            Move nextMove = primaryAI.getNextMove(move);
            //Platform.runLater(() -> ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol()));
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            //Platform.runLater(() -> ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol()));
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (primaryAI != null) {
            primaryAI.applyMove(move);
        }
        if (gameMode == GameMode.AIMATCH) {
            if (secondaryAI != null && move.getPlayerIndex() != secondaryAI.getPlayerIndex()) {
                Move nextMove = secondaryAI.getNextMove(move);
                Platform.runLater(() -> ModelComManager.getInstance().requestBoardTileSelected(-1, secondaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol()));
                Platform.runLater(() -> ModelComManager.getInstance().requestBoardTileSelected(-1, secondaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol()));
            } else if (secondaryAI != null) {
                secondaryAI.applyMove(move);
            }
        }
    }

    public void reset() {

    }

}
