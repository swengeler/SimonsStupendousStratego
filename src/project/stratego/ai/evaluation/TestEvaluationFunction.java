package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

public class TestEvaluationFunction extends AbstractEvaluationFunction {

    public TestEvaluationFunction(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public double evaluate(EnhancedGameState gameState, int playerType) {
        if (gameState.isGameOver() && gameState.playerWon()) {
            return 1000000.0;
        } else if (gameState.isGameOver()) {
            return 0.0;
        }
        
        // estimated value of position based on observed elements
        double opponentSum = 0;
        double ownSum = 0;
       
           
        // pointers for the Arraylists
        ArrayList opponantPieces = gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces());
        ArrayList ownPieces = gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces());
       
        // game state set variables
       
        double opponantInfoWeight = 0.1 * opponantPieces.size();
        double ownInfoWeight = 0.1 * ownPieces.size();
        double materialWeight = 1.0;
        
            
        // iterate over opponant's pieces
        
        
        for (Piece p : gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces()) {
            
            
            
            if (p.getType() == PieceType.BOMB) {
                // revealed bomb is worth 0
                if (p.isRevealed() == FALSE) {
  
                opponentSum += 50.0 * materialWeight;
                }
                                
            } else if (p.getType() == PieceType.FLAG) {
                // Flag stuff goes here. Leave at 0 
                
                
            }else if (p.getType() == PieceType.SPY) {
                // Very rough Spy value. No Marshall check
                 opponentSum += 50.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 50.0 * opponantInfoWeight;
                }
                
                
            } else if (p.getType() == PieceType.SCOUT) {
                // Static Scout value as lowest unit
                            
                opponentSum += 10.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 10.0 * opponantInfoWeight;
                }
                
            } else if (p.getType() == PieceType.MINER) {
                // Static miner value between Sergeant and Lieutenant
                opponentSum += 30.0 * materialWeight;
                if (p.isRevealed()){
                    ownSum += 30.0 * opponantInfoWeight;
                }
                
            } else {
                 // Simple function with attack rating squared       
                int tmp = PieceType.pieceLvlMap.get(p.getType());
                opponentSum += tmp * tmp * materialWeight;
                if (p.isRevealed()){
                    ownSum += tmp * tmp * opponantInfoWeight;
                }
                                                
            }
        
              
            
        
        }
        
        // iterate over own
        
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            if (p.getType() == PieceType.BOMB) {
                if (p.isRevealed() == FALSE){
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
                
            }else {
                
                 int tmp = PieceType.pieceLvlMap.get(p.getType());
              
                ownSum += tmp * tmp * materialWeight;
                if (p.isRevealed()){
                    opponentSum += tmp * tmp * ownInfoWeight;
                }
            }
        }
        
        
       
        
        
        
        // iterate over board
        
        
        
        //if (opponentSum != 0) {
            //return playerIndex == gameState.getPlayerIndex() ? ownSum / opponentSum : opponentSum / ownSum;
           
        return ownSum - opponentSum;
        }
        return 1000.0;
    }

}
