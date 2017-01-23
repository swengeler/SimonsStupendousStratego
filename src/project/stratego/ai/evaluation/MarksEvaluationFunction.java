package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;
import project.stratego.game.entities.BoardTile;

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
        // rank values, flag is index 0, spy is 1, scout 2 etc bomb is 11
        double[] rankValues = {1000.0, 100.0, 10.0, 100.0, 20.0, 50.0, 100.0, 140.0, 175.0, 300.0, 400.0, 75.0  };
        // value of board postions
        double[] rowValues = {1.0, 2.0, 3.0, 4.0, 5.0, 5.0, 4.0, 3.0, 2.0, 1.0};
        double[] colValues = {6.0, 7.0, 4.0, 4.0, 9.0, 9.0, 4.0, 4.0,  7.0, 6.0};

        // iterate over opponent's pieces
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces()) {
            
            // score for board position
            opponentSum += rowValues[p.getRowPos()] * colValues[p.getColPos()];
            
            
            // Score for material and info values
            
            if (p.getType() == PieceType.BOMB) {
                // revealed bomb is worth 0
                if (!p.isRevealed()) {
                    opponentSum += rankValues[11] * materialWeight;
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
            
            
            ownSum += rowValues[p.getRowPos()] * colValues[p.getColPos()];
            
            if (p.getType() == PieceType.BOMB) {
                if (!p.isRevealed()){
                    ownSum +=  75.0 * materialWeight;
                }
            } else if (p.getType() == PieceType.FLAG) {
                
                ownSum +=  1000.0 * materialWeight;

            } else if (p.getType() == PieceType.SPY) {
                // Very rough Spy value. No Marshall check
                ownSum += 100.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 100.0 * ownInfoWeight;
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
            } else if (p.getType() == PieceType.SERGEANT) {
              
                ownSum += 20.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 20.0 * ownInfoWeight;
                }
            } 
             else if (p.getType() == PieceType.LIEUTENANT) {
              
                ownSum += 50.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 50.0 * ownInfoWeight;
                }
                               
            }   
            
             else if (p.getType() == PieceType.CAPTAIN) {
              
                ownSum += 100.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 100.0 * ownInfoWeight;
                }
            } 
            
             else if (p.getType() == PieceType.MAJOR) {
              
                ownSum += 140.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 140.0 * ownInfoWeight;
                }
            } 
             else if (p.getType() == PieceType.COLONEL) {
              
                ownSum += 175.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 175.0 * ownInfoWeight;
                }
            } 
             else if (p.getType() == PieceType.GENERAL) {
              
                ownSum += 300.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 300.0 * ownInfoWeight;
                }
            } 
             else if (p.getType() == PieceType.MARSHAL) {
              
                ownSum += 400.0 * materialWeight;
                if (p.isRevealed()){
                    opponentSum += 400.0 * ownInfoWeight;
                }
            }
        }

       // Score empty tiles
        
        
        double result ownSum - opponentSum= 
            
        while(Math.abs(result) > 1000){
        result = result * 0.95;
        }
        
        return result;
    }

}
