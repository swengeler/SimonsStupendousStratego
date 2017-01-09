package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class MarksEvaluationFunction extends AbstractEvaluationFunction {

    public MarksEvaluationFunction(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public double evaluate(EnhancedGameState gameState, int playerType) {
        if (gameState.isGameOver() && gameState.playerWon()) {
            return 1000.0;
        } else if (gameState.isGameOver()) {
            return -1000.0;
        }

        // estimated value of position based on observed elements
        double opponentSum = 0;
        double ownSum = 0;

        // pointers for the Arraylists
        ArrayList opponentPieces = gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces();
        ArrayList ownPieces = gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces();

        // game state set variables
        double opponentInfoWeight = 0.1 * opponentPieces.size();
        double ownInfoWeight = 0.1 * ownPieces.size();
        double materialWeight = 1.0;

        // iterate over opponent's pieces
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                // revealed bomb is worth 0
                if (!p.isRevealed()) {
                    opponentSum += 75.0 * materialWeight;
                }
            } else if (p.getType() == PieceType.FLAG) {
                // Flag stuff goes here. Leave at 0
                opponentSum += 1000.0 * materialWeight;
            } else if (p.getType() == PieceType.SPY) {
                // Very rough Spy value. No Marshall check
                opponentSum += 100.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 100.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.SCOUT) {
                // Static Scout value as lowest unit
                opponentSum += 10.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 10.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.MINER) {
                // Static miner value between Sergeant and Lieutenant
                opponentSum += 100.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 100.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.SERGEANT) {
                opponentSum += 20.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 20.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.LIEUTENANT) {
                opponentSum += 50.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 50.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.CAPTAIN) {
                opponentSum += 100.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 100.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.MAJOR) {
                opponentSum += 140.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 140.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.COLONEL) {
                opponentSum += 175.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 175.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.GENERAL) {
                opponentSum += 300.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 300.0 * opponentInfoWeight;
                }
            } else if (p.getType() == PieceType.MARSHAL) {
                opponentSum += 400.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 400.0 * opponentInfoWeight;
                }
            }
        }

        // iterate over own
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                if (!p.isRevealed()){
                    ownSum +=  50.0 * materialWeight;
                }
            } else if (p.getType() == PieceType.FLAG) {

            } else if (p.getType() == PieceType.SPY) {
                // Very rough Spy value. No Marshall check
                ownSum += 50.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 50.0 * ownInfoWeight;
                }
            } else if (p.getType() == PieceType.SCOUT) {
                // Static Scout value as lowest unit
                ownSum += 10.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 10.0 * ownInfoWeight;
                }
            } else if (p.getType() == PieceType.MINER) {
                // Static miner value between Sergeant and Lieutenant
                ownSum += 30.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 30.0 * ownInfoWeight;
                }
            } else {
                int tmp = PieceType.pieceLvlMap.get(p.getType());
                ownSum += tmp * tmp * materialWeight;
                if (p.isRevealed()){
                    opponentSum += tmp * tmp * ownInfoWeight;
                }
            }
        }

        return ownSum - opponentSum;
    }

}
