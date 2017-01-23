package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;

import java.util.ArrayList;

public class MarksNewEvaluationFunction extends AbstractEvaluationFunction {

    public MarksNewEvaluationFunction(int playerIndex) {
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
        ArrayList<Piece> opponentPieces = gameState.getPlayer(1 - gameState.getPlayerIndex()).getActivePieces();
        ArrayList<Piece> ownPieces = gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces();
        ArrayList<Piece> enemyGraveyard = gameState.getPlayer(1 - gameState.getPlayerIndex()).getDeadPieces()  ;
        ArrayList<Piece> ownGraveyard = gameState.getPlayer(gameState.getPlayerIndex()).getDeadPieces() ;

        // game state set variables
        double opponentInfoWeight = 0.01 * opponentPieces.size();
        double ownInfoWeight = 0.01 * ownPieces.size();
        double opponentMoveWeight = 0.1;
        double ownMoveWeight =  0.1;
        double materialWeight = 1.0;
        // rank values, flag is index 0, spy is 1, scout 2 etc bomb is 11
        double[] rankValues = {1000.0, 35.0, 8.0, 25.0, 16.0, 24.0, 30.0, 55.0, 70.0, 90.0, 100.0, 50.0  };
        // value of board tile positions
        double[] rowValues = {0.001, 0.01, 0.1, 0.2, 0.4, 0.5, 0.6, 0.1, 0.01, 0.001};
        double[] colValues = {3.5, 4.0, 3.0, 3.0, 4.0, 4.0, 3.0, 3.0,  4.0, 3.5};

        // marshall death status
        boolean deadOppMarsh = false;
        // enemyGraveyard.contains(PieceType.MARSHAL);
        boolean deadOwnMarsh =  false;
        // ownGraveyard.contains(PieceType.MARSHAL);
        boolean deadOppSpy = false;
        boolean deadOwnSpy = false;

        // Adjust Miner and Scout values
        int ownMiners = 5;
        int ownScouts = 8;
        int enemyMiners = 5;
        int enemyScouts = 8;

        for(Piece p:ownGraveyard){
            if(p.getType() == PieceType.SCOUT){
                ownScouts--;
            }
            if(p.getType() == PieceType.MINER){
                ownMiners--;
            }
            if(p.getType() == PieceType.MARSHAL){
            deadOwnMarsh = true;
            }

            if(p.getType() == PieceType.SPY){
            deadOwnSpy = true;
            }
        }

        for(Piece p:enemyGraveyard){
            if(p.getType() == PieceType.SCOUT){
                enemyScouts--;
            }
            if(p.getType() == PieceType.MINER){
                enemyMiners--;
            }
            if(p.getType() == PieceType.MARSHAL){
                deadOppMarsh = true;
            }
            if(p.getType() == PieceType.SPY){
                deadOppSpy = true;
            }
        }








        // iterate over opponent's pieces
        for (Piece p : opponentPieces) {

            // score for board position
            // opponentSum += (rowValues[p.getRowPos()] * colValues[p.getColPos()] * rankValues[(PieceType.pieceLvlMap.get(p.getType()))] );


            // Score for material and info values

            if (p.getType() == PieceType.BOMB) {
                // Bomb threat check - miners only *******


            } else if (p.getType() == PieceType.FLAG) {
                // Flag defence check, normal + scout **********


            } else if (p.getType() == PieceType.SPY) {


                double spyValue = rankValues[1];

                // If friendly marshall dead, lower value enemy spy to that of half a scout
                if (deadOwnMarsh) {
                    spyValue = rankValues[2] * 0.5;

                }


                // Spy defence check, normal + scout ********


            } else if (p.getType() == PieceType.SCOUT) {

                double scoutValue = rankValues[2];
                // If 1-3 scouts exist, score them higher. With this: 20, 40, 60

                if(enemyScouts < 4){
                    scoutValue = (4-enemyScouts)*20;
                }

                // scout threat, maybe scout threat?

            } else if (p.getType() == PieceType.MINER) {

                // miner value 25, plus 5 per lost miner
                double minerValue = rankValues[3];
                if(enemyMiners < 5){
                    minerValue = (10 - enemyMiners) * 5;
                }

                // Miner threat


            } else if (p.getType() == PieceType.MARSHAL) {
                // Marshall threat

            }

            else {
                // for ranks 4 to 9
                int rank = PieceType.pieceLvlMap.get(p.getType());
                double pieceValue = rankValues[rank];
                pieceValue = infoEnemy(p, pieceValue, opponentInfoWeight);

                // do threat


            }

            /* else if (p.getType() == PieceType.LIEUTENANT) {
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
            }
            */

        }


        // iterate over own giving tile value
        for (Piece p : gameState.getPlayer(gameState.getPlayerIndex()).getActivePieces()) {
            
            
            ownSum += (rowValues[p.getRowPos()] * colValues[p.getColPos()] * rankValues[(PieceType.pieceLvlMap.get(p.getType()))]  );
            
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
                if(p.isMoveRevealed()){
                    opponentSum += 400.0 * ownInfoWeight;

                }
            }
        }
       // add random score
        opponentSum += Math.random();


        return 0.0; // *************************************//


    }

    public double infoOwn(Piece p, double pieceValue, double infoValue){


        double result = 0.0;
        if(p.isRevealed()){
            result = pieceValue * (1- infoValue);
            return result;
        }

        else if(p.isMoveRevealed()){
            result = pieceValue * 0.1;
            return result;
        }
        return pieceValue;
    }

    public double infoEnemy(Piece p, double pieceValue, double infoValue){

        double result = 0.0;

        if(p.isRevealed()){
            result = pieceValue * (1 - infoValue);
            return result;
        }

        return pieceValue;
    }

    // manhattan move calc

    // threat gen

}

