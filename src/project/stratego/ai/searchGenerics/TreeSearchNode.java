package project.stratego.ai.searchGenerics;

import project.stratego.game.entities.GameState;

public class TreeSearchNode {

    private GameState state;

    private int playerIndex; // 0 = north, 1 = south, -1 = chance
    private double probability; // if != 1 then it is a chance node

    public TreeSearchNode(GameState state, int playerIndex) {
        this.state = state;
        this.playerIndex = playerIndex;
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public double getProbability() {
        return probability;
    }

    public boolean isChanceNode() {
        return probability == 1.0;
    }

}
