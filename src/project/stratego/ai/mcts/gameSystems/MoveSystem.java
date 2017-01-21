package project.stratego.ai.mcts.gameSystems;

import java.util.ArrayList;

import project.stratego.ai.mcts.abstractGameComponents.Player;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.ChangeActivePlayerEvent;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.events.StrategoMoveEvent;
import project.stratego.ai.mcts.gameObjects.PieceType;
import project.stratego.ai.mcts.gameObjects.StrategoPiece;
import project.stratego.ai.mcts.gameObjects.TerrainType;
import project.stratego.ai.mcts.logger.Logger;

public class MoveSystem {

	private AttackSystem attackSystem;
	private RuntimeDataManipulationSystem runtimeSystem;

	public MoveSystem() {
		this.attackSystem = new AttackSystem();
		this.runtimeSystem = new RuntimeDataManipulationSystem();
	}

	public void moveActivePiece(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		
		StrategoMoveEvent trueEvent = (StrategoMoveEvent) anEvent;
		
		StrategoPiece movingPiece = aGame.getRuntimeData().getActivePiece();
		

		int targetXcoord = movingPiece.getxPos() + trueEvent.getdX();
		int targetYcoord = movingPiece.getyPos() + trueEvent.getdY();
		

    // System.out.println("move system input target coord x  " + targetXcoord);
    // System.out.println("move system input target coord y  " + targetYcoord);
    // System.out.println("MOVE SYSTEM RUNNING");
		if (!aGame.getRuntimeData().isActivePlayerHasAction()) {
      Logger.println("not your  turn");
			return;

		}
		if (!isActivePieceMovable(aGame)) {
			return;
		}
		if (movingPiece.getPieceType() == PieceType.SCOUT) {
			int pathLength = trueEvent.getPathLength();
			targetXcoord = movingPiece.getxPos() + trueEvent.getdX() * pathLength;
			targetYcoord = movingPiece.getyPos() + trueEvent.getdY() * pathLength;

			if (!checkScoutPath(aGame, trueEvent, targetXcoord, targetYcoord)) {
				return;
			}

		}
		
		if (!checkIfInsideBoard(targetXcoord, targetYcoord, aGame)) {
			System.out.println("illegal Coords");
			return;
		}

		if (!checkTileFree(aGame, targetXcoord, targetYcoord)) {
			System.out.println("tile taken  ");
			return;
		}
		if (checkIfLake(aGame, targetXcoord, targetYcoord)) {
			System.out.println("tile  is a lake");
			return;
		}
		


		
    // System.out.println("old w x pos " + movingPiece.getxPos() + " old y pos " + movingPiece.getxPos());
		aGame.getBoard().getBoardStracture()[movingPiece.getyPos()][movingPiece.getxPos()].setOccupyingPiece(null);
		movingPiece.setxPos(targetXcoord);
		movingPiece.setyPos(targetYcoord);
		aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].setOccupyingPiece(movingPiece);
		aGame.getRuntimeData().setActivePlayerHasAction(false);

    // System.out.println("new x pos " + movingPiece.getxPos() + " new y pos " + movingPiece.getyPos());
    // System.out.println("activePieceNewPosition x  " + aGame.getRuntimeData().getActivePiece().getxPos());
    // System.out.println("activePieceNewPosition y  " + aGame.getRuntimeData().getActivePiece().getyPos());
    // System.out.println(movingPiece);
    // System.out.println(aGame.getRuntimeData().getActivePiece());

		}

	private boolean checkScoutPath(StrategoGame aGame, StrategoMoveEvent anEvent, int targetXcoord, int targetYcoord) {
		StrategoPiece movingPiece = aGame.getRuntimeData().getActivePiece();
		for (int i = movingPiece.getyPos() + anEvent.getdY(); i <= targetYcoord; i++) {
			for (int j = movingPiece.getxPos() + anEvent.getdX(); j <= targetXcoord; j++) {
				if (!checkIfInsideBoard(j, i, aGame)) {
					System.out.println("illegal target Coords");
					return false;
				}

				if (!checkTileFree(aGame, j, i)) {
					System.out.println("blocked path");
					return false;
				}
				if (checkIfLake(aGame, j, i)) {
					System.out.println("lake in path");
					return false;
				}


			}

		}
		return true;
	}


	public void moveActivePieceForAttack(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		StrategoMoveEvent trueEvent = (StrategoMoveEvent) anEvent;
		StrategoPiece movingPiece = aGame.getRuntimeData().getActivePiece();
		int targetXcoord = aGame.getRuntimeData().getActivePiece().getxPos() + trueEvent.getdX();
		int targetYcoord = aGame.getRuntimeData().getActivePiece().getyPos() + trueEvent.getdY();

		if (!aGame.getRuntimeData().isActivePlayerHasAction()) {
      Logger.println("not your turn");
			return;
		}

		if (!isActivePieceMovable(aGame)) {
			return;
		}
		if (movingPiece.getPieceType() == PieceType.SCOUT) {
			int pathLength = trueEvent.getPathLength();
			targetXcoord = movingPiece.getxPos() + trueEvent.getdX() * pathLength;
			targetYcoord = movingPiece.getyPos() + trueEvent.getdY() * pathLength;

			if (!checkScoutPath(aGame, trueEvent, targetXcoord - Math.abs(trueEvent.getdX()),
					targetYcoord - Math.abs(trueEvent.getdY()))) {
				return;
			}
		}
		System.out.println("moveFORattack  system  x input :" + targetXcoord);
		System.out.println("moveFORattack  system  y input :" + targetYcoord);

		if (!checkIfInsideBoard(targetXcoord, targetYcoord, aGame)) {
			System.out.println("illegal Coords");
			return;
		}

		if (checkIfLake(aGame, targetXcoord, targetYcoord)) {
			System.out.println("tile  is a lake");
			return;
		}


		if (checkTileFree(aGame, targetXcoord, targetYcoord)) {
			System.out.println("noOneToAttack  in  x= " + targetXcoord + "  y= " + targetYcoord);
			return;
		}
		if (!checkValidOwnerships(aGame, targetXcoord, targetYcoord)) {
			System.out.println("cant attack your own units");
			return;

		}
		System.out.println("moveFORattack  system  x input :" + targetXcoord);
		System.out.println("moveFORattack  system  y input :" + targetYcoord);

		aGame.getBoard().getBoardStracture()[movingPiece.getyPos()][movingPiece.getxPos()].setOccupyingPiece(null);
		movingPiece.setxPos(targetXcoord);
		movingPiece.setyPos(targetYcoord);
		aGame.getRuntimeData().setAttackToResolve(true);
		aGame.getRuntimeData().setActivePlayerHasAction(false);


	}

	// method for AI different function than those in the game
	public void applyAction(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		StrategoMoveEvent trueEvent = (StrategoMoveEvent) anEvent;
		StrategoPiece movingPiece = aGame.getBoard().getBoardStracture()[trueEvent.getOriginY()][trueEvent.getOrigintX()]
				.getOccupyingPiece();
		int targetXcoord = trueEvent.getdX();
		int targetYcoord = trueEvent.getdY();


		if (movingPiece == null) {
			System.out.println("Move Sys line 184 " + " null piece " + trueEvent.getOriginY() + " " + trueEvent.getdY()
					+ " Y " + trueEvent.getOrigintX() + " " + trueEvent.getdX() + " free "
					+ checkTileFree(aGame, targetXcoord, targetYcoord) + " own ");
			// System.out.println();
			// System.out.println(" own " + checkValidOwnerships(aGame, targetXcoord, targetYcoord));
		}
		if (movingPiece.getPieceType() == PieceType.BOMB || movingPiece.getPieceType() == PieceType.FLAG) {
			return;
		}

		if (!checkIfInsideBoard(targetXcoord, targetYcoord, aGame)) {
			throw new RuntimeException("illegal coords");
		}

		if (checkIfLake(aGame, targetXcoord, targetYcoord)) {
			throw new RuntimeException("tile  is a lake");
		}

		if (!checkTileFree(aGame, targetXcoord, targetYcoord)) {
			if (!checkValidOwnerships(aGame, targetXcoord, targetYcoord)) {
				throw new RuntimeException("invalid ownerships");

			} else {
				StrategoPiece attackPiece = movingPiece;
				aGame.getBoard().getBoardStracture()[movingPiece.getyPos()][movingPiece.getxPos()]
						.setOccupyingPiece(null);
				StrategoPiece defendPiece = aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord]
						.getOccupyingPiece();
				attackSystem.resolveAttack(attackPiece, defendPiece, aGame);
				// if (defendPiece == null) {
				// System.out.println("no def piece");
				// }
				attackPiece.setyPos(targetYcoord);
				attackPiece.setxPos(targetXcoord);
				defendPiece.setyPos(targetYcoord);
				defendPiece.setxPos(targetXcoord);
				// aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece().setyPos(
				// targetYcoord);
				// aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece().setxPos(
				// targetXcoord);
				runtimeSystem.changeActivePlayer(aGame, new ChangeActivePlayerEvent());
				return;

			}
		}

		aGame.getBoard().getBoardStracture()[movingPiece.getyPos()][movingPiece.getxPos()].setOccupyingPiece(null);
		movingPiece.setxPos(targetXcoord);
		movingPiece.setyPos(targetYcoord);
		aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].setOccupyingPiece(movingPiece);
		runtimeSystem.changeActivePlayer(aGame, new ChangeActivePlayerEvent());


	}


	public boolean checkTileFree(StrategoGame aGame, int targetXcoord, int targetYcoord) {
		return (aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece() == null);
	}

	public boolean checkIfInsideBoard(int x, int y, StrategoGame aGame) {
		// boolean b = (y >= 0) && (x >= 0) && (x <= aGame.getBoard().getBoardStracture()[0].length - 1)
		// && (y <= aGame.getBoard().getBoardStracture().length - 1);
		// System.out.println(("borad lngth " + aGame.getBoard().getBoardStracture().length) + " "
		// + aGame.getBoard().getBoardStracture()[0].length);
		// System.out.println("x " + x + " y " + y + " ret " + b);
		return (y >= 0) && (x >= 0) && (x <= aGame.getBoard().getBoardStracture()[0].length - 1)
				&& (y <= aGame.getBoard().getBoardStracture().length - 1);

	}

	public boolean checkIfLake(StrategoGame aGame, int targetXcoord, int targetYcoord) {
		return (aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getTerrainType() == TerrainType.LAKE);

	}

	public boolean checkValidOwnerships(StrategoGame aGame, int targetXcoord, int targetYcoord) {
		StrategoPiece target=aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece();
		ArrayList<StrategoPiece> checkList = getActiveOpponent(aGame).getInGamePieces();
		for(int i=0;i<checkList.size();i++){
			if(target.getPieceID()==checkList.get(i).getPieceID()){
				return true;
				
			}
		}
		return false;
	}

	public Player getActiveOpponent(StrategoGame aGame) {
		if (aGame.getRuntimeData().getActivePlayer() == aGame.getPlayerNorth()) {
			return aGame.getPlayerSouth();
		}
		return aGame.getPlayerNorth();
	}

	private boolean isActivePieceMovable(StrategoGame aGame) {
		if (aGame.getRuntimeData().getActivePiece().getPieceType() == PieceType.BOMB
				|| aGame.getRuntimeData().getActivePiece().getPieceType() == PieceType.FLAG) {
			System.out.println("flags and bombs cant move");
			return false;
		}
		return true;
	}

}
