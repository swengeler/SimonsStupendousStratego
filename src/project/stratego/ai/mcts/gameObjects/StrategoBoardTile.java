package project.stratego.ai.mcts.gameObjects;

public class StrategoBoardTile {

	private StrategoPiece occupyingPiece;

	private TerrainType terrainType;

	public StrategoBoardTile(TerrainType terrainType) {
		this.terrainType = terrainType;
	}

	public StrategoPiece getOccupyingPiece() {
		return occupyingPiece;
	}

	public void setOccupyingPiece(StrategoPiece occupyingPiece) {
		this.occupyingPiece = occupyingPiece;
	}

	public TerrainType getTerrainType() {
		return terrainType;
	}

}
