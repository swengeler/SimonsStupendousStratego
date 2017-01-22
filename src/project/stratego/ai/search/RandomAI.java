package project.stratego.ai.search;

import project.stratego.ai.tests.AITestsMain;
import project.stratego.ai.utils.AIMove;
import project.stratego.control.managers.ModelComManager;
import project.stratego.game.entities.GameState;
import project.stratego.game.entities.Piece;
import project.stratego.game.moves.Move;
import project.stratego.game.utils.PlayerType;

import java.util.ArrayList;

public class RandomAI extends AbstractAI {

    public RandomAI(int playerIndex) {
        super(playerIndex);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        //System.out.println("\n------------------------------------------------------------------------------------");
        //System.out.println("RANDOM search for " + PlayerType.values()[playerIndex]);
        //System.out.println("------------------------------------------------------------------------------------");
        gameState.applyMove(lastOpponentMove);
        //System.out.println("Apply: " + lastOpponentMove);
        //gameState.printBoard();
        long testTotal = System.nanoTime();
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        int randIndex = (int) (Math.random() * legalMoves.size());
        //System.out.println("------------------------------------------------------------------------------------\n");
        if (legalMoves.size() == 0) {
            gameState.printBoard();
            System.exit(1);
        }
        AITestsMain.addMoveStatistics(playerIndex, -1, -1, -1, (System.nanoTime() - testTotal), -1, -1);
        return legalMoves.get(randIndex);
    }

}
