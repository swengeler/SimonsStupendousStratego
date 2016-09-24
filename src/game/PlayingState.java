package game;

/**
 *
 */
public class PlayingState extends GameState {

    public PlayingState(StrategoGame parent, Player firstPlayer, Player secondPlayer) {
        super(parent, firstPlayer, secondPlayer);
    }

    public void processBoardSelect(int row, int col) {}

    public void processPlayerReady() {}

    public void processSwitch() {
        if (firstPlayer.isActive()) {
            firstPlayer.setActive(false);
            secondPlayer.setActive(true);
        } else {
            firstPlayer.setActive(true);
            secondPlayer.setActive(false);
        }
    }

}
