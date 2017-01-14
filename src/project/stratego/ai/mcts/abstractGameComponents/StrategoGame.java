package project.stratego.ai.mcts.abstractGameComponents;


import project.stratego.ai.mcts.abstractDefinitions.SearchState;
import project.stratego.ai.mcts.factorys.BoardFactory;
import project.stratego.ai.mcts.factorys.GamePieceFactory;
import project.stratego.ai.mcts.gameObjects.*;

import java.util.ArrayList;

public class StrategoGame extends SearchState {

	private StrategoBoard board;

	// private ArrayList<Player> playerList;

	private Player playerNorth;
	private Player playerSouth;
	private RuntimeData runtimeData;

	// private HashMap<StrategoPiece, Player> inGamePieceOwnershipMap = new HashMap<StrategoPiece, Player>();

	public StrategoGame() {
		
		BoardFactory boardFactory = new BoardFactory();
		this.board = boardFactory.createBoard();
		GamePieceFactory pieceFactory = new GamePieceFactory();
		ArrayList<StrategoPiece> gamePiecesPlayerA = pieceFactory.createPlayerPieces();
		ArrayList<StrategoPiece> gamePiecesPlayerB = pieceFactory.createPlayerPieces();
		playerNorth = new Player(gamePiecesPlayerA);
		playerSouth = new Player(gamePiecesPlayerB);
		runtimeData = new RuntimeData();
		runtimeData.setActivePlayer(playerNorth);
		runtimeData.setDeploymentPhase(true);

	}

	public StrategoGame (StrategoGame aGame){
		BoardFactory boardFactory = new BoardFactory();
		this.board = boardFactory.createBoard();
		Player actPlayer= null;
		if(aGame.getActivePlayer()==1){
			actPlayer = aGame.getPlayerNorth().deepCopyPlayer();
			this.playerNorth = actPlayer;
			this.playerSouth = aGame.getPlayerSouth().deepCopyPlayer();
		}else{
			actPlayer = aGame.getPlayerSouth().deepCopyPlayer();
			this.playerNorth = aGame.getPlayerNorth().deepCopyPlayer();
			this.playerSouth = actPlayer;
		}
		fixPiecePlacement(this);
		RuntimeData dataCopy = aGame.getRuntimeData().cloneRunData(actPlayer);
		this.runtimeData = dataCopy;

	}

	public StrategoBoard getBoard() {
		return board;
	}

	public RuntimeData getRuntimeData() {
		return runtimeData;
	}

	public Player getPlayerSouth() {
		return playerSouth;
	}

	public void setPlayerSouth(Player playerB) {
		this.playerSouth = playerB;
	}

	public Player getPlayerNorth() {
		return playerNorth;
	}

	public void setPlayerNorth(Player playerA) {
		this.playerNorth = playerA;
	}

	@Override
	public SearchState deepCopySelf() {
		StrategoGame gameCopy = new StrategoGame(this);
		return gameCopy;
	}

	@Override
	public int getActivePlayer() {
		if (runtimeData.getActivePlayer() == playerNorth) {
			return 1;
		}
		return 2;
	}

	public Player getActivePlayerObj() {
		return runtimeData.getActivePlayer();
	}

	public Player getActiveOpponentObj() {
		if (getRuntimeData().getActivePlayer() == getPlayerNorth()) {
			return getPlayerSouth();
		}
		return getPlayerNorth();
	}

	public StrategoBoardTile[][] deepCopyBoard(StrategoBoardTile[][] initBoard) {
		StrategoBoardTile[][] copyOfarray = new StrategoBoardTile[initBoard.length][initBoard[0].length];
		for (int i = 0; i < initBoard.length; i++) {
			for (int j = 0; j < initBoard[i].length; j++) {
				StrategoBoardTile tile = new StrategoBoardTile(initBoard[i][j].getTerrainType());
				copyOfarray[i][j] = tile;
			}

		}
		return copyOfarray;
	}

	private void fixPiecePlacement(StrategoGame aGame) {
	
		ArrayList<StrategoPiece> northPieces = aGame.getPlayerNorth().getInGamePieces();
		ArrayList<StrategoPiece> southPieces = aGame.getPlayerSouth().getInGamePieces();
		
		for (int i = 0; i < northPieces.size(); i++) {
			StrategoPiece northPiece = northPieces.get(i);
			aGame.getBoard().getBoardStracture()[northPiece.getyPos()][northPiece.getxPos()]
					.setOccupyingPiece(northPiece);
		}
		for (int i = 0; i < southPieces.size(); i++) {
			aGame.getBoard().getBoardStracture()[southPieces.get(i).getyPos()][southPieces.get(i).getxPos()]
					.setOccupyingPiece(southPieces.get(i));
		}
	
	}

}
