package project.stratego.ai.wrappers;

import project.stratego.ai.mcts.abstractDefinitions.TreeNode;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.aiPack.*;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.events.StrategoMoveEvent;
import project.stratego.ai.mcts.gameLogic.SystemsManager;

public class WrapperHelp {

    // initialise at start of the game
    private SystemsManager manager = new SystemsManager();
    private StrategoMoveGenerator generator = new StrategoMoveGenerator(manager);
    private StrategoRules rules = new StrategoRules(manager);
    private StrategoPlaythrough playthrough = new StrategoPlaythrough(generator, rules);
    private StrategoMCTSPerformer performer = new StrategoMCTSPerformer(new StrategoRules(manager), generator, playthrough);

    // start
    TreeNode<StrategoGame, StrategoAbstractEvent> node = performer.runMCTS(new StrategoNode(null));
    StrategoMoveEvent move = (StrategoMoveEvent) node.getAction();
    // end

}
