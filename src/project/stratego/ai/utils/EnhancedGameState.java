package project.stratego.ai.utils;

import project.stratego.ai.tests.AITestsMain;
import project.stratego.game.entities.*;
import project.stratego.game.moves.*;
import project.stratego.game.utils.*;

import java.io.*;
import java.util.*;

/**
 * A class used in the AI part of the code. Aside from the information inherited from GameState (the board
 * state and the players), EnhancedGameState also contains a HashMap storing information about the
 * probability distribution (in respect to the type/rank) of every piece of the opponent of the player the
 * EnhancedGameState instance is assigned to.
 * */
public class EnhancedGameState extends GameState {

    public static final double PROB_EPSILON = 0.05;

    private double[][] tileEvaluationArray;

    private HashMap<Piece, double[]> probabilitiesMap;
    private int[] remainingPieceCount;

    private Stack<MoveInformation> moveInformationStack;
    private Stack<AssignmentInformation> assignmentHistory;

    // from the perspective of the player whose pieces are all known
    // the opponent (1 - playerIndex) is associated with the probabilitiesMap
    private int playerIndex;
    private int playerWonIndex;
    private int nrMoveRevealedPieces;

    public EnhancedGameState(int playerIndex) {
        super();
        this.playerIndex = playerIndex;
        remainingPieceCount = PieceType.pieceQuantity.clone();
        tileEvaluationArray = new double[10][10];
        // initTileEvalArray();
        moveInformationStack = new Stack<>();
        assignmentHistory = new Stack<>();
        playerWonIndex = -1;
        nrMoveRevealedPieces = 0;
    }

    @Override
    public void applyMove(Move move) {
        lastUpdated = 0;
        //printProbabilitiesTable();
        if (move.getOrRow() == -1) {
            return;
        }

        if (move instanceof AIMove) {
            lastMoveAIMove = 1;
        } else {
            lastMoveAIMove = 0;
        }

        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        if (movingPiece == null) {
            /*System.out.println("movingPiece in EnhancedGameState null after " + move);
            printBoard();
            System.exit(1);*/
            return;
        }

        Piece encounteredPiece = board[move.getDestRow()][move.getDestCol()].getOccupyingPiece();
        Piece opponentPiece = movingPiece.getPlayerType().ordinal() == playerIndex ? encounteredPiece : movingPiece;
        MoveInformation moveInformation = new MoveInformation(move, movingPiece, encounteredPiece);

        if (probabilitiesMap == null) {
            System.out.println(move);
            printBoard();
            System.out.println("probabilitiesMap null");
            System.exit(1);
        }

        if (opponentPiece != null && probabilitiesMap.get(opponentPiece) == null) {
            System.out.println("opponent piece not in probabilitiesMap: ");
            System.out.println(move);
            System.out.println("opponent's piece: " + opponentPiece);
            System.out.println("opponent has " + getPlayer(1 - playerIndex).getActivePieces().size() + " pieces and dead " + getPlayer(1 - playerIndex).getDeadPieces().size());
            System.out.println("we have " + getPlayer(playerIndex).getActivePieces().size() + " pieces and dead " + getPlayer(playerIndex).getDeadPieces().size());
            int playerOnBoard = 0, opponentOnBoard = 0;
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if (board[row][col].getOccupyingPiece() != null && board[row][col].getOccupyingPiece().getPlayerType().ordinal() == playerIndex) {
                        playerOnBoard++;
                    } else if (board[row][col].getOccupyingPiece() != null) {
                        opponentOnBoard++;
                    }
                }
            }
            System.out.println("opponent on board: " + opponentOnBoard);
            System.out.println("we on board: " + playerOnBoard);
            System.out.println(opponentPiece);

            printBoard();
            System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
            printBoardAssignment();
            printProbabilitiesTable();

            System.out.println("\nEXTRA:\nLast move was " + (lastMoveAIMove == 1 ? "AIMove" : "normal Move"));
            if (lastUpdated == 0) {
                undoLastMove();
            } else if (lastUpdated == 1) {
                undoLastAssignment();
            } else {
                System.out.println("wat");
                System.exit(1);
            }

            printBoard();
            System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
            printBoardAssignment();
            printProbabilitiesTable();

            Set<Piece> pieces = probabilitiesMap.keySet();

            for (int i = 0; i < PieceType.values().length - 1; i++) {
                double sum = 0;
                for (Piece q : pieces) {
                    sum += probabilitiesMap.get(q)[i];
                }
                System.out.println(PieceType.values()[i] + " -- " + sum);
            }
            System.out.println();

            int c = 2;
            while (c < 10 && !moveInformationStack.isEmpty()) {
                System.out.println("BACK BY " + c);

                undoLastMove();
                printBoard();
                System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                printBoardAssignment();
                printProbabilitiesTable();

                pieces = probabilitiesMap.keySet();

                for (Piece q : pieces) {
                    double sum = 0;
                    for (double val : probabilitiesMap.get(q)) {
                        sum += val;
                    }
                    System.out.println(q + " -- " + sum);
                }

                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    double sum = 0;
                    for (Piece q : pieces) {
                        sum += probabilitiesMap.get(q)[i];
                    }
                    System.out.println(PieceType.values()[i] + " -- " + sum);
                }
                System.out.println();
                c++;
            }

            System.exit(1);
        }

        // not strictly speaking necessary, could also just adapt the checkIfAttackWins() method in ProbabilisticMoveManager
        // to search for a piece with value 1 somewhere and if there isn't one it must be a move from the actual game
        MoveManager moveManager;
        if (!(move instanceof AIMove)) {
            moveManager = new DiscreteMoveManager(board);
        } else {
            moveManager = new ProbabilisticMoveManager(board, probabilitiesMap);
        }

        // perform move
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());

        moveInformation.setMoveResult(moveManager.lastMoveResult());
        moveInformationStack.add(moveInformation);

        // depending on the outcome of the move, update probabilities
           
        /*
        	1) Flag always has the highest value
        	2) Spy.value = 1/2 * Marshal.value
        	3) If the spy is in game: Marshal.value = Marshal.value * 0.8 
        	4) If =< 3 Miners: Miners.value = Miners.value * (4 - Miners.count)
        	5) If =< 3 Scouts: Scouts.value = Scouts.value * (4 - Scouts.count)
        	6) Bomb.value = Opponents Highest piece value * 0.5
        	7) For every piece type, piece.value += 1/(piece.count)    // EX: If there are 5 scouts, scout.value += 1/5
        	8) If the opponent has a spy: Marshal.value = Marshal.value * 0.5
        	9) For every piece that is know, give a penalty. Value ????
        	10) For a specific piece type, if piece.type.count > than opponent.piece.type.count then piece.type.value += value (?) //Ex: I have 3 miners, opponent has 2 then my miners get an increased value
        */
        
        // what should matter for probabilities is whether the opponent's piece was moved or revealed
        // mappings would not have to be removed from the hash-map, instead they should just be set to 1
        // which would produce the same effect for the other probabilities as removing them
        // -> that means that we have to go through all 40 pieces for evaluation though

        if (movingPiece.getPlayerType().ordinal() != playerIndex) {
            probabilitiesMap.put(movingPiece, probabilitiesMap.remove(movingPiece));
        }

        // first check whether (a) the opponent moved a piece or (b) we encountered a piece of the opponent
        // if not there is no need to update probabilities
        if (opponentPiece == null) {
            return;
        }

        moveInformation.setPreviousProbabilities(probabilitiesMap);

        // if the flag (or the piece which was assumed to be the flag) was captured, set playerWonIndex to the player's
        // index who made the capturing move (note: might be easier to have a new MoveResult GAMEWON in the MoveManager)
        moveInformation.setPreviousPlayerWonIndex(playerWonIndex);
        if (encounteredPiece != null && encounteredPiece.getPlayerType().ordinal() == playerIndex && encounteredPiece.getType() == PieceType.FLAG) {
            // current player lost
            playerWonIndex = 1 - playerIndex;
        } else if (encounteredPiece != null && encounteredPiece.getPlayerType().ordinal() != playerIndex && (Math.abs(getProbability(encounteredPiece, PieceType.FLAG) - 1.0) < /*2 * */PROB_EPSILON || encounteredPiece.isRevealed() && encounteredPiece.getType() == PieceType.FLAG)) {
            // current player won
            playerWonIndex = playerIndex;
        }

        // only if the move was performed in the actual game should the probabilities of the piece be updated
        // according to the outcome of the encounter, otherwise the piece was already assigned a probability
        if (!(move instanceof AIMove) && encounteredPiece != null /*&& movingPiece != encounteredPiece*/) {
            // check whether opponent's piece was encountered with our move or the other way around
            // then, regardless of the outcome of that move, the piece should be revealed
            int typeIndex = opponentPiece.getType().ordinal();
            double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
            for (int i = 0; i < probabilitiesArray.length; i++) {
                if (i != typeIndex) {
                    probabilitiesArray[i] = 0.0;
                } else {
                    probabilitiesArray[i] = 1.0;
                    //remainingPieceCount[i]--;
                }
            }
            //System.out.println("CHECK 1: " + move);
            updateProbabilities();
            if (movingPiece.getPlayerType().ordinal() != playerIndex) {
                updateUnmovablePiecesProbabilities();
            }
            return;
        }

        // if it was the opponent's piece that moved and it moved further than 1 step it has to be a SCOUT
        if (move.length() > 1 && move.getPlayerIndex() == opponentPiece.getPlayerType().ordinal()) {
            double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
            for (int i = 0; i < probabilitiesArray.length; i++) {
                if (i != PieceType.SCOUT.ordinal()) {
                    probabilitiesArray[i] = 0;
                } else {
                    probabilitiesArray[i] = 1;
                    //remainingPieceCount[i]--;
                }
            }
            updateProbabilities();
            if (movingPiece.getPlayerType().ordinal() != playerIndex) {
                updateUnmovablePiecesProbabilities();
            }
            return;
        }

        // the last possibility is a simple move of the opponent's piece which means it cannot be a FLAG or a BOMB
        probabilitiesMap.get(opponentPiece)[PieceType.BOMB.ordinal()] = 0.0;
        probabilitiesMap.get(opponentPiece)[PieceType.FLAG.ordinal()] = 0.0;
        updateProbabilities();
        if (movingPiece.getPlayerType().ordinal() != playerIndex) {
            updateUnmovablePiecesProbabilities();
        }

    }

    /* new methods for the "enhanced" implementation */

    public void undoLastMove() {
        MoveInformation lastMoveInfo = moveInformationStack.pop();
        if (lastMoveInfo.getMoveResult() == MoveResult.MOVE && lastMoveInfo.getMovingPieceReference().getPlayerType().ordinal() != playerIndex) {
            // in this case the move was performed by the opponent, possibly revealing that it is a SCOUT or NOT a BOMB or FLAG
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().remove(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getMovingPieceClone());
            board[lastMoveInfo.getDestRow()][lastMoveInfo.getDestCol()].setOccupyingPiece(null);
            //board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getOrRow()][lastMoveInfo.getOrCol()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());

            probabilitiesMap.put(lastMoveInfo.getMovingPieceClone(), probabilitiesMap.remove(lastMoveInfo.getMovingPieceClone()));
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
        } else if (lastMoveInfo.getMoveResult() == MoveResult.MOVE) {
            // in this case our own piece moved and has to be set to the last position
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().remove(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getMovingPieceClone());
            board[lastMoveInfo.getDestRow()][lastMoveInfo.getDestCol()].setOccupyingPiece(null);
            //board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getOrRow()][lastMoveInfo.getOrCol()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKTIE) {
            // in this case both pieces have to be added to the respective players' active pieces lists again
            //board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getOrRow()][lastMoveInfo.getOrCol()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getDeadPieces().remove(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getMovingPieceClone());
//            if (!lastMoveInfo.getMovingPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getMovingPieceClone());
//            }

            board[lastMoveInfo.getDestRow()][lastMoveInfo.getDestCol()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getDeadPieces().remove(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getEncounteredPieceClone());
//            if (!lastMoveInfo.getEncounteredPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getEncounteredPieceClone());
//            }
//
//            if (!lastMoveInfo.getPlayersPieceClone(1 - playerIndex).isRevealed()) {
//                remainingPieceCount[lastMoveInfo.getPlayersPieceClone(1 - playerIndex).getType().ordinal()]++;
//            }

            if (lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex) {
                probabilitiesMap.put(lastMoveInfo.getEncounteredPieceClone(), probabilitiesMap.remove(lastMoveInfo.getEncounteredPieceClone()));
            } else {
                probabilitiesMap.put(lastMoveInfo.getMovingPieceClone(), probabilitiesMap.remove(lastMoveInfo.getMovingPieceClone()));
            }
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
            playerWonIndex = lastMoveInfo.getPreviousPlayerWonIndex();
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKWON) {
            // in that case the piece that moved and won should be set to the original position again and the original piece should be removed from the player; the defeated piece has to be re-added
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().remove(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getMovingPieceClone());
//            if (!lastMoveInfo.getMovingPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getMovingPieceClone());
//            }
            //board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getOrRow()][lastMoveInfo.getOrCol()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());

            board[lastMoveInfo.getDestRow()][lastMoveInfo.getDestCol()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getDeadPieces().remove(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getEncounteredPieceClone());
//            if (!lastMoveInfo.getEncounteredPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getEncounteredPieceClone());
//            }
//
//            if (!lastMoveInfo.getPlayersPieceClone(1 - playerIndex).isRevealed()) {
//                remainingPieceCount[lastMoveInfo.getPlayersPieceClone(1 - playerIndex).getType().ordinal()]++;
//            }

            if (lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex) {
                probabilitiesMap.put(lastMoveInfo.getEncounteredPieceClone(), probabilitiesMap.remove(lastMoveInfo.getEncounteredPieceClone()));
            } else {
                probabilitiesMap.put(lastMoveInfo.getMovingPieceClone(), probabilitiesMap.remove(lastMoveInfo.getMovingPieceClone()));
            }
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
            playerWonIndex = lastMoveInfo.getPreviousPlayerWonIndex();
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKLOST) {
            // in that case the moving piece has to be re-added and set to the original position
            //board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getOrRow()][lastMoveInfo.getOrCol()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getDeadPieces().remove(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getMovingPieceClone());
//            if (!lastMoveInfo.getMovingPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getMovingPieceClone());
//            }

            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getActivePieces().remove(lastMoveInfo.getEncounteredPieceReference());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getActivePieces().add(lastMoveInfo.getEncounteredPieceClone());
//            if (!lastMoveInfo.getEncounteredPieceClone().isRevealed()) {
//                getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).getHiddenPieces().add(lastMoveInfo.getEncounteredPieceClone());
//            }
            //board[lastMoveInfo.getEncounteredPieceClone().getRowPos()][lastMoveInfo.getEncounteredPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceReference());
            board[lastMoveInfo.getDestRow()][lastMoveInfo.getDestCol()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());

//            if (!lastMoveInfo.getPlayersPieceClone(1 - playerIndex).isRevealed()) {
//                remainingPieceCount[lastMoveInfo.getPlayersPieceClone(1 - playerIndex).getType().ordinal()]++;
//            }

            if (lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex) {
                probabilitiesMap.put(lastMoveInfo.getEncounteredPieceClone(), probabilitiesMap.remove(lastMoveInfo.getEncounteredPieceClone()));
            } else {
                probabilitiesMap.put(lastMoveInfo.getMovingPieceClone(), probabilitiesMap.remove(lastMoveInfo.getMovingPieceClone()));
            }
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
            playerWonIndex = lastMoveInfo.getPreviousPlayerWonIndex();
        }
        updateProbabilities();
    }

    /**
     * This method is used to copy a setup created either by the other player or by the makeBoardSetup() method
     * of whichever AI is using the EnhancedGameState instance into that instance. The pieces which are now on the
     * board are then inserted into the HashMap storing the type probabilities and the probability values are
     * initialised.
     * */
    public void copySetup(GameState state, int playerIndex) {
        super.copySetup(state, playerIndex);
        if (this.playerIndex != playerIndex && getPlayer(playerIndex).getActivePieces().size() != 0) {
            probabilitiesMap = new HashMap<>(40);
            try (BufferedReader br = new BufferedReader(new FileReader("res\\mirroredprobs.txt"))) {
                for (Piece p : getPlayer(playerIndex).getActivePieces()) {
                    probabilitiesMap.put(p, new double[PieceType.values().length - 1]);
                }
                String line;
                int readRow = 0, readCol = 0, actualRow = 0, actualCol = 0;
                while ((line = br.readLine()) != null) {
                    if (line.length() != 0 && line.charAt(0) == '(') {
                        readRow = Integer.parseInt(line.charAt(1) + "");
                        readCol = Integer.parseInt(line.charAt(3) + "");
                    } else if (line.length() != 0 && !line.startsWith(" ")) {
                        String[] array = line.split(":");
                        actualRow = playerIndex == PlayerType.NORTH.ordinal() ? 9 - readRow : readRow;
                        actualCol = playerIndex == PlayerType.NORTH.ordinal() ? 9 - readCol : readCol;
                        probabilitiesMap.get(board[actualRow][actualCol].getOccupyingPiece())[PieceType.valueOf(array[0]).ordinal()] = Double.parseDouble(array[1].trim());
                    }
                }
                //System.out.println("Probabilities initialised, opponent has " + getPlayer(1 - playerIndex).getActivePieces().size() + " pieces");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void interpretAndCopySetup(String encodedSetup) {
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        String example2 = "SERGEANT SCOUT MINER BOMB SERGEANT MINER MINER BOMB FLAG BOMB " +
                "BOMB CAPTAIN SPY MAJOR BOMB LIEUTENANT SCOUT CAPTAIN LIEUTENANT BOMB SERGEANT " +
                "MINER SCOUT COLONEL MAJOR BOMB LIEUTENANT MARSHAL MAJOR LIEUTENANT COLONEL " +
                "GENERAL CAPTAIN SCOUT SERGEANT SCOUT SCOUT SCOUT MINER CAPTAIN SCOUT";
        String example3 = "CAPTAIN SCOUT SERGEANT GENERAL CAPTAIN SCOUT SCOUT MARSHAL SCOUT CAPTAIN " +
                "LIEUTENANT SCOUT MAJOR LIEUTENANT BOMB SCOUT MAJOR MAJOR COLONEL MINER " +
                "SERGEANT COLONEL SPY MINER BOMB SCOUT CAPTAIN LIEUTENANT LIEUTENANT BOMB " +
                "MINER BOMB SERGEANT BOMB SERGEANT SCOUT MINER MINER BOMB FLAG";
        String[] pieceEncodings = encodedSetup.split(" ");
        int counter = 0;
        // note that since this counts up/down from the back row of the setup this has to be taken into account when a setup is supplied
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].setOccupyingPiece(new Piece(PieceType.valueOf(pieceEncodings[counter++]), PlayerType.values()[playerIndex]));
                //System.out.println("EnhancedGameState");
                getPlayer(playerIndex).getActivePieces().add(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
                //getPlayer(playerIndex).getHiddenPieces().add(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
            }
        }
    }

    public void interpretEncodedSetup(String encodedSetup, int playerIndex) {
        super.interpretEncodedSetup(encodedSetup, playerIndex);

        if (this.playerIndex != playerIndex && getPlayer(playerIndex).getActivePieces().size() != 0) {
            probabilitiesMap = new HashMap<>(40);
            double[] initProbabilities = new double[PieceType.values().length - 1];
            /*for (int i = 0; i < initProbabilities.length; i++) {
                initProbabilities[i] = ((double) PieceType.pieceQuantity[i]) / getPlayer(playerIndex).getActivePieces().size();
            }
            for (Piece p : getPlayer(playerIndex).getActivePieces()) {
                probabilitiesMap.put(p, initProbabilities.clone());
                if (playerIndex == PlayerType.NORTH.ordinal() && p.getRowPos() > 1) {
                    probabilitiesMap.get(p)[PieceType.FLAG.ordinal()] = 0.005;
                } else if (playerIndex == PlayerType.SOUTH.ordinal() && p.getRowPos() < 8) {
                    probabilitiesMap.get(p)[PieceType.FLAG.ordinal()] = 0.005;
                }
            }
            updateProbabilities();*/
            try (BufferedReader br = new BufferedReader(new FileReader("res\\mirroredprobs.txt"))) {
                for (Piece p : getPlayer(playerIndex).getActivePieces()) {
                    probabilitiesMap.put(p, new double[PieceType.values().length - 1]);
                }
                String line;
                int readRow = 0, readCol = 0, actualRow = 0, actualCol = 0;
                while ((line = br.readLine()) != null) {
                    if (line.length() != 0 && line.charAt(0) == '(') {
                        readRow = Integer.parseInt(line.charAt(1) + "");
                        readCol = Integer.parseInt(line.charAt(3) + "");
                    } else if (line.length() != 0 && !line.startsWith(" ")) {
                        String[] array = line.split(":");
                        actualRow = playerIndex == PlayerType.NORTH.ordinal() ? 9 - readRow : readRow;
                        actualCol = playerIndex == PlayerType.NORTH.ordinal() ? 9 - readCol : readCol;
                        probabilitiesMap.get(board[actualRow][actualCol].getOccupyingPiece())[PieceType.valueOf(array[0]).ordinal()] = Double.parseDouble(array[1].trim());
                    }
                }
                //System.out.println("Probabilities initialised, opponent has " + getPlayer(1 - playerIndex).getActivePieces().size() + " pieces");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

   /* public void interpretEncodedBoard(String encodedBoard) {
        super.interpretEncodedBoard(encodedBoard);
        // make probability table for opponent
        probabilitiesMap = new HashMap<>(40);
        double[] initProbabilities = new double[PieceType.values().length - 1];
        try (BufferedReader br = new BufferedReader(new FileReader("res\\savefile.txt"))) {
            for (Piece p : getPlayer(1 - playerIndex).getActivePieces()) {
                probabilitiesMap.put(p, new double[PieceType.values().length - 1]);
            }
            String line;
            int readRow = 0, readCol = 0, actualRow = 0, actualCol = 0;
            while ((line = br.readLine()) != null) {
                if (line.length() != 0 && line.charAt(0) == '(') {
                    readRow = Integer.parseInt(line.charAt(1) + "");
                    readCol = Integer.parseInt(line.charAt(3) + "");
                } else if (line.length() != 0 && !line.startsWith(" ")) {
                    String[] array = line.split(":");
                    actualRow = (1 - playerIndex) == PlayerType.NORTH.ordinal() ? 9 - readRow : readRow;
                    actualCol = (1 - playerIndex) == PlayerType.NORTH.ordinal() ? 9 - readCol : readCol;
                    probabilitiesMap.get(board[actualRow][actualCol].getOccupyingPiece())[PieceType.valueOf(array[0]).ordinal()] = Double.parseDouble(array[1].trim());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public int getPlayerIndex() {
        return playerIndex;
    }

    public int getPlayerWonIndex() {
        return playerWonIndex;
    }

    public boolean isGameOver() {
        return playerWonIndex != -1;
    }

    public boolean playerWon() {
        return playerWonIndex == playerIndex;
    }

    public double getProbability(Piece piece, PieceType type) {
        return getProbability(piece, type.ordinal());
    }

    public double getProbability(Piece piece, int typeIndex) {
        if (probabilitiesMap.get(piece) == null) {
            return 0;
        }
        return probabilitiesMap.get(piece)[typeIndex];
    }

    /**
     * A method that checks whether a piece is "technically"/effectively revealed by the fact that it has a
     * probability of 1 for a certain rank/type.
     * */
    public boolean probabilityRevealed(Piece piece) {
        for (double prob : probabilitiesMap.get(piece)) {
            if (Math.abs(prob - 1.0) < /*2 * */PROB_EPSILON) {
                return true;
            } else if (prob >= /*2 * */PROB_EPSILON) {
                return false;
            }
        }
        return false;
    }

    public void assignPieceType(Piece piece, PieceType assignedType) {
        lastUpdated = 1;
        String lastStringThing = "Probability for piece at (" + piece.getRowPos() + "|" + piece.getColPos() + ") to be " + assignedType + ": " + getProbability(piece, assignedType.ordinal());
        //System.out.println(assignedType + " assigned to " + piece);
        double[] probabilitiesArray = probabilitiesMap.get(piece);
        assignmentHistory.add(new AssignmentInformation(piece, probabilitiesMap));
        for (int i = 0; i < probabilitiesArray.length; i++) {
            if (i != assignedType.ordinal()) {
                probabilitiesArray[i] = 0;
            } else {
                probabilitiesArray[i] = 1;
            }
        }
        updateProbabilities();
    }

    public void undoLastAssignment() {
        AssignmentInformation assignmentInfo = assignmentHistory.pop();
        //System.out.println("Undo assignment for " + assignmentInfo.getAssignedPiece());
        assignmentInfo.replaceProbabilities(probabilitiesMap);
        updateProbabilities();
    }

    private int lastUpdated = -1;

    private int lastMoveAIMove = -1;

    public void checkDebugCondition(int code) {
        int counter;
        for (Piece p : getPlayer(1 - playerIndex).getActivePieces()) {
            counter = 0;
            for (double probability : probabilitiesMap.get(p)) {
                if ((Math.abs(probability - 1.0) < /*2 * */PROB_EPSILON && counter != p.getType().ordinal()) || (Math.abs(probability) < /*2 * */PROB_EPSILON && counter == p.getType().ordinal())) {
                    System.out.println("\nCancelled with code " + code + " because " + (counter != p.getType().ordinal() ? "piece has probability 1.0 for wrong rank" : "piece has probability 0.0 for right rank") + " (" + p.getType() + " at (" + p.getRowPos() + "|" + p.getColPos() + "))");
                    printBoard();
                    System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                    printBoardAssignment();
                    printProbabilitiesTable();

                    System.out.println("\nEXTRA:\nLast move was " + (lastMoveAIMove == 1 ? "AIMove" : "normal Move" + ": " + moveInformationStack.peek().toMoveString()));
                    if (lastUpdated == 0) {
                        undoLastMove();
                    } else if (lastUpdated == 1) {
                        undoLastAssignment();
                    } else {
                        System.out.println("wat");
                        System.exit(1);
                    }

                    printBoard();
                    System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                    printBoardAssignment();
                    printProbabilitiesTable();

                    Set<Piece> pieces = probabilitiesMap.keySet();

                    for (Piece q : pieces) {
                        double sum = 0;
                        for (double val : probabilitiesMap.get(q)) {
                            sum += val;
                        }
                        System.out.println(p + " -- " + sum);
                    }

                    for (int i = 0; i < PieceType.values().length - 1; i++) {
                        double sum = 0;
                        for (Piece q : pieces) {
                            sum += probabilitiesMap.get(q)[i];
                        }
                        System.out.println(PieceType.values()[i] + " -- " + sum);
                    }
                    System.out.println();

                    int c = 2;
                    while (c < 10 && !moveInformationStack.isEmpty()) {
                        System.out.println("BACK BY " + c);

                        undoLastMove();
                        printBoard();
                        System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                        printBoardAssignment();
                        printProbabilitiesTable();

                        pieces = probabilitiesMap.keySet();

                        for (Piece q : pieces) {
                            double sum = 0;
                            for (double val : probabilitiesMap.get(q)) {
                                sum += val;
                            }
                            System.out.println(q + " -- " + sum);
                        }

                        for (int i = 0; i < PieceType.values().length - 1; i++) {
                            double sum = 0;
                            for (Piece q : pieces) {
                                sum += probabilitiesMap.get(q)[i];
                            }
                            System.out.println(PieceType.values()[i] + " -- " + sum);
                        }
                        System.out.println();
                        c++;
                    }

                    AITestsMain.printStats();

                    System.exit(1);
                }
                counter++;
            }
        }
    }

    public void checkDebugDepthThreeCondition(int code) {
        if ((getPlayer(playerIndex).getActivePieces().size() + getPlayer(playerIndex).getDeadPieces().size() != 40) || (getPlayer(1 - playerIndex).getActivePieces().size() + getPlayer(1 - playerIndex).getDeadPieces().size() != 40)) {
            System.out.println("DEBUG WITH CODE " + code);
            System.out.println("opponent has " + getPlayer(1 - playerIndex).getActivePieces().size() + " pieces and dead " + getPlayer(1 - playerIndex).getDeadPieces().size());
            System.out.println("we have " + getPlayer(playerIndex).getActivePieces().size() + " pieces and dead " + getPlayer(playerIndex).getDeadPieces().size());
            if ((getPlayer(playerIndex).getActivePieces().size() + getPlayer(playerIndex).getDeadPieces().size() != 40)) {
                System.out.println("Own live pieces:");
                for (Piece p : getPlayer(playerIndex).getActivePieces()) {
                    System.out.println(p);
                }
                System.out.println("\nOwn dead pieces:");
                for (Piece p : getPlayer(playerIndex).getDeadPieces()) {
                    System.out.println(p);
                }
            } else {
                System.out.println("Opponents live pieces:");
                for (Piece p : getPlayer(1 - playerIndex).getActivePieces()) {
                    System.out.println(p);
                }
                System.out.println("\nOwn dead pieces:");
                for (Piece p : getPlayer(1 - playerIndex).getDeadPieces()) {
                    System.out.println(p);
                }
            }
            int playerOnBoard = 0, opponentOnBoard = 0;
            for (int row = 0; row < 10; row++) {
                for (int col = 0; col < 10; col++) {
                    if (board[row][col].getOccupyingPiece() != null && board[row][col].getOccupyingPiece().getPlayerType().ordinal() == playerIndex) {
                        playerOnBoard++;
                    } else if (board[row][col].getOccupyingPiece() != null) {
                        opponentOnBoard++;
                    }
                }
            }
            System.out.println("opponent on board: " + opponentOnBoard);
            System.out.println("we on board: " + playerOnBoard);

            printBoard();
            System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
            printBoardAssignment();
            printProbabilitiesTable();

            System.out.println("\nEXTRA:\nLast move was " + (lastMoveAIMove == 1 ? "AIMove" : "normal Move"));
            if (lastUpdated == 0) {
                undoLastMove();
            } else if (lastUpdated == 1) {
                undoLastAssignment();
            } else {
                System.out.println("wat");
                System.exit(1);
            }

            printBoard();
            System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
            printBoardAssignment();
            printProbabilitiesTable();

            Set<Piece> pieces = probabilitiesMap.keySet();

            for (int i = 0; i < PieceType.values().length - 1; i++) {
                double sum = 0;
                for (Piece q : pieces) {
                    sum += probabilitiesMap.get(q)[i];
                }
                System.out.println(PieceType.values()[i] + " -- " + sum);
            }
            System.out.println();

            int c = 2;
            while (c < 10 && !moveInformationStack.isEmpty()) {
                System.out.println("BACK BY " + c);

                undoLastMove();
                printBoard();
                System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                printBoardAssignment();
                printProbabilitiesTable();

                pieces = probabilitiesMap.keySet();

                for (Piece q : pieces) {
                    double sum = 0;
                    for (double val : probabilitiesMap.get(q)) {
                        sum += val;
                    }
                    System.out.println(q + " -- " + sum);
                }

                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    double sum = 0;
                    for (Piece q : pieces) {
                        sum += probabilitiesMap.get(q)[i];
                    }
                    System.out.println(PieceType.values()[i] + " -- " + sum);
                }
                System.out.println();
                c++;
            }

            System.exit(1);
        }
    }

    private void updateUnmovablePiecesProbabilities() {
        // in order to recognise when all moving pieces have been "move-revealed" the counter nrMoveRevealedPieces is used
        // when it reaches totalNrPieces - nrUnmovablePieces = 40 - 7 = 33 then all remaining unrevealed pieces have to be
        // either BOMB or FLAG, so the probabilities for other pieces should be set to 0
        int pieceMoveRevealedCounter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            if (probabilitiesMap.get(p)[PieceType.FLAG.ordinal()] == 0.0 && probabilitiesMap.get(p)[PieceType.BOMB.ordinal()] == 0.0) {
                pieceMoveRevealedCounter++;
            }
        }
        if (pieceMoveRevealedCounter == 33) {
            for (Piece p : probabilitiesMap.keySet()) {
                if (probabilitiesMap.get(p)[PieceType.FLAG.ordinal()] != 0.0) {
                    for (int i = 2; i < PieceType.values().length - 1; i++) {
                        probabilitiesMap.get(p)[i] = 0.0;
                    }
                }
            }
        }
    }

    private void updateProbabilities() {
        //ArrayList<Piece> pieces = getPlayer(1 - playerIndex).getActivePieces();
        /*int[] remainingPieceCount = new int[PieceType.values().length - 1];
        ArrayList<Piece> pieces = new ArrayList<>(40);
        for (Piece p : probabilitiesMap.keySet()) {
            if (!p.isRevealed()) {
                remainingPieceCount[p.getType().ordinal()]++;
                pieces.add(p);
            }
        }*/
        Set<Piece> pieces = probabilitiesMap.keySet();
        double[] currentProbabilities;
        boolean updated = false;
        long before = System.nanoTime();
        int counter = 0;
        while (!updated) {
            counter++;/*
            if (counter % 1000 == 0) {
                System.out.println(counter);
            }*/
            updated = true;

            if (counter > 200000) {
                System.out.println("\nCounter too large");
                printBoard();
                System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                printBoardAssignment();
                printProbabilitiesTable();

                for (Piece p : pieces) {
                    double sum = 0;
                    for (double val : probabilitiesMap.get(p)) {
                        sum += val;
                    }
                    System.out.println(p + " -- " + sum);
                }

                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    double sum = 0;
                    for (Piece p : pieces) {
                        sum += probabilitiesMap.get(p)[i];
                    }
                    System.out.println(PieceType.values()[i] + " -- " + sum);
                }
                System.out.println();

                System.out.println("\nEXTRA:\nLast move was " + (lastMoveAIMove == 1 ? "AIMove" : "normal Move") + ": " + moveInformationStack.peek().toMoveString());
                if (lastUpdated == 0) {
                    undoLastMove();
                } else if (lastUpdated == 1) {
                    undoLastAssignment();
                } else {
                    System.out.println("wat");
                    System.exit(1);
                }

                printBoard();
                System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                printBoardAssignment();
                printProbabilitiesTable();

                int c = 2;
                while (c < 3 && !moveInformationStack.isEmpty()) {
                    System.out.println("BACK BY " + c + " (" + moveInformationStack.peek().toMoveString() + ")");

                    undoLastMove();
                    printBoard();
                    System.out.println("OPPONENT'S DEAD PIECES:\n" + opponentDeadPiecesToString());
                    printBoardAssignment();
                    printProbabilitiesTable();

                    Set<Piece> piecesInMap = probabilitiesMap.keySet();

                    for (Piece q : piecesInMap) {
                        double sum = 0;
                        for (double val : probabilitiesMap.get(q)) {
                            sum += val;
                        }
                        System.out.println(q + " -- " + sum);
                    }

                    for (int i = 0; i < PieceType.values().length - 1; i++) {
                        double sum = 0;
                        for (Piece q : piecesInMap) {
                            sum += probabilitiesMap.get(q)[i];
                        }
                        System.out.println(PieceType.values()[i] + " -- " + sum);
                    }
                    System.out.println();
                    c++;
                }

                AITestsMain.printStats();

                System.exit(1);
            }

            // go through each rank
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                double sum = 0;

                for (Piece p : pieces) {
                    // sum += p.prob(rank associated with i);
                    sum += probabilitiesMap.get(p)[i];
                }

                sum /= PieceType.pieceQuantity[i];
                //sum /= remainingPieceCount[i];

                if (Math.abs(1 - sum) > PROB_EPSILON) {
                    updated = false;
                    for (Piece p : pieces) {
                        // p.prob(rank associated with i) /= sum;
                        probabilitiesMap.get(p)[i] /= sum;
                    }
                }
            }

            for (Piece p : pieces) {
                currentProbabilities = probabilitiesMap.get(p);
                double sum = 0;

                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    // sum += p.prob(rank associated with i);
                    sum += currentProbabilities[i];
                }

                if (Math.abs(1 - sum) > PROB_EPSILON) {
                    updated = false;
                    for (int i = 0; i < PieceType.values().length - 1; i++) {
                        // p.prob(rank associated with i) /= sum;
                        currentProbabilities[i] /= sum;
                    }
                }
            }
        }
        rankProbabilitiesThing();

        long difference = System.nanoTime() - before;
    }

    private void rankProbabilitiesThing() {
        // for each rank loop through all pieces, if there are PieceType.pieceQuantity[rank] pieces with probability almost 1.0, then set all others to 0.0
        int oneCount;
        double pieceProbabilityCount;
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            oneCount = 0;
            pieceProbabilityCount = 0;
            for (Piece p : probabilitiesMap.keySet()) {
                if (Math.abs(probabilitiesMap.get(p)[i] - 1.0) < /*2 * */PROB_EPSILON) {
                    oneCount++;
                    pieceProbabilityCount += 1.0;
                } else if (Math.abs(probabilitiesMap.get(p)[i]) > /*2 * */PROB_EPSILON) {
                    pieceProbabilityCount += probabilitiesMap.get(p)[i];
                }
            }
            if (oneCount == PieceType.pieceQuantity[i]) {
                for (Piece p : probabilitiesMap.keySet()) {
                    if (Math.abs(probabilitiesMap.get(p)[i] - 1.0) < /*2 * */PROB_EPSILON) {
                        probabilitiesMap.get(p)[i] = 1.0;
                    } else {
                        probabilitiesMap.get(p)[i] = 0.0;
                    }
                }
            }
        }
    }

    public void printProbabilitiesTable() {
        System.out.print("TABLE OF PROBABILITIES:\n" + probabilitiesTableToReadableString());
    }

    public String probabilitiesTableToReadableString() {
        String probabilitiesTableString = "";
        probabilitiesTableString += "--------------";
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            probabilitiesTableString += "---------";
        }
        probabilitiesTableString += "\n";

        probabilitiesTableString += "|            |";
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            if (PieceType.values()[i] == PieceType.MAJOR) {
                probabilitiesTableString += " MJ (" + PieceType.pieceQuantity[i] + ") |";
            } else {
                probabilitiesTableString += " " + PieceType.values()[i].toString().substring(0, 2) + " (" + PieceType.pieceQuantity[i] + ") |";
            }
        }
        probabilitiesTableString += "\n";

        probabilitiesTableString += "--------------";
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            probabilitiesTableString += "---------";
        }
        probabilitiesTableString += "\n";

        StringBuilder stringBuilder = new StringBuilder();
        Formatter format = new Formatter(stringBuilder);
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            //probabilitiesTableString += "| (" + p.getRowPos() + "|" + p.getColPos() + ") |";
            probabilitiesTableString += "| (" + p.getRowPos() + "|" + p.getColPos() + ") - " + (p.getType() == PieceType.MAJOR ? "MJ" : p.getType().toString().substring(0, 2)) + " |";
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                stringBuilder.setLength(0);
                format.format(" %.4f |", probabilitiesMap.get(p)[i]);
                probabilitiesTableString += stringBuilder.toString();
            }

            probabilitiesTableString += "\n--------------";
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                probabilitiesTableString += "---------";
            }
            probabilitiesTableString += "\n";

            if (counter % 10 == 9 && counter != 39) {
                probabilitiesTableString += "|            |";
                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    if (PieceType.values()[i] == PieceType.MAJOR) {
                        probabilitiesTableString += " MJ (" + PieceType.pieceQuantity[i] + ") |";
                    } else {
                        probabilitiesTableString += " " + PieceType.values()[i].toString().substring(0, 2) + " (" + PieceType.pieceQuantity[i] + ") |";
                    }
                }
                probabilitiesTableString += "\n";

                probabilitiesTableString += "--------------";
                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    probabilitiesTableString += "---------";
                }
                probabilitiesTableString += "\n";
            }
            counter++;
        }
        return probabilitiesTableString;
    }

    public void printBoardAssignment() {
        System.out.print("BOARD ASSIGNMENT:\n" + boardAssignmentsToReadableString());
    }

    public String boardAssignmentsToReadableString() {
        String boardAssignmentString = "";
        for (int row = 0; row < BOARD_SIZE; row++) {
            boardAssignmentString += "-------------------------------------------------------------------------------------------\n|";
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].isAccessible()) {
                    boardAssignmentString += "   ~~   |";
                } else if (board[row][col].getOccupyingPiece() == null) {
                    boardAssignmentString += "        |";
                } else {
                    if (board[row][col].getOccupyingPiece().getPlayerType().ordinal() == playerIndex) {
                        if (board[row][col].getOccupyingPiece().getType() == PieceType.MAJOR) {
                            boardAssignmentString += " MJ (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                        } else {
                            boardAssignmentString += " " + board[row][col].getOccupyingPiece().getType().toString().substring(0, 2) + " (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                        }
                    } else if (probabilityRevealed(board[row][col].getOccupyingPiece())) {
                        PieceType assignedType = PieceType.UNKNOWN;
                        int counter = 0;
                        boolean found = false;
                        double[] temp = probabilitiesMap.get(board[row][col].getOccupyingPiece());
                        for (int i = 0; !found && i < PieceType.values().length - 1; i++) {
                            if (Math.abs(temp[i] - 1.0) < /*2 * */PROB_EPSILON) {
                                assignedType = PieceType.values()[counter];
                                break;
                            }
                            counter++;
                        }
                        if (assignedType == PieceType.MAJOR) {
                            boardAssignmentString += " MJ (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                        } else {
                            boardAssignmentString += " " + assignedType.toString().substring(0, 2) + " (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                        }
                    } else {
                        boardAssignmentString += " " + PieceType.UNKNOWN.toString().substring(0, 2) + " (" + board[row][col].getOccupyingPiece().getPlayerType().toString().substring(0, 1) + ") |";
                    }
                }
            }
            boardAssignmentString += "\n|";
            for (int col = 0; col < BOARD_SIZE; col++) {
                if (!board[row][col].isAccessible()) {
                    boardAssignmentString += "   ~~   |";
                } else if (board[row][col].getOccupyingPiece() == null) {
                    boardAssignmentString += "        |";
                } else {
                    boardAssignmentString += "  (" + (board[row][col].getOccupyingPiece().isRevealed() ? "rv" : (board[row][col].getOccupyingPiece().isMoveRevealed() ? "mr" : "nr")) + ")  |";
                }
            }
            boardAssignmentString += "\n";
        }
        boardAssignmentString += "-------------------------------------------------------------------------------------------\n";
        return boardAssignmentString;
    }

    public String opponentDeadPiecesToString() {
        String deadPiecesString = "";
        for (Piece p : getPlayer(1 - playerIndex).getDeadPieces()) {
            deadPiecesString += p + " where probability for that type is: " + probabilitiesMap.get(p)[p.getType().ordinal()] + "\n";
        }
        return deadPiecesString;
    }

   /*
    private void updateNewProbabilities(){


        board[x][y].getOccupyingPiece;
        //Check around if there are anny opponent's pieces, 2 square distance
        check : board[x][y-2].isOccupied || board[x][y-1].isOccupied || board[x][y+1].isOccupied || board[x][y+2].isOccupied
            || board[x-1][y-1].isOccupied || board[x-1][y].isOccupied || board[x-1][y+1].isOccupied || board[x-2][y].isOccupied
            || board[x+1][y-1].isOccupied || board[x+1][y].isOccupied || board[x+1][y+1].isOccupied || board[x+2][y].isOccupied
        //For each ally piece in a range of two squares check if it goes towards it or away

        Move.OriginX
        Move.OriginY
        Move.DestinX
        Move.DestinY

        get.AllyPieceX
        get.AllyPieceY

        //Checks if the unknown piece goes towards or away from my piece
        originSubstraction = (get.AllyPieceX - Move.OriginX) + (get.AllyPieceY - Move.OriginY)
        destinationSubstraction = (get.AllyPieceX - Move.DestinX) + (get.AllyPieceY - Move.DestinY)

       //CASE 1 & 2

       int p = 50;

       If(originSubstraction >= destination Substraction && AllyPiece.isRevealed){
               get.AllyPieceProbabilityMap

            for (AllyPiece.ProbabilityMap.rank > AllyPiece.rank){
                AllyPiece.ProbabilityMap.rank += (AllyPiece.ProbabilityMap.rank * p/100)
            }
            for (AllyPiece.ProbabilityMap.rank <= AllyPiece.rank){
                AllyPiece.ProbabilityMap.rank -= (AllyPiece.ProbabilityMap.rank * p/100)
            }

       }

       If(originSubstraction < destination Substraction && AllyPiece.isRevealed){
               get.AllyPieceProbabilityMap

            for (AllyPiece.ProbabilityMap.rank > AllyPiece.rank){
                AllyPiece.ProbabilityMap.rank -= (AllyPiece.ProbabilityMap.rank * p/100)
            }
            for (AllyPiece.ProbabilityMap.rank <= AllyPiece.rank){
                AllyPiece.ProbabilityMap.rank += (AllyPiece.ProbabilityMap.rank * p/100)
            }

       }

    }

    */

}
