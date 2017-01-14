package project.stratego.ai.mcts.gameLogic;

import project.stratego.ai.mcts.abstractGameComponents.StrategoGame;
import project.stratego.ai.mcts.events.StrategoAbstractEvent;

public class MainGameLogic {
	
	private StrategoGame stratego;

	private SystemsManager systemsManager;



	public MainGameLogic(StrategoGame aGame){
		this.stratego=aGame;
		
		systemsManager = new SystemsManager();

	}

	public void notifyForEvent(StrategoAbstractEvent strategoEvent) {


			systemsManager.proccessEvent(strategoEvent, stratego);



}

	public SystemsManager getManager() {
		return systemsManager;
	}
}