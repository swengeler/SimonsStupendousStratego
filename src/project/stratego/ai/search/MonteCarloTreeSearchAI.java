package project.stratego.ai.search;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.aiPack.*;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.events.StrategoMoveEvent;
import project.stratego.ai.mcts.gameLogic.SystemsManager;
import project.stratego.game.entities.GameState;
import project.stratego.game.moves.Move;

public class MonteCarloTreeSearchAI extends AbstractAI {

    private StrategoMCTSPerformer performer;

    public MonteCarloTreeSearchAI(int playerIndex) {
        super(playerIndex);
        SystemsManager manager = new SystemsManager();
        StrategoMoveGenerator generator = new StrategoMoveGenerator(manager);
        StrategoRules rules = new StrategoRules(manager);
        StrategoPlaythrough playthrough = new StrategoPlaythrough(generator, rules);
        performer = new StrategoMCTSPerformer(new StrategoRules(manager), generator, playthrough);
    }

    @Override
    public Move getNextMove(Move lastOpponentMove) {
        gameState.applyMove(lastOpponentMove);

        // --- WRAPPER CODE --- //
        StrategoGame gameForMCTS = new StrategoGame(gameState);
        TreeNode<StrategoGame, StrategoAbstractEvent> node = performer.runMCTS(new StrategoNode(gameForMCTS));
        StrategoMoveEvent move = (StrategoMoveEvent) node.getAction();
        // --- WRAPPER CODE --- //

        Move actualMove = new Move(move.getOrigintX(), move.getOriginY(), move.getDestX(), move.getDestY(), playerIndex);
        System.out.println("Generated by MCTS: " + actualMove);
        return actualMove;
    }

    @Override
    public void makeBoardSetup(GameState inGameState) {
        String example1 = "SCOUT MINER BOMB SCOUT MINER BOMB FLAG BOMB MINER MINER " +
                "SERGEANT BOMB SERGEANT MAJOR COLONEL LIEUTENANT BOMB LIEUTENANT CAPTAIN SERGEANT " +
                "LIEUTENANT SERGEANT BOMB SPY GENERAL SCOUT MAJOR MAJOR COLONEL SCOUT " +
                "CAPTAIN SCOUT SCOUT LIEUTENANT SCOUT CAPTAIN MINER MARSHAL SCOUT CAPTAIN";
        gameState.interpretAndCopySetup(example1);
        inGameState.copySetup(gameState, playerIndex);
    }

}