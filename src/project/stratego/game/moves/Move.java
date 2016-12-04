package project.stratego.game.moves;

public interface Move {

    int getOrRow();
    int getOrCol();
    int getDestRow();
    int getDestCol();
    int length();
    int getPlayerIndex();

}
