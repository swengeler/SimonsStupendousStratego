package project.stratego.ai.utils;

import project.stratego.game.entities.*;
import project.stratego.game.moves.*;
import project.stratego.game.utils.*;

import java.util.*;

/**
 * A class used in the AI part of the code. Aside from the information inherited from GameState (the board
 * state and the players), EnhancedGameState also contains a HashMap storing information about the
 * probability distribution (in respect to the type/rank) of every piece of the opponent of the player the
 * EnhancedGameState instance is assigned to.
 * */
public class EnhancedGameState extends GameState {

    public static final double PROB_EPSILON = 0.001;

    private HashMap<Piece, double[]> probabilitiesMap;

    private Stack<MoveInformation> moveHistory;
    private Stack<AssignmentInformation> assignmentHistory;

    // need queue to keep track of moves here

    // from the perspective of the player whose pieces are all known
    // the opponent (1 - playerIndex) is associated with the probabilitiesMap
    private int playerIndex;

    public EnhancedGameState(int playerIndex) {
        super();
        this.playerIndex = playerIndex;
        moveHistory = new Stack<>();
        assignmentHistory = new Stack<>();
    }

    protected EnhancedGameState(int playerIndex, BoardTile[][] board, Player playerNorth, Player playerSouth, HashMap<Piece, double[]> probabilitiesMap) {
        super(board, playerNorth, playerSouth);
        this.playerIndex = playerIndex;
        this.probabilitiesMap = new HashMap<>(40);
        for (Piece p : probabilitiesMap.keySet()) {
            this.probabilitiesMap.put(p, probabilitiesMap.get(p).clone());
        }
        moveHistory = new Stack<>();
        assignmentHistory = new Stack<>();
    }

    @Override
    public void applyMove(Move move) {
        if (move.getOrRow() == -1) {
            return;
        }

        System.out.println("Move FROM (" + move.getOrRow() + "|" + move.getOrCol() + ") TO (" + move.getDestRow() + "|" + move.getDestCol() + ")");
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        Piece encounteredPiece = board[move.getDestRow()][move.getDestCol()].getOccupyingPiece();
        Piece opponentPiece = movingPiece.getPlayerType().ordinal() == playerIndex ? encounteredPiece : movingPiece;
        MoveInformation moveInformation = new MoveInformation(move, movingPiece, encounteredPiece);

        if (opponentPiece != null && probabilitiesMap.get(opponentPiece) == null) {
            System.out.println("Weird stuff: ");
            printBoard();
        }

        // not strictly speaking necessary, could also just adapt the checkIfAttackWins() method in ProbabilisticMoveManager
        // to search for a piece with value 1 somewhere and if there isn't one it must be a move from the actual game
        MoveManager moveManager;
        if (move instanceof InGameMove) {
            moveManager = new DiscreteMoveManager(board);
        } else {
            moveManager = new ProbabilisticMoveManager(board, probabilitiesMap);
        }

        // perform move
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());

        moveInformation.setMoveResult(moveManager.lastMoveResult());
        moveHistory.add(moveInformation);

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

        // first check whether (a) the opponent moved a piece or (b) we encountered a piece of the opponent
        // if not there is no need to update probabilities
        if (opponentPiece == null) {
            return;
        }

        moveInformation.setPreviousProbabilities(probabilitiesMap);

        if (probabilitiesMap.get(opponentPiece) == null) {
            System.out.println("Moving: " + movingPiece + "\nEncountered: " + encounteredPiece + "\nOpponent: " + opponentPiece);
            System.exit(1);
        }

        // only if the move was performed in the actual game should the probabilities of the piece be updated
        // according to the outcome of the encounter, otherwise the piece was already assigned a probability
        if (move instanceof InGameMove && encounteredPiece != null && movingPiece != encounteredPiece) {
            // check whether opponent's piece was encountered with our move or the other way around
            // then, regardless of the outcome of that move, the piece should be revealed
            //if (moveManager.lastMoveResult() != MoveResult.MOVE) {
                int typeIndex = opponentPiece.getType().ordinal();
                double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
                if (Math.abs(probabilitiesArray[typeIndex]) > PROB_EPSILON) {
                    for (int i = 0; i < probabilitiesArray.length; i++) {
                        if (i != typeIndex) {
                            probabilitiesArray[i] = 0;
                        } else {
                            probabilitiesArray[i] = 1;
                        }
                    }
                    updateProbabilities();
                }
                return;
            //}
        }

        // if it was the opponent's piece that moved and it moved further than 1 step it has to be a SCOUT
        if (move.length() > 1 && move.getPlayerIndex() == opponentPiece.getPlayerType().ordinal()) {
            double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
            if (Math.abs(probabilitiesArray[PieceType.SCOUT.ordinal()]) > PROB_EPSILON) {
                for (int i = 0; i < probabilitiesArray.length; i++) {
                    if (i != PieceType.SCOUT.ordinal()) {
                        probabilitiesArray[i] = 0;
                    } else {
                        probabilitiesArray[i] = 1;
                    }
                }/*
                System.out.println("Probabilities array (1) for move from (" + move.getOrRow() + "|" + move.getOrCol() + ") to (" + move.getDestRow() + "|" + move.getDestCol() + ")");
                for (int i = 0; i < probabilitiesArray.length; i++) {
                    System.out.print(probabilitiesArray[i] + " ");
                }
                System.out.println();*/
                updateProbabilities();
            }/*
            System.out.println("Probabilities array (2) for move from (" + move.getOrRow() + "|" + move.getOrCol() + ") to (" + move.getDestRow() + "|" + move.getDestCol() + ")");
            for (int i = 0; i < probabilitiesArray.length; i++) {
                System.out.print(probabilitiesArray[i] + " ");
            }
            System.out.println();*/
            return;
        }

        // the last possibility is a simple move of the opponent's piece which means it cannot be a FLAG or a BOMB
        probabilitiesMap.get(opponentPiece)[PieceType.BOMB.ordinal()] = 0;
        probabilitiesMap.get(opponentPiece)[PieceType.FLAG.ordinal()] = 0;
        updateProbabilities();
    }

    @Override
    public EnhancedGameState clone() {
        EnhancedGameState clone = new EnhancedGameState(playerIndex, board, playerNorth, playerSouth, probabilitiesMap);
        return clone;
    }

    /* new methods for the "enhanced" implementation */

    public void undoLastMove() {
        //printBoard();
        MoveInformation lastMoveInfo = moveHistory.pop();
        //System.out.println("Undo move FROM (" + lastMoveInfo.getOrRow() + "|" + lastMoveInfo.getOrCol() + ") TO (" + lastMoveInfo.getDestRow() + "|" + lastMoveInfo.getDestCol() + ")");
        if (lastMoveInfo.getMoveResult() == MoveResult.MOVE && lastMoveInfo.getMovingPieceReference().getPlayerType().ordinal() != playerIndex) {
            // in this case the move was performed by the opponent, possibly revealing that it is a SCOUT or NOT a BOMB or FLAG
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).removePiece(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).addPiece(lastMoveInfo.getMovingPieceClone());
            board[lastMoveInfo.getMovingPieceReference().getRowPos()][lastMoveInfo.getMovingPieceReference().getColPos()].setOccupyingPiece(null);
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());

            //probabilitiesMap.replace(lastMoveInfo.getMovingPieceClone(), lastMoveInfo.getPreviousProbabilities());
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
        } else if (lastMoveInfo.getMoveResult() == MoveResult.MOVE) {
            // in this case our own piece moved and has to be set to the last position
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).removePiece(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).addPiece(lastMoveInfo.getMovingPieceClone());
            board[lastMoveInfo.getMovingPieceReference().getRowPos()][lastMoveInfo.getMovingPieceReference().getColPos()].setOccupyingPiece(null);
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
            //System.out.println("(" + lastMoveInfo.getMovingPieceReference().getRowPos() + "|" + lastMoveInfo.getMovingPieceReference().getColPos() + ") set to " + board[lastMoveInfo.getMovingPieceReference().getRowPos()][lastMoveInfo.getMovingPieceReference().getColPos()].getOccupyingPiece());
            //System.out.println("(" + lastMoveInfo.getMovingPieceClone().getRowPos() + "|" + lastMoveInfo.getMovingPieceClone().getColPos() + ") set to " + board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].getOccupyingPiece());
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKTIE) {
            // in this case both pieces have to be added to the respective players' active pieces lists again
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).addPiece(lastMoveInfo.getMovingPieceClone());

            board[lastMoveInfo.getEncounteredPieceClone().getRowPos()][lastMoveInfo.getEncounteredPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).addPiece(lastMoveInfo.getEncounteredPieceClone());

            //probabilitiesMap.replace(lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex ? lastMoveInfo.getEncounteredPieceClone() : lastMoveInfo.getMovingPieceClone(), lastMoveInfo.getPreviousProbabilities());
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKWON) {
            // in that case the piece that moved and won should be set to the original position again and the original piece should be removed from the player; the defeated piece has to be re-added
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).removePiece(lastMoveInfo.getMovingPieceReference());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).addPiece(lastMoveInfo.getMovingPieceClone());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());

            board[lastMoveInfo.getEncounteredPieceClone().getRowPos()][lastMoveInfo.getEncounteredPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).addPiece(lastMoveInfo.getEncounteredPieceClone());

            //probabilitiesMap.replace(lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex ? lastMoveInfo.getEncounteredPieceClone() : lastMoveInfo.getMovingPieceClone(), lastMoveInfo.getPreviousProbabilities());
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
        } else if (lastMoveInfo.getMoveResult() == MoveResult.ATTACKLOST) {
            // in that case the moving piece has to be re-added and set to the original position
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceReference());
            board[lastMoveInfo.getMovingPieceClone().getRowPos()][lastMoveInfo.getMovingPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getMovingPieceClone());
            getPlayer(lastMoveInfo.getMovingPieceClone().getPlayerType()).addPiece(lastMoveInfo.getMovingPieceClone());

            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).removePiece(lastMoveInfo.getEncounteredPieceReference());
            getPlayer(lastMoveInfo.getEncounteredPieceClone().getPlayerType()).addPiece(lastMoveInfo.getEncounteredPieceClone());
            board[lastMoveInfo.getEncounteredPieceClone().getRowPos()][lastMoveInfo.getEncounteredPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceReference());
            board[lastMoveInfo.getEncounteredPieceClone().getRowPos()][lastMoveInfo.getEncounteredPieceClone().getColPos()].setOccupyingPiece(lastMoveInfo.getEncounteredPieceClone());

            //probabilitiesMap.replace(lastMoveInfo.getMovingPieceClone().getPlayerType().ordinal() == playerIndex ? lastMoveInfo.getEncounteredPieceClone() : lastMoveInfo.getMovingPieceClone(), lastMoveInfo.getPreviousProbabilities());
            lastMoveInfo.replaceProbabilities(probabilitiesMap);
        }
        updateProbabilities();
        //System.out.println("undo move: FROM (" + lastMoveInfo.getm);
        //printBoard();
    }

    /**
     * This method is used to copy a setup created either by the other player or by the makeBoardSetup() method
     * of whichever AI is using the EnhancedGameState instance into that instance. The pieces which are now on the
     * board are then inserted into the HashMap storing the type probabilities and the probability values are
     * initialised.
     * */
    public void copySetup(GameState state, int playerIndex) {
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col] = state.getBoardArray()[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].clone();
                //System.out.println("EnhancedGameState");
                getPlayer(playerIndex).addPiece(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
            }
        }
        if (this.playerIndex != playerIndex) {
            probabilitiesMap = new HashMap<>(40);
            double[] initProbabilities = new double[PieceType.values().length - 1];
            for (int i = 0; i < initProbabilities.length; i++) {
                initProbabilities[i] = ((double) PieceType.pieceQuantity[i]) / getPlayer(playerIndex).getActivePieces().size();
            }
            for (Piece p : getPlayer(playerIndex).getActivePieces()) {
                probabilitiesMap.put(p, initProbabilities.clone());
            }
        }
    }

    public int getPlayerIndex() {
        return playerIndex;
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
        for (double val : probabilitiesMap.get(piece)) {
            if (Math.abs(val - 1.0) < PROB_EPSILON) {
                return true;
            } else if (val > PROB_EPSILON) {
                return false;
            }
        }
        return false;
    }

    public void assignPieceType(Piece piece, PieceType assignedType) {
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

    private void updateProbabilities() {
        //ArrayList<Piece> pieces = getPlayer(1 - playerIndex).getActivePieces();
        Set<Piece> pieces = probabilitiesMap.keySet();
        double[] currentProbabilities;
        boolean updated = false;
        long before = System.nanoTime();
        int counter = 0;
        while (!updated) {
            /*if (counter % 1000 == 0) {
                System.out.println(counter);
            }*/
            counter++;
            updated = true;

            // go through each rank
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                double sum = 0;

                for (Piece p : pieces) {
                    //System.out.println("hashCode: " + p.hashCode() + ", value: " + probabilitiesMap.get(p)[i]);
                    // sum += p.prob(rank associated with i);
                    sum += probabilitiesMap.get(p)[i];
                }

                sum /= PieceType.pieceQuantity[i];

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

        long difference = System.nanoTime() - before;
    }

    public void printProbabilitiesTable() {
        System.out.println("\nTABLE OF PROBABILITIES: ");

        System.out.print("---------");
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            System.out.print("---------");
        }
        System.out.println();

        System.out.print("|       |");
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            System.out.print("   " + PieceType.values()[i].toString().substring(0, 2) + "   |");
        }
        System.out.println();

        System.out.print("---------");
        for (int i = 0; i < PieceType.values().length - 1; i++) {
            System.out.print("---------");
        }
        System.out.println();

        for (Piece p : probabilitiesMap.keySet()) {
            System.out.print("| (" + p.getRowPos() + "|" + p.getColPos() + ") |");
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                System.out.printf(" %.4f |", probabilitiesMap.get(p)[i]);
            }

            System.out.print("\n---------");
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                System.out.print("---------");
            }
            System.out.println();
        }

        System.out.println("\n");
    }

}
