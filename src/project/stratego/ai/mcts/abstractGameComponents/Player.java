package project.stratego.ai.mcts.abstractGameComponents;

import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

public class Player {
	private ArrayList<StrategoPiece> inGamePieces;
	private ArrayList<StrategoPiece> unDeployedPieces;
	private ArrayList<StrategoPiece> piecesKnownByOpponent;

	public Player(ArrayList<StrategoPiece> inGamePieces) {
		super();
		this.inGamePieces = inGamePieces;
		this.unDeployedPieces = new ArrayList<StrategoPiece>(this.inGamePieces);
		this.piecesKnownByOpponent = new ArrayList<StrategoPiece>();
	}

	public Player deepCopyPlayer() {

		Player copy = new Player(deepCopyPieces(inGamePieces));
		copy.setPiecesKnownByOpponent(deepCopyPieces(piecesKnownByOpponent));
		return copy;
	}

	public ArrayList<StrategoPiece> getInGamePieces() {
		return inGamePieces;
	}

	public void setInGamePieces(ArrayList<StrategoPiece> inGamePieces) {
		this.inGamePieces = inGamePieces;
	}

	public ArrayList<StrategoPiece> getUnDeployedPieces() {
		return unDeployedPieces;
	}

	public void setUnDeployedPieces(ArrayList<StrategoPiece> unDeployedPieces) {
		this.unDeployedPieces = unDeployedPieces;
	}

	public ArrayList<StrategoPiece> getPiecesKnownByOpponent() {
		return piecesKnownByOpponent;
	}

	public void setPiecesKnownByOpponent(ArrayList<StrategoPiece> piecesKnownByOpponent) {
		this.piecesKnownByOpponent = piecesKnownByOpponent;
	}

	public void addKnownPiece(StrategoPiece aPiece) {
		piecesKnownByOpponent.add(aPiece);
	}

	private ArrayList<StrategoPiece> deepCopyPieces(ArrayList<StrategoPiece> pieces) {
		ArrayList<StrategoPiece> returnList = new ArrayList<StrategoPiece>();
		for (int i = 0; i < pieces.size(); i++) {
			returnList.add(pieces.get(i).copyPiece());

		}

		return returnList;
	}

}
