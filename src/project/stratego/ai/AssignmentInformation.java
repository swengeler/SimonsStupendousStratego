package project.stratego.ai;

import project.stratego.game.entities.Piece;

import java.util.HashMap;

public class AssignmentInformation {

    private Piece assignedPiece;

    private double[][] previousProbabilities;

    public AssignmentInformation(Piece assignedPiece, HashMap<Piece, double[]> probabilitiesMap) {
        this.assignedPiece = assignedPiece;
        previousProbabilities = new double[40][0];
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            previousProbabilities[counter++] = probabilitiesMap.get(p).clone();
        }
    }

    public Piece getAssignedPiece() {
        return assignedPiece;
    }

    public void replaceProbabilities(HashMap<Piece, double[]> probabilitiesMap) {
        int counter = 0;
        for (Piece p : probabilitiesMap.keySet()) {
            probabilitiesMap.replace(p, previousProbabilities[counter++]);
        }
    }

}
