package project.stratego.ai.search;

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
        ArrayList<AIMove> legalMoves = generateLegalMoves(gameState, playerIndex);
        int randIndex = (int) (Math.random() * legalMoves.size());
        //System.out.println("------------------------------------------------------------------------------------\n");
        return legalMoves.get(randIndex);
    }

    @Override
    public int getPlayerIndex() {
        return playerIndex;
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        //ModelComManager.getInstance().requestAutoDeploy(-1, playerIndex);
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        gameState.interpretAndCopySetup(example1);
        inGameState.copySetup(gameState, playerIndex);
    }

    @Override
    public void copyOpponentSetup(GameState inGameState) {
        gameState.copySetup(inGameState, 1 - playerIndex);
        gameState.printBoard();
    }

    public void applyMove(Move move) {
        super.applyMove(move);
        System.out.println("In random AI:");
        gameState.printBoard();
    }

}
