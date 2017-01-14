package project.stratego.ai.mcts.aiPack;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.abstractSearchComponents.MoveGenerator;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.events.StrategoMoveEvent;
import project.stratego.ai.mcts.gameLogic.SystemsManager;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

public class StrategoMoveGenerator extends MoveGenerator<StrategoGame, StrategoAbstractEvent> {

	private SystemsManager systemManager;

	public StrategoMoveGenerator(SystemsManager systemManager) {
		super();
		this.systemManager = systemManager;
	}

	@Override
	public ArrayList<StrategoAbstractEvent> generateAvailiableMoves(StrategoGame state) {
		ArrayList<StrategoAbstractEvent> allMovesList = new ArrayList<StrategoAbstractEvent>();
		ArrayList<StrategoPiece> pieceList= state.getActivePlayerObj().getInGamePieces();
		for (int i=0;i<pieceList.size();i++){
			StrategoPiece piece = pieceList.get(i);
			if (state.getBoard().getBoardStracture()[piece.getyPos()][piece.getxPos()] == null) {
				System.out.println("VERY BAD!");
			}
			ArrayList<StrategoMoveEvent> onePieceList = generateAvailiableMoves(piece, state);
			allMovesList.addAll(onePieceList);
		}


		return allMovesList;
	}

	@Override
	public void applyAction(StrategoGame state, StrategoAbstractEvent action) {

		systemManager.getMoveSystem().applyAction(state, action);

	}

	// public ArrayList<Strate>



	public ArrayList<StrategoMoveEvent> generateAvailiableMoves(StrategoPiece aPiece, StrategoGame aState) {

		ArrayList<StrategoMoveEvent> moveList = new ArrayList<StrategoMoveEvent>();
		if (aPiece.getPieceType() == PieceType.BOMB || aPiece.getPieceType() == PieceType.FLAG) {
			return moveList;
		}
		int targetX = aPiece.getxPos() + 1;
		int targetY = aPiece.getyPos();
		StrategoMoveEvent move1 = new StrategoMoveEvent(aPiece.getxPos(), aPiece.getyPos(), targetX, targetY);
		addValidMovesOnly(moveList, move1, aState);
		targetX = aPiece.getxPos() - 1;
		targetY = aPiece.getyPos();
		StrategoMoveEvent move2 = new StrategoMoveEvent(aPiece.getxPos(), aPiece.getyPos(), targetX, targetY);
		addValidMovesOnly(moveList, move2, aState);
		targetX = aPiece.getxPos();
		targetY = aPiece.getyPos() + 1;
		StrategoMoveEvent move3 = new StrategoMoveEvent(aPiece.getxPos(), aPiece.getyPos(), targetX, targetY);
		addValidMovesOnly(moveList, move3, aState);
		targetX = aPiece.getxPos();
		targetY = aPiece.getyPos() - 1;
		StrategoMoveEvent move4 = new StrategoMoveEvent(aPiece.getxPos(), aPiece.getyPos(), targetX, targetY);
		addValidMovesOnly(moveList, move4, aState);


		return moveList;

	}

	private void addValidMovesOnly(ArrayList<StrategoMoveEvent> movelist, StrategoMoveEvent move, StrategoGame aState) {
		if (checkIfMoveValid(move, aState)) {
			movelist.add(move);
		}

	}

	public boolean checkIfMoveValid(StrategoMoveEvent event, StrategoGame aState) {

		if (!systemManager.getMoveSystem().checkIfInsideBoard(event.getDestX(), event.getDestY(), aState)) {
			return false;
		}
		// System.out.println(event.getDestX() + "    dx dy   " + event.getDestY());
		if (systemManager.getMoveSystem().checkIfLake(aState, event.getDestX(), event.getDestY())) {
			return false;
		}
		if (!systemManager.getMoveSystem().checkTileFree(aState, event.getDestX(), event.getDestY())) {
			if (!systemManager.getMoveSystem().checkValidOwnerships(aState, event.getDestX(), event.getDestY())) {
				return false;
			}
		}

			return true;
	}

}
