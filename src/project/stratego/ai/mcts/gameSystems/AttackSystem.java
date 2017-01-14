package project.stratego.ai.mcts.gameSystems;

import project.stratego.ai.mcts.abstractGameComponents.*;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;

import java.util.ArrayList;

public class AttackSystem {

	public void resolveAttack(StrategoGame aGame, StrategoAbstractEvent anEvent) {

		if (!aGame.getRuntimeData().isAttackToResolve()) {
			return;
		}
		StrategoPiece attackingPiece = aGame.getRuntimeData().getActivePiece();
		StrategoPiece defendingPiece = aGame.getBoard().getBoardStracture()[attackingPiece.getyPos()][attackingPiece
				.getxPos()].getOccupyingPiece();

		if (attackWins(attackingPiece, defendingPiece)) {
			System.out
.println("Attacker wins   " + attackingPiece.getPieceType() + " vs "
					+ defendingPiece.getPieceType());
			aGame.getRuntimeData().setGetOutInfo(
					"Attacker wins   " + attackingPiece.getPieceType() + " vs " + defendingPiece.getPieceType());
			aGame.getBoard().getBoardStracture()[attackingPiece.getyPos()][attackingPiece.getxPos()]
					.setOccupyingPiece(attackingPiece);
			
			defendingPiece.setxPos(-100);
			defendingPiece.setyPos(-100);

			ArrayList<StrategoPiece> checkList = getActiveOpponent(aGame).getInGamePieces();

			removePieceFromGame(defendingPiece, checkList);
			aGame.getRuntimeData().setAttackToResolve(false);
		} else if (combatIsDraw(attackingPiece, defendingPiece)) {

			System.out.println("draw");
			aGame.getBoard().getBoardStracture()[attackingPiece.getyPos()][attackingPiece.getyPos()]
					.setOccupyingPiece(null);
			
			attackingPiece.setxPos(-100);
			attackingPiece.setyPos(-100);
			defendingPiece.setxPos(-100);
			defendingPiece.setyPos(-100);

			ArrayList<StrategoPiece> checkList = getActiveOpponent(aGame).getInGamePieces();
			removePieceFromGame(defendingPiece, checkList);
			checkList = aGame.getRuntimeData().getActivePlayer().getInGamePieces();
			removePieceFromGame(attackingPiece, checkList);
			aGame.getRuntimeData().setActivePiece(null);
			aGame.getRuntimeData().setAttackToResolve(false);

		} else {
			System.out
					.println("defender wins" + attackingPiece.getPieceType() + " vs " + defendingPiece.getPieceType());
		attackingPiece.setxPos(-100);
		attackingPiece.setyPos(-100);
		ArrayList<StrategoPiece> checkList = aGame.getRuntimeData().getActivePlayer().getInGamePieces();
			removePieceFromGame(attackingPiece, checkList);
			aGame.getRuntimeData().setActivePiece(null);
			aGame.getRuntimeData().setAttackToResolve(false);
		}
	}

	public boolean combatIsDraw(StrategoPiece attackingPiece, StrategoPiece defendingPiece) {
		return (int) (PieceHierarchyData.pieceLvlMap.get(attackingPiece.getPieceType())) == (int) (PieceHierarchyData.pieceLvlMap
				.get(defendingPiece.getPieceType()));
	}

	public boolean attackWins(StrategoPiece attackingPiece, StrategoPiece defendingPiece) {
		if (attackingPiece.getPieceType() == PieceType.SPY && defendingPiece.getPieceType() == PieceType.MARSHAL) {
			return true;
		}
		if (attackingPiece.getPieceType() == PieceType.MINER && defendingPiece.getPieceType() == PieceType.BOMB) {
			return true;
		}
		return (PieceHierarchyData.pieceLvlMap.get(attackingPiece.getPieceType())) > (PieceHierarchyData.pieceLvlMap
				.get(defendingPiece.getPieceType()));
	}

	public void removePieceFromGame(StrategoPiece aPiece, ArrayList<StrategoPiece> checkList) {
		for (int i = 0; i < checkList.size(); i++) {
			if (aPiece.getPieceID() == checkList.get(i).getPieceID()) {
				checkList.remove(i);

			}
		}
	}
	
	public Player getActiveOpponent(StrategoGame aGame) {
		if (aGame.getRuntimeData().getActivePlayer() == aGame.getPlayerNorth()) {
			return aGame.getPlayerSouth();
		}
		return aGame.getPlayerNorth();
	}

	// Ai method
	public void resolveAttack(StrategoPiece attackingPiece, StrategoPiece defendingPiece, StrategoGame aGame) {
    // System.out.println("Resolving attack " + attackingPiece + " " + defendingPiece);
		if (attackWins(attackingPiece, defendingPiece)) {


			aGame.getBoard().getBoardStracture()[attackingPiece.getyPos()][attackingPiece.getxPos()]
					.setOccupyingPiece(attackingPiece);

			defendingPiece.setxPos(-100);
			defendingPiece.setyPos(-100);

			ArrayList<StrategoPiece> checkList = getActiveOpponent(aGame).getInGamePieces();

			removePieceFromGame(defendingPiece, checkList);

		} else if (combatIsDraw(attackingPiece, defendingPiece)) {


			aGame.getBoard().getBoardStracture()[attackingPiece.getyPos()][attackingPiece.getyPos()]
					.setOccupyingPiece(null);

			attackingPiece.setxPos(-100);
			attackingPiece.setyPos(-100);
			defendingPiece.setxPos(-100);
			defendingPiece.setyPos(-100);

			ArrayList<StrategoPiece> checkList = getActiveOpponent(aGame).getInGamePieces();
			removePieceFromGame(defendingPiece, checkList);
			checkList = aGame.getRuntimeData().getActivePlayer().getInGamePieces();
			removePieceFromGame(attackingPiece, checkList);
		} else {

			attackingPiece.setxPos(-100);
			attackingPiece.setyPos(-100);
			ArrayList<StrategoPiece> checkList = aGame.getRuntimeData().getActivePlayer().getInGamePieces();
			removePieceFromGame(attackingPiece, checkList);

		}

	}
}
