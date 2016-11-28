package project.stratego.ai;

import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.Move;
import project.stratego.game.utils.MoveManager;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class EnhancedGameState extends GameState {

    private static final double PROB_EPSILON = 0.001;

    private double[][][] probabilities;

    public EnhancedGameState() {
        super();
        probabilities = new double[BOARD_SIZE][BOARD_SIZE][PieceType.values().length - 1];
    }

    @Override
    public void applyMove(Move move) {
        Piece movingPiece = board[move.getOrRow()][move.getOrCol()].getOccupyingPiece();
        if (movingPiece == null) {
            return;
        }
        MoveManager moveManager = new MoveManager(board);
        moveManager.processMove(movingPiece.getPlayerType() == PlayerType.NORTH ? playerNorth : playerSouth, movingPiece.getPlayerType() == PlayerType.NORTH ? playerSouth : playerNorth, movingPiece, move.getDestRow(), move.getDestCol());
        // depending on the outcome of the move, update probabilities
    }

    /* new methods for the "enhanced" implementation */

    public double[][][] getProbabilitiesArray() {
        return probabilities;
    }

    private void updateProbabilities(int playerIndex) {
        ArrayList<Piece> pieces = getPlayer(playerIndex).getActivePieces();
        boolean updated = false;
        while (!updated) {
            updated = true;

            // go through each rank
            for (int i = 0; i < PieceType.values().length - 1; i++) {
                double sum = 0;

                for (Piece p : pieces) {
                    // sum += p.prob(rank associated with i);
                }

                if (Math.abs(1 - sum) > PROB_EPSILON) {
                    updated = false;
                    for (Piece p : pieces) {
                        // p.prob(rank associated with i) /= sum;
                    }
                }
            }

            for (Piece p : pieces) {
                double sum = 0;

                for (int i = 0; i < PieceType.values().length - 1; i++) {
                    // sum += p.prob(rank associated with i);
                }

                if (Math.abs(1 - sum) > PROB_EPSILON) {
                    updated = false;
                    for (int i = 0; i < PieceType.values().length - 1; i++) {
                        // p.prob(rank associated with i) /= sum;
                    }
                }
            }


        }
    }

}
