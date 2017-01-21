package project.stratego.ai.mcts.abstractGameComponents;

import project.stratego.ai.mcts.gameObjects.StrategoPiece;

public class RuntimeData {

	private Player activePlayer;
	private StrategoPiece activePiece;
	private boolean deploymentPhase = false;
	private boolean attackToResolve = false;
	private boolean activePlayerHasAction = true;

	private String getOutInfo;


	public RuntimeData() {

	}
	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player activePlayer) {
		this.activePlayer = activePlayer;
	}


	public StrategoPiece getActivePiece() {
		return activePiece;
	}

	public void setActivePiece(StrategoPiece activePiece) {
		this.activePiece = activePiece;
	}

	public boolean isDeploymentPhase() {
		return deploymentPhase;
	}

	public void setDeploymentPhase(boolean deploymentPhase) {
		this.deploymentPhase = deploymentPhase;
	}

	public boolean isAttackToResolve() {
		return attackToResolve;
	}

	public void setAttackToResolve(boolean attackToResolve) {
		this.attackToResolve = attackToResolve;
	}
	public String getGetOutInfo() {
		return getOutInfo;
	}
	public void setGetOutInfo(String getOutInfo) {
		this.getOutInfo = getOutInfo;
	}

	public boolean isActivePlayerHasAction() {
		return activePlayerHasAction;
	}

	public void setActivePlayerHasAction(boolean activePlayerHasAction) {
		this.activePlayerHasAction = activePlayerHasAction;
	}

	public RuntimeData cloneRunData(Player activePlayer) {

		RuntimeData copy = new RuntimeData();
		copy.setActivePlayer(activePlayer);
		return copy;

	}



}
