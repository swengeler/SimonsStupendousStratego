package project.stratego.ai.mcts.gameSystems;

import project.stratego.ai.mcts.abstractGameComponents.Player;
import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.*;
import project.stratego.ai.mcts.gameObjects.*;

import java.util.ArrayList;

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
		

		int targetXcoord = movingPiece.getXPos() + trueEvent.getDestX();
		int targetYcoord = movingPiece.getYPos() + trueEvent.getDestY();
		

    // System.out.println("move system input target coord x  " + targetXcoord);
    // System.out.println("move system input target coord y  " + targetYcoord);
    // System.out.println("MOVE SYSTEM RUNNING");
		if (!aGame.getRuntimeData().isActivePlayerHasAction()) {
			// Logger.println("not your  turn");
			return;

		}
		if (!isActivePieceMovable(aGame)) {
			return;
		}
		if (movingPiece.getPieceType() == PieceType.SCOUT) {
			int pathLength = trueEvent.getPathLength();
			targetXcoord = movingPiece.getXPos() + trueEvent.getDestX() * pathLength;
			targetYcoord = movingPiece.getYPos() + trueEvent.getDestY() * pathLength;

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
		


		
    // System.out.println("old w x pos " + movingPiece.getXPos() + " old y pos " + movingPiece.getXPos());
		aGame.getBoard().getBoardStracture()[movingPiece.getYPos()][movingPiece.getXPos()].setOccupyingPiece(null);
		movingPiece.setXPos(targetXcoord);
		movingPiece.setYPos(targetYcoord);
		aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].setOccupyingPiece(movingPiece);
		aGame.getRuntimeData().setActivePlayerHasAction(false);

    // System.out.println("new x pos " + movingPiece.getXPos() + " new y pos " + movingPiece.getYPos());
    // System.out.println("activePieceNewPosition x  " + aGame.getRuntimeData().getActivePiece().getXPos());
    // System.out.println("activePieceNewPosition y  " + aGame.getRuntimeData().getActivePiece().getYPos());
    // System.out.println(movingPiece);
    // System.out.println(aGame.getRuntimeData().getActivePiece());

		}

	private boolean checkScoutPath(StrategoGame aGame, StrategoMoveEvent anEvent, int targetXcoord, int targetYcoord) {
		StrategoPiece movingPiece = aGame.getRuntimeData().getActivePiece();
		for (int i = movingPiece.getYPos() + anEvent.getDestY(); i <= targetYcoord; i++) {
			for (int j = movingPiece.getXPos() + anEvent.getDestX(); j <= targetXcoord; j++) {
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
		int targetXcoord = aGame.getRuntimeData().getActivePiece().getXPos() + trueEvent.getDestX();
		int targetYcoord = aGame.getRuntimeData().getActivePiece().getYPos() + trueEvent.getDestY();

		if (!aGame.getRuntimeData().isActivePlayerHasAction()) {
			// Logger.println("not your turn");
			return;
		}

		if (!isActivePieceMovable(aGame)) {
			return;
		}
		if (movingPiece.getPieceType() == PieceType.SCOUT) {
			int pathLength = trueEvent.getPathLength();
			targetXcoord = movingPiece.getXPos() + trueEvent.getDestX() * pathLength;
			targetYcoord = movingPiece.getYPos() + trueEvent.getDestY() * pathLength;

			if (!checkScoutPath(aGame, trueEvent, targetXcoord - Math.abs(trueEvent.getDestX()),
					targetYcoord - Math.abs(trueEvent.getDestY()))) {
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

		aGame.getBoard().getBoardStracture()[movingPiece.getYPos()][movingPiece.getXPos()].setOccupyingPiece(null);
		movingPiece.setXPos(targetXcoord);
		movingPiece.setYPos(targetYcoord);
		aGame.getRuntimeData().setAttackToResolve(true);
		aGame.getRuntimeData().setActivePlayerHasAction(false);


	}

	// method for AI different function than those in the game
	public void applyAction(StrategoGame aGame, StrategoAbstractEvent anEvent) {
		StrategoMoveEvent trueEvent = (StrategoMoveEvent) anEvent;
		StrategoPiece movingPiece = aGame.getBoard().getBoardStracture()[trueEvent.getOriginY()][trueEvent.getOrigintX()]
				.getOccupyingPiece();
		int targetXcoord = trueEvent.getDestX();
		int targetYcoord = trueEvent.getDestY();


		if (movingPiece == null) {
			System.out.println("Move Sys line 184 " + " null piece " + trueEvent.getOriginY() + " " + trueEvent.getDestY()
					+ " Y " + trueEvent.getOrigintX() + " " + trueEvent.getDestX() + " free "
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
				aGame.getBoard().getBoardStracture()[movingPiece.getYPos()][movingPiece.getXPos()]
						.setOccupyingPiece(null);
				StrategoPiece defendPiece = aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord]
						.getOccupyingPiece();
				attackSystem.resolveAttack(attackPiece, defendPiece, aGame);
				// if (defendPiece == null) {
				// System.out.println("no def piece");
				// }
				attackPiece.setYPos(targetYcoord);
				attackPiece.setXPos(targetXcoord);
				defendPiece.setYPos(targetYcoord);
				defendPiece.setXPos(targetXcoord);
				// aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece().setYPos(
				// targetYcoord);
				// aGame.getBoard().getBoardStracture()[targetYcoord][targetXcoord].getOccupyingPiece().setXPos(
				// targetXcoord);
				runtimeSystem.changeActivePlayer(aGame, new ChangeActivePlayerEvent());
				return;

			}
		}

		aGame.getBoard().getBoardStracture()[movingPiece.getYPos()][movingPiece.getXPos()].setOccupyingPiece(null);
		movingPiece.setXPos(targetXcoord);
		movingPiece.setYPos(targetYcoord);
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
