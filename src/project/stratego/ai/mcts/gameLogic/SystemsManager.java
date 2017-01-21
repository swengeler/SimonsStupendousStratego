package project.stratego.ai.mcts.gameLogic;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.AttackEvent;
import project.stratego.ai.mcts.events.AutoDeployEvent;
import project.stratego.ai.mcts.events.ChangeActivePlayerEvent;
import project.stratego.ai.mcts.events.DeploymentEvent;
import project.stratego.ai.mcts.events.SetActivePieceEvent;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;
import project.stratego.ai.mcts.events.StrategoMoveEvent;
import project.stratego.ai.mcts.gameSystems.AttackSystem;
import project.stratego.ai.mcts.gameSystems.CheckVictorySystem;
import project.stratego.ai.mcts.gameSystems.DeploymentSystem;
import project.stratego.ai.mcts.gameSystems.MoveSystem;
import project.stratego.ai.mcts.gameSystems.RuntimeDataManipulationSystem;

public class SystemsManager {

	// private List<AbstractStrategoEvent> eventQueue;

	private MoveSystem moveSystem;
	private AttackSystem attackSystem;
	private DeploymentSystem deployementSystem;
	private RuntimeDataManipulationSystem runtimeDataSystem;
	private CheckVictorySystem checkVictorySystem;

	public SystemsManager() {
		attackSystem = new AttackSystem();
		deployementSystem = new DeploymentSystem();
		runtimeDataSystem = new RuntimeDataManipulationSystem();
		moveSystem = new MoveSystem();
		checkVictorySystem = new CheckVictorySystem();

	}

	public void proccessEvent(StrategoAbstractEvent anEvent, StrategoGame aGame) {
		if (anEvent.getClass() == DeploymentEvent.class) {
		deployementSystem.proccessDeployEvent(aGame, anEvent);
		}
		if (anEvent.getClass() == ChangeActivePlayerEvent.class) {
			runtimeDataSystem.changeActivePlayer(aGame, anEvent);
			checkVictorySystem.checkForLeagalMoves(aGame);

		}
		if (anEvent.getClass() == SetActivePieceEvent.class) {
			runtimeDataSystem.setActivePiece(aGame, anEvent);
		}
		if (anEvent.getClass() == StrategoMoveEvent.class) {
			moveSystem.moveActivePiece(aGame, anEvent);
		}
		if (anEvent.getClass() == AttackEvent.class) {
			moveSystem.moveActivePieceForAttack(aGame, anEvent);
			attackSystem.resolveAttack(aGame, anEvent);
			checkVictorySystem.checkIfFlagCaptured(aGame);
		}
		if (anEvent.getClass() == AutoDeployEvent.class) {
			deployementSystem.autoDeployArmy(aGame, anEvent);

		}

	}

	public MoveSystem getMoveSystem() {
		return moveSystem;
	}

	public AttackSystem getAttackSystem() {
		return attackSystem;
	}

	public DeploymentSystem getDeployementSystem() {
		return deployementSystem;
	}

	public RuntimeDataManipulationSystem getRuntimeDataSystem() {
		return runtimeDataSystem;
	}

	public CheckVictorySystem getCheckVictorySystem() {
		return checkVictorySystem;
	}

}
