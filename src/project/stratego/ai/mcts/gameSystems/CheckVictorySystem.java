package project.stratego.ai.mcts.gameSystems;

import project.stratego.ai.mcts.abstractGameComponents.Player;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

public class CheckVictorySystem extends MoveSystem {

	public boolean checkIfFlagCaptured(StrategoGame aGame) {

		Player opponet = super.getActiveOpponent(aGame);
		ArrayList<StrategoPiece> checkList = opponet.getInGamePieces();
		for (int i = 0; i < checkList.size(); i++) {
			if (checkList.get(i).getPieceType() == PieceType.FLAG) {
				return false;
			}
		}
		System.out.println("game over " + aGame.getRuntimeData().getActivePlayer() + "  wins");
		return true;
	}

	public boolean checkForLeagalMoves(StrategoGame aGame) {
		Player player = aGame.getRuntimeData().getActivePlayer();
		ArrayList<StrategoPiece> checkList = player.getInGamePieces();
		for (int i = 0; i < checkList.size(); i++) {
			StrategoPiece testPiece = checkList.get(i);
			if (testPiece.getPieceType() != PieceType.BOMB && testPiece.getPieceType() != PieceType.FLAG) {


			if (checkIfLegalToMove(aGame, testPiece.getYPos(), testPiece.getXPos() + 1)) {
				return true;
			}
			if (checkIfLegalToMove(aGame, testPiece.getYPos(), testPiece.getXPos() - 1)) {
				return true;
			}
			if (checkIfLegalToMove(aGame, testPiece.getYPos() - 1, testPiece.getXPos())) {
				return true;
			}
			if (checkIfLegalToMove(aGame, testPiece.getYPos() + 1, testPiece.getXPos())) {
				return true;
			}
			}
		}
		System.out.println("no possible moves   : you loose");
		return false;
	}

	private boolean checkIfLegalToMove(StrategoGame aGame, int yCoord, int xCoord) {
		
		
		
		return super.checkIfInsideBoard(xCoord, yCoord, aGame) && !super.checkIfLake(aGame, xCoord, yCoord)
				&& (super.checkTileFree(aGame, xCoord, yCoord) || super.checkValidOwnerships(aGame, xCoord, yCoord));
	}

}
