package project.stratego.control;

interface ModelReceiver {

    void sendTrayActiveUpdate(int gameID, int pieceIndex);
    void sendActivePieceUpdate(int gameID, int row, int col);
    void sendResetDeployment(int gameID, int playerIndex);
    void sendPiecePlaced(int gameID, int playerIndex, int pieceIndex, int row, int col);
    void sendPieceMoved(int gameID, int orRow, int orCol, int destRow, int destCol);
    void sendHidePiece(int gameID, int playerIndex, int row, int col);
    void sendRevealPiece(int gameID, int playerIndex, int row, int col);
    void sendAttackLost(int gameID, int orRow, int orCol, int destRow, int destCol);
    void sendAttackTied(int gameID, int orRow, int orCol, int destRow, int destCol);
    void sendAttackWon(int gameID, int orRow, int orCol, int destRow, int destCol);

}
