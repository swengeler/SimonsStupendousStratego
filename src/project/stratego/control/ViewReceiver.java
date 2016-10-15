package project.stratego.control;

import project.stratego.ui.StrategoFrame;

interface ViewReceiver {

    void setStrategoFrame(StrategoFrame frame);
    void sendResetGame();
    void sendAutoDeploy();
    void sendPlayerReady();
    void sendTrayPieceSelected(int playerIndex, int index);
    void sendBoardTileSelected(int row, int col);

}
