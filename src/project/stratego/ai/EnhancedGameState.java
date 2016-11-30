package project.stratego.ai;

import project.stratego.game.entities.*;
import project.stratego.game.utils.*;

import java.util.*;

/**
 * A class used in the AI part of the code. Aside from the information inherited from GameState (the board
 * state and the players), EnhancedGameState also contains a HashMap storing information about the
 * probability distribution (in respect to the type/rank) of every piece of the opponent of the player the
 * EnhancedGameState instance is assigned to.
 * */
public class EnhancedGameState extends GameState {

    public static final double PROB_EPSILON = 0.0001;

    private HashMap<Piece, double[]> probabilitiesMap;

    // need queue to keep track of moves here

    // from the perspective of the player whose pieces are all known
    // the opponent (1 - playerIndex) is associated with the probabilitiesMap
    private int playerIndex;

    public EnhancedGameState(int playerIndex) {
        super();
        this.playerIndex = playerIndex;
    }

    protected EnhancedGameState(int playerIndex, BoardTile[][] board, Player playerNorth, Player playerSouth, HashMap<Piece, double[]> probabilitiesMap) {
        super(board, playerNorth, playerSouth);
        this.playerIndex = playerIndex;
        this.probabilitiesMap = new HashMap<>(getPlayer(1 - playerIndex).getActivePieces().size());
        for (Piece p : getPlayer(1 - playerIndex).getActivePieces()) {
            this.probabilitiesMap.put(p, probabilitiesMap.get(p).clone());
        }
    }

    @Override
    public void applyMove(Move move) {
        System.out.println("Move from: (" + move.getOrRow() + "|" + move.getOrCol() + ") to (" + move.getDestRow() + "|" + move.getDestCol() + ")");
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        Piece opponentPiece = movingPiece.getPlayerType().ordinal() == playerIndex ? board[move.getDestRow()][move.getDestCol()].getOccupyingPiece() : movingPiece;
        MoveManager moveManager = new MoveManager(board);
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());


        // depending on the outcome of the move, update probabilities
        // what should matter for probabilities is whether the opponent's piece was moved or revealed
        // mappings would not have to be removed from the hash-map, instead they should just be set to 1
        // which would produce the same effect for the other probabilities as removing them
        // -> that means that we have to go through all 40 pieces for evaluation though

        // first check whether (a) the opponent moved a piece or (b) we encountered a piece of the opponent
        // if not there is no need to update probabilities
        if (opponentPiece == null) {
            return;
        }

        // check whether opponent's piece was encountered with our move
        // then, regardless of the outcome of that move, the piece should be revealed
        if (moveManager.lastMoveResult() != MoveResult.MOVE) {
            // assumption is that if the value is already very close to 1, it is already revealed (as good as revealed)
            // could probably also leave this out, but it's good for avoiding an updateProbabilities() call
            int typeIndex = opponentPiece.getType().ordinal();
            double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
            if (Math.abs(probabilitiesArray[typeIndex]) > PROB_EPSILON) {
                for (int i = 0; i < probabilitiesArray.length; i++) {
                    if (i != typeIndex) {
                        probabilitiesArray[typeIndex] = 0;
                    } else {
                        probabilitiesArray[typeIndex] = 1;
                    }
                }
                updateProbabilities();
            }
            return;
        }

        // if it was the opponent's piece that moved and it moved further than 1 step it has to be a SCOUT
        if (move.length() > 1) {
            double[] probabilitiesArray = probabilitiesMap.get(opponentPiece);
            if (Math.abs(probabilitiesArray[PieceType.SCOUT.ordinal()]) > PROB_EPSILON) {
                for (int i = 0; i < probabilitiesArray.length; i++) {
                    if (i != PieceType.SCOUT.ordinal()) {
                        probabilitiesArray[PieceType.SCOUT.ordinal()] = 0;
                    } else {
                        probabilitiesArray[PieceType.SCOUT.ordinal()] = 1;
                    }
                }
                updateProbabilities();
            }
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
                getPlayer(playerIndex).addPiece(board[playerIndex == PlayerType.NORTH.ordinal() ? row : BOARD_SIZE - 1 - row][col].getOccupyingPiece());
            }
        }
        if (this.playerIndex != playerIndex) {
            probabilitiesMap = new HashMap<>(getPlayer(playerIndex).getActivePieces().size());
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

    public HashMap<Piece, double[]> getProbabilitiesMap() {
        return probabilitiesMap;
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

    public void assignPieceType(Piece piece, PieceType assignedType) {
        double[] probabilitiesArray = probabilitiesMap.get(piece);
        for (int i = 0; i < probabilitiesArray.length; i++) {
            if (i != assignedType.ordinal()) {
                probabilitiesArray[PieceType.SCOUT.ordinal()] = 0;
            } else {
                probabilitiesArray[PieceType.SCOUT.ordinal()] = 1;
            }
        }
    }

    private void updateProbabilities() {
        //ArrayList<Piece> pieces = getPlayer(1 - playerIndex).getActivePieces();
        Set<Piece> pieces = probabilitiesMap.keySet();
        double[] currentProbabilities;
        boolean updated = false;
        long before = System.nanoTime();
        while (!updated) {
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

                //System.out.println("Sum 1 (for " + PieceType.values()[i] + "): " + sum);

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

                //System.out.println("Sum 2 (for piece at (" + p.getRowPos() + "|" + p.getColPos() + ")): " + sum);

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
        System.out.println("Loop ended in " + (System.nanoTime() - before) + "ns, " + Math.round((System.nanoTime() - before) / 1000.0) + "µs");

        //printProbabilitiesTable();
    }

    private void printProbabilitiesTable() {
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
