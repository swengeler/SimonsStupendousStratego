package project.stratego.ai;

import project.stratego.ai.searchGenerics.TreeSearchNode;
import project.stratego.game.entities.*;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class ExpectimaxAI implements AIInterface {

    private TreeSearchNode root;

    private int playerIndex;

    @Override
    public AIMove getNextMove(GameState state, int playerIndex) {
        return expectimaxAlphaBetaSearch(state, playerIndex);
    }

    @Override
    public void makeBoardSetup(GameState state, int playerIndex) {

    }

    private AIMove expectimaxAlphaBetaSearch(GameState state, int playerIndex) {
        root = new TreeSearchNode(state.clone(playerIndex));
        this.playerIndex = playerIndex;
        return null;
    }

    private ArrayList<TreeSearchNode> generateSuccessors(TreeSearchNode root) {
        if (playerIndexTurn == playerIndex) {
            // find all movable pieces
            // construct nodes with all possible resulting moves

        } else {

        }

    }

    private ArrayList<Piece> getMovablePieces(BoardTile[][] board, Player player) {
        ArrayList<Piece> movablePieces = new ArrayList<>();
        for (Piece p : player.getActivePieces()) {
            if (p.getType() != PieceType.BOMB && p.getType() != PieceType.FLAG &&
                ((p.getRowPos() - 1 >= 0 && (board[p.getRowPos() - 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() - 1][p.getColPos()].isAccessible()) ||
                (p.getRowPos() + 1 < 10 && (board[p.getRowPos() + 1][p.getColPos()].getOccupyingPiece() == null) && board[p.getRowPos() + 1][p.getColPos()].isAccessible()) ||
                (p.getColPos() - 1 >= 0 && (board[p.getRowPos()][p.getColPos() - 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() - 1].isAccessible()) ||
                (p.getColPos() + 1 < 10 && (board[p.getRowPos()][p.getColPos() + 1].getOccupyingPiece() == null) && board[p.getRowPos()][p.getColPos() + 1].isAccessible()))) {
                movablePieces.add(p);
            }
        }
        return movablePieces;
    }

    private ArrayList<TreeSearchNode> addSuccessorNodes(ArrayList<TreeSearchNode> successors, GameState state, Piece movablePiece) {
        // if an opponent's piece is not known probability node should be created
        return successors;
    }

}
