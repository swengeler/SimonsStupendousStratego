package project.stratego.ai.searchGenerics;

import project.stratego.game.entities.GameState;

public class TreeSearchNode {

    private GameState state;

    private int identifyingIndex; // 0 = north, 1 = south, -1 = chance

    public TreeSearchNode(GameState state, int identifyingIndex) {
        this.state = state;
        this.identifyingIndex = identifyingIndex;
    }

}
