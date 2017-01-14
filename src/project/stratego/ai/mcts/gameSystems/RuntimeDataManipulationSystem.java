package project.stratego.ai.mcts.gameSystems;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.SetActivePieceEvent;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

public class RuntimeDataManipulationSystem {

	public void changeActivePlayer(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		if (aGame.getRuntimeData().getActivePlayer() == aGame.getPlayerNorth()) {
			aGame.getRuntimeData().setActivePlayer(aGame.getPlayerSouth());
			aGame.getRuntimeData().setActivePiece(null);
			aGame.getRuntimeData().setActivePlayerHasAction(true);
		} else {
			aGame.getRuntimeData().setActivePlayer(aGame.getPlayerNorth());
			aGame.getRuntimeData().setActivePiece(null);
			aGame.getRuntimeData().setActivePlayerHasAction(true);
		}


	}

	public void setActivePiece(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		
		SetActivePieceEvent trueEvent=(SetActivePieceEvent) anEvent;
		StrategoPiece tempPiece = aGame.getBoard().getBoardStracture()[trueEvent.getyCoord()][trueEvent.getxCoord()]
				.getOccupyingPiece();
		System.out.println(tempPiece + "   piece in given Coords");
		System.out.println("id of activity try tile"
				+ aGame.getBoard().getBoardStracture()[trueEvent.getyCoord()][trueEvent.getxCoord()]);

		System.out.println("event Coords for activity : " + trueEvent.getxCoord() + "  " + trueEvent.getyCoord());
		if (tempPiece != null) {
			if (belongsToActivePlayer(aGame, trueEvent)) {
				aGame.getRuntimeData().setActivePiece(
						aGame.getBoard().getBoardStracture()[trueEvent.getyCoord()][trueEvent.getxCoord()].getOccupyingPiece());
				System.out.println("active Piece was set");
			}

		} else {
			System.out.println("no piece in tile");
		}
		
	}

	private boolean belongsToActivePlayer(StrategoGame aGame, SetActivePieceEvent anEvent) {
		ArrayList<StrategoPiece> checkList = aGame.getRuntimeData().getActivePlayer().getInGamePieces();
		
		for(int i= 0;i<checkList.size();i++){
			boolean check = checkList.get(i).getPieceID() == aGame.getBoard().getBoardStracture()[anEvent.getyCoord()][anEvent
					.getxCoord()].getOccupyingPiece().getPieceID();
			// System.out.println(check);
			if (check) {
				return true;
			}
		}
		return false;
	}

}
