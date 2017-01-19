package project.stratego.control.managers;

import project.stratego.ai.evaluation.MarksEvaluationFunction;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.ai.search.*;
import project.stratego.ai.tests.AITestsMain;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.StrategoGame;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PlayerType;

public class AIComManager {

    private static AIComManager instance;

    public static AIComManager getInstance() {
        if (instance == null) {
            instance = new AIComManager();
            //instance.run();
        }
        return instance;
    }

    private AIComManager() {
        setPrimaryAI("expectinegamax", 1);
    }

    private AbstractAI primaryAI, secondaryAI;

    private GameMode gameMode;
    private boolean gameLoaded;

    private boolean aiMatchRunning;
    private Move lastMove;

    void configureMultiPlayer() {
        gameMode = GameMode.MULTIPLAYER;
        aiMatchRunning = false;
    }

    void configureSinglePlayer() {
        gameMode = GameMode.SINGLEPLAYER;
        aiMatchRunning = false;
    }

    void configureAIMatch() {
        gameMode = GameMode.AIMATCH;
    }

    void configureAIShowMatch() {
        gameMode = GameMode.AISHOWMATCH;
    }

    void setGameLoaded(boolean gameLoaded) {
        this.gameLoaded = gameLoaded;
    }

    void setPrimaryAI(String aiType, int playerIndex) {
        if (aiType.startsWith("random")) {
            primaryAI = new RandomAI(playerIndex);
        } else if (aiType.startsWith("expectinegamax")) {
            String[] parts = aiType.split(" ");
            int maxDepth;
            if (parts.length > 1) {
                maxDepth = Integer.parseInt(parts[1]);
            } else {
                maxDepth = 2;
            }
            primaryAI = new ExpectiNegamaxAI(playerIndex, maxDepth);
        } else if (aiType.startsWith("mcts")) {
            primaryAI = new MonteCarloTreeSearchAI(playerIndex);
        } else if (aiType.startsWith("star1")) {
            String[] parts = aiType.split(" ");
            int maxDepth = 2;
            if (parts.length > 1) {
                maxDepth = Integer.parseInt(parts[1]);
            }
            primaryAI = new Star1MinimaxAI(playerIndex, maxDepth);
        } else if (aiType.startsWith("iterdeepexp")) {
            String[] parts = aiType.split(" ");
            long timeLimitMillis = 3000;
            if (parts.length > 1) {
                timeLimitMillis = Long.parseLong(parts[1]);
            }
            primaryAI = new IterativeDeepeningExpectimaxAI(playerIndex, timeLimitMillis);
        }
    }

    void setSecondaryAI(String aiType, int playerIndex) {
        if (aiType.startsWith("random")) {
            secondaryAI = new RandomAI(playerIndex);
        } else if (aiType.startsWith("expectinegamax")) {
            String[] parts = aiType.split(" ");
            int maxDepth;
            if (parts.length > 1) {
                maxDepth = Integer.parseInt(parts[1]);
            } else {
                maxDepth = 2;
            }
            secondaryAI = new ExpectiNegamaxAI(playerIndex, maxDepth);
        } else if (aiType.startsWith("mcts")) {
            secondaryAI = new MonteCarloTreeSearchAI(playerIndex);
        } else if (aiType.startsWith("star1")) {
            String[] parts = aiType.split(" ");
            int maxDepth = 2;
            if (parts.length > 1) {
                maxDepth = Integer.parseInt(parts[1]);
            }
            secondaryAI = new Star1MinimaxAI(playerIndex, maxDepth);
        } else if (aiType.startsWith("iterdeepexp")) {
            String[] parts = aiType.split(" ");
            long timeLimitMillis = 3000;
            if (parts.length > 1) {
                timeLimitMillis = Long.parseLong(parts[1]);
            }
            primaryAI = new IterativeDeepeningExpectimaxAI(playerIndex, timeLimitMillis);
        }
    }

    void setPrimaryAIEval(String primaryAIEval) {
        if (primaryAI == null) {
            return;
        }
        if (primaryAIEval.equals("default")) {
            primaryAI.setEvaluationFunction(new TestEvaluationFunction(primaryAI.getPlayerIndex()));
        } else if (primaryAIEval.equals("marks")) {
            primaryAI.setEvaluationFunction(new MarksEvaluationFunction(primaryAI.getPlayerIndex()));
        }
    }

    void setSecondaryAIEval(String secondaryAIEval) {
        if (secondaryAI == null) {
            return;

        }
        if (secondaryAIEval.equals("default")) {
            secondaryAI.setEvaluationFunction(new TestEvaluationFunction(secondaryAI.getPlayerIndex()));
        } else if (secondaryAIEval.equals("marks")) {
            secondaryAI.setEvaluationFunction(new MarksEvaluationFunction(secondaryAI.getPlayerIndex()));
        }
    }

    public void tryCopySetup(GameState state) {
        if (gameLoaded) {
            return;
        }
        // needs to be changed or method may not be necessary at all
        if (gameMode == GameMode.SINGLEPLAYER && primaryAI != null) {
            primaryAI.copyOpponentSetup(state);
        } /*else if (gameMode == GameMode.AIMATCH) {
            primaryAI.makeBoardSetup(state);
            secondaryAI.copyOpponentSetup(state);
        }*/
    }

    public void tryBoardSetup(GameState state) {
        if (gameLoaded) {
            return;
        }
        if (gameMode == GameMode.SINGLEPLAYER && primaryAI != null) {
            primaryAI.makeBoardSetup(state);
            primaryAI.copyOpponentSetup(state);
        } else if (gameMode == GameMode.AIMATCH || gameMode == GameMode.AISHOWMATCH) {
            primaryAI.makeBoardSetup(state);
            secondaryAI.makeBoardSetup(state);
            primaryAI.copyOpponentSetup(state);
            secondaryAI.copyOpponentSetup(state);
        }
    }

    public void tryNextMove(Move move) {
        if (gameLoaded) {
            return;
        }
        if (gameMode != GameMode.MULTIPLAYER && primaryAI != null && move.getPlayerIndex() != primaryAI.getPlayerIndex()) {
            Move nextMove = primaryAI.getNextMove(move);
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, primaryAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());
        } else if (gameMode != GameMode.MULTIPLAYER && primaryAI != null) {
            primaryAI.applyMove(move);
        }
    }

    public void tryLoadGame(String gameEncoding) {
        if (gameMode == GameMode.MULTIPLAYER) {
            return;
        }
        primaryAI.loadGame(gameEncoding);
        System.out.println("GAME LOADED");
        if (gameMode != GameMode.SINGLEPLAYER) {
            secondaryAI.loadGame(gameEncoding);
        }

    }

    public void advanceAIMatch() {
        if (gameMode != GameMode.AISHOWMATCH) {
            return;
        }

        if (!aiMatchRunning) {
            aiMatchRunning = true;
            lastMove = findAI(PlayerType.NORTH.ordinal()).getNextMove(new AIMove(0, -1, -1, -1, -1, false));
            //System.out.println("First " + lastMove);
            ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), lastMove.getOrRow(), lastMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), lastMove.getDestRow(), lastMove.getDestCol());
            findAI(PlayerType.NORTH.ordinal()).applyMove(new Move(lastMove));
        } else {
            AbstractAI currentAI = findAI(1 - lastMove.getPlayerIndex());
            Move nextMove = currentAI.getNextMove(new Move(lastMove));
            //System.out.println(nextMove);

            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());

            currentAI.applyMove(new Move(nextMove));
            lastMove = nextMove;
        }
    }

    public void gameOver(StrategoGame game) {
        aiMatchRunning = false;
        System.out.println("AI match finished");
        game.getGameState().printBoard();
    }

    public void runAutomaticAIMatch() {
        if (gameMode != GameMode.AIMATCH) {
            return;
        }

        aiMatchRunning = true;
        System.out.println("Start of AI match");
        Move firstMove = findAI(PlayerType.NORTH.ordinal()).getNextMove(new AIMove(0, -1, -1, -1, -1, false));
        //System.out.println("AI (NORTH) does first " + firstMove);
        ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), firstMove.getOrRow(), firstMove.getOrCol());
        ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), firstMove.getDestRow(), firstMove.getDestCol());
        findAI(PlayerType.NORTH.ordinal()).applyMove(new Move(firstMove));

        AbstractAI currentAI = secondaryAI;
        Move lastMove = firstMove;
        Move nextMove;
        int moveCounter = 1;

        long beforeOne = System.currentTimeMillis();
        long beforeTwo;

        while (aiMatchRunning) {
            moveCounter++;

            // select correct AI for this move
            currentAI = currentAI == primaryAI ? secondaryAI : primaryAI;

            beforeTwo = System.nanoTime();

            // apply last move made by other AI to current AI and thereby get the following move from the current AI
            nextMove = currentAI.getNextMove(new Move(lastMove));

            AITestsMain.addMoveSearchTime(currentAI.getPlayerIndex(), System.nanoTime() - beforeTwo);

            //System.out.println("In AIComManager: " + nextMove);

            // update the actual game board by applying the previously generated move
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());

            // also apply move to current AI
            currentAI.applyMove(new Move(nextMove));
            //currentAI.getEnhancedGameState().printBoard();

            // set the last move to be the move just generated by the current AI -> can be applied to the other AI for getting the next move
            lastMove = nextMove;
        }

        AITestsMain.addPlayTime(System.currentTimeMillis() - beforeOne);
        //AITestsMain.addNodesSearched(((ExpectiNegamaxAI) primaryAI).getNodesSearched());
        AITestsMain.addNumberMoves(moveCounter);
        //AITestsMain.addWin(primaryAI.getPlayerWonIndex());
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
