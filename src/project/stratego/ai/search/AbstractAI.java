package project.stratego.ai.search;

import project.stratego.ai.evaluation.AbstractEvaluationFunction;
import project.stratego.ai.evaluation.TestEvaluationFunction;
import project.stratego.ai.setup.SetupMaker;
import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.ai.utils.AIMove;
import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public abstract class AbstractAI {

    protected int playerIndex;
    protected EnhancedGameState gameState;
    protected AbstractEvaluationFunction evaluationFunction;

    protected AbstractAI(int playerIndex) {
        this.playerIndex = playerIndex;
        gameState = new EnhancedGameState(playerIndex);
        evaluationFunction = new TestEvaluationFunction(playerIndex);
    }

    public abstract Move getNextMove(Move lastOpponentMove);

    public void makeBoardSetup(GameState inGameState) {
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        gameState.interpretAndCopySetup(example1);
        inGameState.copySetup(gameState, playerIndex);
    }

    public void interpretAndCopyEncodedSetup(GameState inGameState, String setup) {
        gameState.interpretEncodedSetup(setup, playerIndex);
        inGameState.copySetup(gameState, playerIndex);
    }

    public void setEvaluationFunction(AbstractEvaluationFunction evaluationFunction) {
        this.evaluationFunction = evaluationFunction;
    }

    public void setGameState(EnhancedGameState gameState) {
        this.gameState = gameState;
    }

    public void loadGame(String gameEncoding) {
        String[] encodings = gameEncoding.split("\n");
        gameState.interpretEncodedBoard(encodings[0]);
        gameState.interpretEncodedMoves(encodings[1]);
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public EnhancedGameState getEnhancedGameState() {
        return gameState;
    }

    public void copyOpponentSetup(GameState inGameState) {
        gameState.copySetup(inGameState, 1 - playerIndex);
    }

    public void applyMove(Move move) {
        gameState.applyMove(move);
    }

    protected ArrayList<AIMove> orderMoves(EnhancedGameState state, ArrayList<AIMove> legalMoves) {
        // move captures flag? -> opponent's piece has flag "revealed"
        // move wins
        // move attacking unknown piece
        // move towards opponent's piece (maybe differentiate between known/unknown
        // -> towards means withing x range forward
        // move sideways (same)
        // move back (same)
        // attacking move with loss
        ArrayList<Integer> scores = new ArrayList<>(legalMoves.size());
        int movePlayerIndex = legalMoves.get(0).getPlayerIndex();
        Piece encounteredPiece, movingPiece;
        for (AIMove m : legalMoves) {
            movingPiece = state.getBoardArray()[m.getOrRow()][m.getOrCol()].getOccupyingPiece();
            encounteredPiece = state.getBoardArray()[m.getDestRow()][m.getDestCol()].getOccupyingPiece();
            if (encounteredPiece == null) {
                // moving towards a position with adjacent opponents pieces
            }

            if (movePlayerIndex != playerIndex && state.getBoardArray()[m.getDestRow()][m.getDestCol()].getOccupyingPiece().getType() == PieceType.FLAG) {

            }
        }
        return legalMoves;
    }

    protected ArrayList<AIMove> generateLegalMoves(EnhancedGameState state, int playerIndex) {
        //System.out.println("Number of player pieces: " + state.getPlayer(playerIndex).getActivePieces().size());
        ArrayList<AIMove> legalMoves = new ArrayList<>();
        boolean chanceEvent = false;
        int destRow, destCol;
        for (Piece p : state.getPlayer(playerIndex).getActivePieces()) {
            // check for unmovable pieces
            if ((playerIndex == state.getPlayerIndex() && p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG) ||
                    (playerIndex != state.getPlayerIndex() && (Math.abs(state.getProbability(p, PieceType.BOMB) + state.getProbability(p, PieceType.FLAG) - 1.0) > 2 * EnhancedGameState.PROB_EPSILON))) {
                int moveRadius = 1;                             // ^ this here shit, who comes up with that
                // scouts can move more squares than the other pieces
                if ((playerIndex == state.getPlayerIndex() && p.getType() == PieceType.SCOUT) || Math.abs(state.getProbability(p, PieceType.SCOUT) - 1.0) < /*2 * */EnhancedGameState.PROB_EPSILON) {
                    // state.getProbability(p, PieceType.SCOUT) > EnhancedGameState.PROB_EPSILON instead to be more accurate
                    moveRadius = 9;
                }
                // loop through the positions around the piece and check whether they can be moved there
                for (int row = -moveRadius; row <= moveRadius; row++) {
                    destRow = p.getRowPos() + row;
                    for (int col = -moveRadius; col <= moveRadius; col++) {
                        destCol = p.getColPos() + col;
                        // check whether the given piece can move to the target position
                        if (!(row == 0 && col == 0) && checkMovePossible(state, p, destRow, destCol)) {
                            // add legal move to list and also specify whether it will induce a chance event
                            if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null) {
                                // either different playerIndex from root (initPlayerIndex) AND piece to be moved is not revealed AND position to be moved to is taken by root player
                                chanceEvent = playerIndex != this.playerIndex && !p.isRevealed() && !state.probabilityRevealed(p);
                                if (p.getType() == PieceType.SCOUT) {
                                    //System.out.println(p + " chanceEvent: " + chanceEvent);
                                }
                                // OR same playerIndex as root (initPlayerIndex) AND position to be moved to is taken by opponent's unrevealed piece
                                chanceEvent = chanceEvent || (playerIndex == this.playerIndex && !state.getBoardArray()[destRow][destCol].getOccupyingPiece().isRevealed() && !state.probabilityRevealed(state.getBoardArray()[destRow][destCol].getOccupyingPiece()));
                            }
                            legalMoves.add(new AIMove(playerIndex, p.getRowPos(), p.getColPos(), destRow, destCol, chanceEvent));
                            chanceEvent = false;
                        }
                    }
                }
            }
        }
        return legalMoves;
    }

    private boolean checkMovePossible(EnhancedGameState state, Piece p, int destRow, int destCol) {
        // check if position is on the board
        if (destRow < 0 || destRow >= 10 || destCol < 0 || destCol >= 10) {
            return false;
        }
        // check if the move is in a row or column only
        if ((destRow - p.getRowPos()) != 0 && (destCol - p.getColPos()) != 0) {
            return false;
        }
        // check if position is accessible
        if (!state.getBoardArray()[destRow][destCol].isAccessible()) {
            return false;
        }
        // check if position is already occupied by own piece
        if (state.getBoardArray()[destRow][destCol].getOccupyingPiece() != null && state.getBoardArray()[destRow][destCol].getOccupyingPiece().getPlayerType() == p.getPlayerType()) {
            return false;
        }
        // check if path to position is blocked by other piece/inaccessible tile
        if (((p.getPlayerType().ordinal() == state.getPlayerIndex() && p.getType() == PieceType.SCOUT) || Math.abs(state.getProbability(p, PieceType.SCOUT.ordinal()) - 1.0) < 2* EnhancedGameState.PROB_EPSILON) && !checkScoutPath(state, p, destRow, destCol)) {
            return false;
        }
        return true;
    }

    private boolean checkScoutPath(EnhancedGameState state, Piece scout, int destRow, int destCol) {
        // target position is in the same column as current position
        if (scout.getColPos() - destCol == 0) {
            for (int row = scout.getRowPos() + (destRow - scout.getRowPos() < 0 ? -1 : 1); row != destRow; row += (destRow - scout.getRowPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[row][destCol].getOccupyingPiece() != null || !state.getBoardArray()[row][destCol].isAccessible()) {
                    return false;
                }
            }
        } else if (scout.getRowPos() - destRow == 0) {
            for (int col = scout.getColPos() + (destCol - scout.getColPos() < 0 ? -1 : 1); col != destCol; col += (destCol - scout.getColPos() < 0 ? -1 : 1)) {
                if (state.getBoardArray()[destRow][col].getOccupyingPiece() != null || !state.getBoardArray()[destRow][col].isAccessible()) {
                    return false;
                }
            }
        }
        return true;
    }

    /* stats */

    public int getPlayerWonIndex() {
        return gameState.getPlayerWonIndex();
    }

}
