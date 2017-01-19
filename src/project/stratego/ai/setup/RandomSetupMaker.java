package project.stratego.ai.setup;

import project.stratego.game.entities.*;
import project.stratego.game.utils.PieceFactory;
import project.stratego.game.utils.PlayerType;

public class RandomSetupMaker implements SetupMaker {

    @Override
    public void makeBoardSetup(GameState state, int playerIndex) {
        PieceFactory pieceFactory = new PieceFactory();
        Player tempPlayer = state.getPlayer(playerIndex);
        tempPlayer.getActivePieces().clear();
        tempPlayer.getDeadPieces().clear();
        Piece temp;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 10; col++) {
                temp = pieceFactory.makeRandomPiece(tempPlayer.getType());
                tempPlayer.getActivePieces().add(temp);
                //tempPlayer.getHiddenPieces().add(temp);
                state.getBoardArray()[tempPlayer.getType() == PlayerType.NORTH ? row : 9 - row][col].setOccupyingPiece(temp);
            }
        }
    }

}
