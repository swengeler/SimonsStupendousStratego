package project.stratego.ai.evaluation;

import project.stratego.ai.utils.EnhancedGameState;
import project.stratego.game.entities.BoardTile;
import project.stratego.game.entities.Piece;
import project.stratego.game.utils.PieceType;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class VincibleEvaluationFunction extends AbstractEvaluationFunction {

    public VincibleEvaluationFunction(int playerIndex) {
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
        double opponentSum = 0.0;
        double ownSum = 0.0;

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
        double[] rankValues = {1000.0, 35.0, 8.0, 25.0, 16.0, 24.0, 30.0, 50.0, 70.0, 90.0, 100.0, 50.0  };
        // value of board tile positions
        double[] rowValues = {0.0, 0.0, 0.0, 0.05, 0.1, 0.1, 0.05, 0.0, 0.0, 0.0};
        double[] colValues = {4.0, 4.5, 3.0, 3.5, 5.0, 5.0, 3.5, 3.0,  4.0, 3.5};

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

            // score for revealed piece. 
            if(p.isRevealed()){
                int rank = PieceType.pieceLvlMap.get(p.getType());
             ownSum -= (rowValues[p.getRowPos()] * colValues[p.getColPos()] * rank );
                
              ArrayList<Piece> danger = threatRange(gameState, p,6);
                
                if(danger.size() > 0){
               // get nearest own piece that can threaten enemy piece
                    int distance = 20;
                    for(Piece q:danger){
                        if(PieceType.pieceLvlMap.get(q.getType()) > rank){
                        int tempDistance = Math.abs(p.getRowPos() - q.getRowPos() ) + Math.abs(p.getColPos() - q.getColPos() );
                        distance = Math.min(distance, tempDistance);
                        }
                        
                    }
               
                // reduce position value by threat, reduced over distance    
                ownSum += rankValues[rank] * (1/distance);  
                }
            }
            else
               
                ownSum -= (rowValues[p.getRowPos()] * colValues[p.getColPos()] * 5);


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
                spyValue = infoEnemy(p, spyValue, opponentInfoWeight);
                ownSum -= spyValue;


                // Spy defence check, normal + scout ********


            } else if (p.getType() == PieceType.SCOUT) {

                double scoutValue = rankValues[2];
                // If 1-3 scouts exist, score them higher. With this: 20, 40, 60

                if(enemyScouts < 4){
                    scoutValue = (4-enemyScouts)*20;
                }
                scoutValue = infoEnemy(p, scoutValue, opponentInfoWeight);
                ownSum -= scoutValue;

                // scout threat, maybe scout threat?

            } else if (p.getType() == PieceType.MINER) {

                // miner value 25, plus 5 per lost miner
                double minerValue = rankValues[3];
                if(enemyMiners < 5){
                    minerValue = (10 - enemyMiners) * 5;
                }
                
                minerValue = infoEnemy(p, minerValue, opponentInfoWeight);
                ownSum -= minerValue;
                

                // Miner threat


            } else if (p.getType() == PieceType.MARSHAL) {
                double pieceValue = rankValues[10];
                pieceValue = infoEnemy(p, pieceValue, opponentInfoWeight);
                ownSum -= pieceValue;
                // Marshall threat

            }

            else {
                // for ranks 4 to 9
                int rank = PieceType.pieceLvlMap.get(p.getType());
                double pieceValue = rankValues[rank];
                pieceValue = infoEnemy(p, pieceValue, opponentInfoWeight);
                ownSum -= pieceValue;

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
            
            // position value
             ownSum += (rowValues[p.getRowPos()] * colValues[p.getColPos()] * PieceType.pieceLvlMap.get(p.getType()) );
            
            if (p.getType() == PieceType.BOMB) {
                // Bomb threat check - miners only 

            } else if (p.getType() == PieceType.FLAG) {
                // Flag defence check, normal 
                
                // get pieces within 6 moves
                ArrayList<Piece> danger = threatRange(gameState, p,6);
                if(danger.size() > 0){
                   // get distance to nearest enemy piece
                    int distance = Math.abs(p.getRowPos() - danger.get(0).getRowPos() ) + Math.abs(p.getColPos() - danger.get(0).getColPos());
                    // reduce position value by threat, reduced over distance
                    ownSum -= rankValues[0] * (1/distance);
                }


            } else if (p.getType() == PieceType.SPY) {


                double spyValue = rankValues[1];

                // If friendly marshall dead, lower value enemy spy to that of half a scout
                if (deadOwnMarsh) {
                    spyValue = rankValues[2] * 0.5;

                }
                ownSum += spyValue;


                // Spy defence check, normal + scout ********


            } else if (p.getType() == PieceType.SCOUT) {

                double scoutValue = rankValues[2];
                // If 1-3 scouts exist, score them higher. With this: 20, 40, 60

                if(ownScouts < 4){
                    scoutValue = (4-ownScouts)*20;
                }
                ownSum += scoutValue;

                // scout threat, maybe scout threat?

            } else if (p.getType() == PieceType.MINER) {

                // miner value 25, plus 5 per lost miner
                double minerValue = rankValues[3];
                if(ownMiners < 5){
                    minerValue = (10 - ownMiners) * 5;
                }
                

                // Miner threat


            } else if (p.getType() == PieceType.MARSHAL) {
                double pieceValue = rankValues[10];
                pieceValue = infoOwn(p, pieceValue, ownInfoWeight);
                ownSum += pieceValue;
                // Marshall threat

            }

            else {
                // for ranks 4 to 9
                int rank = PieceType.pieceLvlMap.get(p.getType());
                double pieceValue = rankValues[rank];
                pieceValue = infoOwn(p, pieceValue, ownInfoWeight);
                ownSum += pieceValue;

                // do threat


            }
        }
       // add random score
        ownSum += Math.random();
        
         
        // sanity check, shouldn't be needed
        while(Math.abs(ownSum) > 1000){
        ownSum = ownSum * 0.999;
        }


        return ownSum; 

    }
    // value adjustment for own pieces, should reduce moving unmoved pieces 

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
    // value reduction of enemy. Can't use isMoveRevealed as it would give away hidden information

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
    // returns distance to nearest piece that can capture it
    public ArrayList<Piece> threatRange(EnhancedGameState gameState, Piece p, int range){
        ArrayList<Piece> result = new ArrayList<Piece>();
        int row = p.getRowPos();
        int col = p.getColPos();
        int[] dx = {0};
        int[] dy = {0};
        
        int distance = 0;
        
        PlayerType friendly = p.getPlayerType();
        int rank = PieceType.pieceLvlMap.get(p.getType());
        
        
        // distance 1
        distance = 1;
        if(distance > range){
            return result;
            }
         
        dx = new int[]{1, -1, 0, 0};
        dy = new int[]{0, 0, 1, -1};
        
        for(int i =0; i<4; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ){
            
                // check tile temp for enemy piece
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        }
        
        // distance 2
        distance = 2;
        if(distance > range){
        return result;
        }
        
        dx = new int[]{2, -2, 0, 0, 1, 1, -1, -1};
        dy = new int[]{0, 0, 2, -2, 1, -1, 1, -1};
        for(int i =0; i<8; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ){
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        }
        
        
        // distance 3
        
        distance = 3;
        if(distance > range){
        return result;
        }
        
        dx = new int[]{3, -3, 0, 0, 2, 2, -2, -2, 1, 1, -1, -1};
        dy = new int[]{0, 0, 3, -3, 1, -1, 1, -1, 2, -2, 2, -2};
        for(int i =0; i<12; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ){
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        }
        
        
        
        // distance 4
        
        distance = 4;
        if(distance > range){
        return result;
        }
        
        dx = new int[]{4, -4, 0, 0, 3, 3, -3, -3, 1, 1, -1, -1, 2, 2, -2, -2};
        dy = new int[]{0, 0, 4, -4, 1, -1, 1, -1, 3, -3, 3, -3, 2, -2, 2, -2};
        for(int i =0; i<16; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ){
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        }
        
       
        
        // distance 5
        
        distance = 5;
        if(distance > range){
        return result;
        }
        
        dx = new int[]{5, -5, 0, 0, 4, 4, -4, -4, 1, 1, -1, -1, 3, 3, -3, -3, 2, 2, -2, -2};
        dy = new int[]{0, 0, 5, -5, 1, -1, 1, -1, 4, -4, 4, -4, 2, -2, 2, -2, 3, -3, 3, -3};
        for(int i =0; i<20; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ){
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        } 
    
    
        
        // distance 6
        
        distance = 6;
        if(distance > range){
        return result;
        }
        
        dx = new int[]{6, -6, 0, 0, 5, 5, -5, -5, 1, 1, -1, -1, 4, 4, -4, -4, 2, 2, -2, -2, 3, 3, -3, -3};
        dy = new int[]{0, 0, 6, -6, 1, -1, 1, -1, 5, -5, 5, -5, 2, -2, 2, -2, 4, -4, 4, -4, 3, -3, 3, -3};
        for(int i =0; i<24; i++){
            // boundary check
            if( (row + dx[i] > -1) && (row + dx[i] < 10) && (col + dy[i] > -1) && (col + dy[i] < 10) ) {
                BoardTile temp = gameState.getBoardArray()[row + dx[i]][col + dy[i]];
                if(temp.getOccupyingPiece().getPlayerType() != friendly ){
        
                result.add(temp.getOccupyingPiece());
                }
            
            }
        
        } 
    
        return result;
    }
    
    

}

