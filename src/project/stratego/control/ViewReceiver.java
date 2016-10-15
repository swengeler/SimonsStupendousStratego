package project.stratego.control;

import project.stratego.ui.StrategoFrame;

interface ViewReceiver {

    void setStrategoFrame(StrategoFrame frame);
    void sendResetGame();
    void sendAutoDeploy(int playerIndex);
    void sendPlayerReady(int playerIndex);
    void sendTrayPieceSelected(int playerIndex, int index);
    void sendBoardTileSelected(int playerIndex, int row, int col);

}
