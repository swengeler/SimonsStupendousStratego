package project.stratego.control.managers;

import project.stratego.ai.evaluation.*;
import project.stratego.ai.search.*;
import project.stratego.ai.tests.AITestsMain;
import project.stratego.ai.utils.AIMove;
import project.stratego.ai.utils.EnhancedGameState;
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
        setPrimaryAI("expectinegamax");
    }

    private AbstractAI primaryAI, secondaryAI;

    private GameMode gameMode;
    private boolean gameLoaded;

    private boolean aiMatchRunning;
    private Move lastMove;

    public AbstractAI getPrimaryAI() {
        return primaryAI;
    }

    public AbstractAI getSecondaryAI() {
        return secondaryAI;
    }

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

    void setPrimaryAI(String aiType) {
        EnhancedGameState temp = null;
        if (primaryAI != null) {
            temp = primaryAI.getEnhancedGameState();
        }
        if (aiType.startsWith("random")) {
            primaryAI = new RandomAI(PlayerType.SOUTH.ordinal());
            if (temp != null) {
                primaryAI.copyOwnSetup(temp);
            }
        } else if (aiType.startsWith("expectimax")) {
            primaryAI = new ExpectiMinimaxAI(PlayerType.SOUTH.ordinal());
            if (temp != null) {
                primaryAI.copyOwnSetup(temp);
            }
            ExpectiMinimaxAI expectiMinimaxAI = (ExpectiMinimaxAI) primaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                expectiMinimaxAI.setIterativeDeepening(true);
                expectiMinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                expectiMinimaxAI.setIterativeDeepening(false);
                expectiMinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                expectiMinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                expectiMinimaxAI.setOpponentModelling(false);
            }
            if (parts.length > 5 && parts[5].equals("inv")) {
                expectiMinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(expectiMinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("mcts")) {
            primaryAI = new MonteCarloTreeSearchAI(PlayerType.SOUTH.ordinal());
        } else if (aiType.startsWith("star1")) {
            primaryAI = new Star1MinimaxAI(PlayerType.SOUTH.ordinal());
            if (temp != null) {
                primaryAI.copyOwnSetup(temp);
            }
            Star1MinimaxAI star1MinimaxAI = (Star1MinimaxAI) primaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                star1MinimaxAI.setIterativeDeepening(true);
                star1MinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                star1MinimaxAI.setIterativeDeepening(false);
                star1MinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                star1MinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                star1MinimaxAI.setOpponentModelling(false);
            }
            // same for move ordering
            if (parts[5].equals("m")) {
                star1MinimaxAI.setMoveOrdering(true);
            } else if (parts[5].equals("-m")) {
                star1MinimaxAI.setMoveOrdering(false);
            }
            if (parts.length > 6 && parts[6].equals("inv")) {
                star1MinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(star1MinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("star2")) {
            primaryAI = new Star2MinimaxAI(PlayerType.SOUTH.ordinal());
            if (temp != null) {
                primaryAI.copyOwnSetup(temp);
            }
            Star2MinimaxAI star2MinimaxAI = (Star2MinimaxAI) primaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                star2MinimaxAI.setIterativeDeepening(true);
                star2MinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                star2MinimaxAI.setIterativeDeepening(false);
                star2MinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                star2MinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                star2MinimaxAI.setOpponentModelling(false);
            }
            // same for move ordering
            if (parts[5].equals("m")) {
                star2MinimaxAI.setMoveOrdering(true);
            } else if (parts[5].equals("-m")) {
                star2MinimaxAI.setMoveOrdering(false);
            }
            if (parts.length > 6 && parts[6].equals("inv")) {
                star2MinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(star2MinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("iterdeepexp")) {
            String[] parts = aiType.split(" ");
            long timeLimitMillis = 3000;
            if (parts.length > 1) {
                timeLimitMillis = Long.parseLong(parts[1]);
            }
            primaryAI = new IterativeDeepeningExpectimaxAI(PlayerType.SOUTH.ordinal(), timeLimitMillis);
        }
    }

    void setSecondaryAI(String aiType) {
        EnhancedGameState temp = null;
        if (secondaryAI != null) {
            temp = secondaryAI.getEnhancedGameState();
        }
        if (aiType.startsWith("random")) {
            secondaryAI = new RandomAI(PlayerType.NORTH.ordinal());
            if (temp != null) {
                secondaryAI.copyOwnSetup(temp);
            }
        } else if (aiType.startsWith("expectimax")) {
            secondaryAI = new ExpectiMinimaxAI(PlayerType.NORTH.ordinal());
            if (temp != null) {
                secondaryAI.copyOwnSetup(temp);
            }
            ExpectiMinimaxAI expectiMinimaxAI = (ExpectiMinimaxAI) secondaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                expectiMinimaxAI.setIterativeDeepening(true);
                expectiMinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                expectiMinimaxAI.setIterativeDeepening(false);
                expectiMinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                expectiMinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                expectiMinimaxAI.setOpponentModelling(false);
            }
            if (parts.length > 5 && parts[5].equals("inv")) {
                expectiMinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(expectiMinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("mcts")) {
            secondaryAI = new MonteCarloTreeSearchAI(PlayerType.NORTH.ordinal());
            if (temp != null) {
                secondaryAI.copyOwnSetup(temp);
            }
        } else if (aiType.startsWith("star1")) {
            secondaryAI = new Star1MinimaxAI(PlayerType.NORTH.ordinal());
            if (temp != null) {
                secondaryAI.copyOwnSetup(temp);
            }
            Star1MinimaxAI star1MinimaxAI = (Star1MinimaxAI) secondaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                star1MinimaxAI.setIterativeDeepening(true);
                star1MinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                star1MinimaxAI.setIterativeDeepening(false);
                star1MinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                star1MinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                star1MinimaxAI.setOpponentModelling(false);
            }
            // same for move ordering
            if (parts[5].equals("m")) {
                star1MinimaxAI.setMoveOrdering(true);
            } else if (parts[5].equals("-m")) {
                star1MinimaxAI.setMoveOrdering(false);
            }
            if (parts.length > 6 && parts[6].equals("inv")) {
                star1MinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(star1MinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("star2")) {
            secondaryAI = new Star2MinimaxAI(PlayerType.NORTH.ordinal());
            if (temp != null) {
                secondaryAI.copyOwnSetup(temp);
            }
            Star2MinimaxAI star2MinimaxAI = (Star2MinimaxAI) secondaryAI;
            String[] parts = aiType.split(" ");
            if (parts[2].equals("i")) {
                // iterative deepening enables, so depth does not matter
                star2MinimaxAI.setIterativeDeepening(true);
                star2MinimaxAI.setTimeLimit(Long.parseLong(parts[3]));
            } else if (parts[2].equals("-i")) {
                // iterative deepening disabled, so depth has to be read out
                star2MinimaxAI.setIterativeDeepening(false);
                star2MinimaxAI.setMaxDepth(Integer.parseInt(parts[1]));
            }
            // opponent modelling does not influence the other two parameters
            if (parts[4].equals("o")) {
                star2MinimaxAI.setOpponentModelling(true);
            } else if (parts[4].equals("-o")) {
                star2MinimaxAI.setOpponentModelling(false);
            }
            // same for move ordering
            if (parts[5].equals("m")) {
                star2MinimaxAI.setMoveOrdering(true);
            } else if (parts[5].equals("-m")) {
                star2MinimaxAI.setMoveOrdering(false);
            }
            if (parts.length > 6 && parts[6].equals("inv")) {
                star2MinimaxAI.setEvaluationFunction(new VincibleEvaluationFunction(star2MinimaxAI.getPlayerIndex()));
            }
        } else if (aiType.startsWith("iterdeepexp")) {
            String[] parts = aiType.split(" ");
            long timeLimitMillis = 3000;
            if (parts.length > 1) {
                timeLimitMillis = Long.parseLong(parts[1]);
            }
            secondaryAI = new IterativeDeepeningExpectimaxAI(PlayerType.NORTH.ordinal(), timeLimitMillis);
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

    void tryBoardSetup(GameState state) {
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

    void tryBoardSetup(GameState state, String setupNorth, String setupSouth) {
        if (gameLoaded || gameMode == GameMode.SINGLEPLAYER) {
            return;
        }
        if (gameMode == GameMode.AIMATCH || gameMode == GameMode.AISHOWMATCH) {
            primaryAI.interpretAndCopyEncodedSetup(state, primaryAI.getPlayerIndex() == PlayerType.NORTH.ordinal() ? setupNorth : setupSouth);
            secondaryAI.interpretAndCopyEncodedSetup(state, secondaryAI.getPlayerIndex() == PlayerType.NORTH.ordinal() ? setupNorth : setupSouth);
            primaryAI.copyOpponentSetup(state);
            secondaryAI.copyOpponentSetup(state);
        }
    }

    void tryNextMove(Move move) {
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

    void tryLoadGame(String gameEncoding) {
        if (gameMode == GameMode.MULTIPLAYER) {
            return;
        }
        primaryAI.loadGame(gameEncoding);
        System.out.println("GAME LOADED");
        primaryAI.getEnhancedGameState().printProbabilitiesTable();
        primaryAI.getEnhancedGameState().printBoardAssignment();
        if (gameMode != GameMode.SINGLEPLAYER) {
            secondaryAI.loadGame(gameEncoding);
        }

    }

    void advanceAIMatch() {
        if (gameMode != GameMode.AISHOWMATCH) {
            return;
        }

        if (!aiMatchRunning) {
            aiMatchRunning = true;
            lastMove = getAI(PlayerType.NORTH.ordinal()).getNextMove(new AIMove(0, -1, -1, -1, -1, false));
            //System.out.println("First " + lastMove);
            ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), lastMove.getOrRow(), lastMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), lastMove.getDestRow(), lastMove.getDestCol());
            getAI(PlayerType.NORTH.ordinal()).applyMove(new Move(lastMove));
        } else {
            AbstractAI currentAI = getAI(1 - lastMove.getPlayerIndex());
            Move nextMove = currentAI.getNextMove(new Move(lastMove));
            //System.out.println(nextMove);

            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getOrRow(), nextMove.getOrCol());
            ModelComManager.getInstance().requestBoardTileSelected(-1, currentAI.getPlayerIndex(), nextMove.getDestRow(), nextMove.getDestCol());

            currentAI.applyMove(new Move(nextMove));
            lastMove = nextMove;
        }
    }

    public void gameOver(StrategoGame game) {
        if (gameMode == GameMode.MULTIPLAYER) {
            return;
        }
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
        Move firstMove = getAI(PlayerType.NORTH.ordinal()).getNextMove(new AIMove(0, -1, -1, -1, -1, false));
        //System.out.println("AI (NORTH) does first " + firstMove);
        ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), firstMove.getOrRow(), firstMove.getOrCol());
        ModelComManager.getInstance().requestBoardTileSelected(-1, PlayerType.NORTH.ordinal(), firstMove.getDestRow(), firstMove.getDestCol());
        getAI(PlayerType.NORTH.ordinal()).applyMove(new Move(firstMove));

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

            //AITestsMain.addMoveSearchTime(currentAI.getPlayerIndex(), System.nanoTime() - beforeTwo);

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
        //AITestsMain.addNodesSearched(((ExpectiMinimaxAI) primaryAI).getLeafNodeCounter());
        AITestsMain.addNumberMoves(moveCounter);
        //AITestsMain.addResult(primaryAI.getPlayerWonIndex());
    }

    public void reset() {
        setPrimaryAI("expectinegamax");
    }

    public AbstractAI getAI(int playerIndex) {
        if (playerIndex == primaryAI.getPlayerIndex()) {
            return primaryAI;
        } else if (playerIndex == secondaryAI.getPlayerIndex()) {
            return secondaryAI;
        }
        return null;
    }

}
