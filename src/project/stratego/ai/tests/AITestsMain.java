package project.stratego.ai.tests;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;

public class AITestsMain {

    private static final int numberGames = 200;

    private static int numberMovesMin = Integer.MAX_VALUE;
    private static int numberMovesAvg = 0;
    private static int numberMovesMax = -Integer.MAX_VALUE;

    private static int playerOneWonFlagCapture = 0;
    private static int playerTwoWonFlagCapture = 0;
    private static int playerOneWonNoMovingPieces = 0;
    private static int playerTwoWonNoMovingPieces = 0;

    private static long playTimeMillisMin = Long.MAX_VALUE;
    private static long playTimeMillisAvg = 0;
    private static long playTimeMillisMax = -Long.MAX_VALUE;

    private static int nodesSearchedMin = Integer.MAX_VALUE;
    private static int nodesSearchedAvg = 0;
    private static int nodesSearchedMax = -Integer.MAX_VALUE;

    private static long playerOneSearchTimeMin = Long.MAX_VALUE;
    private static long playerOneSearchTimeAvg = 0;
    private static long playerOneSearchTimeMax = -Long.MAX_VALUE;

    private static long playerTwoSearchTimeMin = Long.MAX_VALUE;
    private static long playerTwoSearchTimeAvg = 0;
    private static long playerTwoSearchTimeMax = -Long.MAX_VALUE;


    public static void main(String[] args) {

        for (int i = 0; i < numberGames; i++) {
            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            System.out.println("RUN NUMBER " + (i + 1));
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
            ModelComManager.getInstance().configureAIMatch();
            AIComManager.getInstance().runAIMatch();
        }
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("RESULTS:");
        System.out.println("-----------------------------------------------------------");
        System.out.println("NUMBER OF MOVES:");
        System.out.println("Minimum: " + numberMovesMin);
        System.out.println("Average: " + (numberMovesAvg / (double) numberGames));
        System.out.println("Maximum: " + numberMovesMax + "\n");

        System.out.println("PLAYTIME:");
        System.out.println("Minimum: " + playTimeMillisMin);
        System.out.println("Average: " + (playTimeMillisAvg / (double) numberGames));
        System.out.println("Maximum: " + playTimeMillisMax + "\n");

        System.out.println("NODES SEARCHED:");
        System.out.println("Minimum: " + nodesSearchedMin);
        System.out.println("Average: " + (nodesSearchedAvg / (double) numberGames));
        System.out.println("Maximum: " + nodesSearchedMax + "\n");

        System.out.println("PLAYER ONE SEARCH TIME:");
        System.out.println("Minimum: " + playerOneSearchTimeMin);
        System.out.println("Average: " + (playerOneSearchTimeAvg / (double) numberGames));
        System.out.println("Maximum: " + playerOneSearchTimeMax + "\n");

        System.out.println("PLAYER TWO SEARCH TIME:");
        System.out.println("Minimum: " + playerTwoSearchTimeMin);
        System.out.println("Average: " + (playerTwoSearchTimeAvg / (double) numberGames));
        System.out.println("Maximum: " + playerTwoSearchTimeMax + "\n");

        System.out.println("WINS BY FLAG CAPTURE:");
        System.out.println("Player 1: " + playerOneWonFlagCapture);
        System.out.println("Player 2: " + playerTwoWonFlagCapture + "\n");

        System.out.println("WINS BY PIECE CAPTURE");
        System.out.println("Player 1: " + playerOneWonNoMovingPieces);
        System.out.println("Player 2: " + playerTwoWonNoMovingPieces);
        System.out.println("-----------------------------------------------------------\n");
    }

    public static void addNumberMoves(int numberMoves) {
        if (numberMoves < numberMovesMin) {
            numberMovesMin = numberMoves;
        }
        if (numberMoves > numberMovesMax) {
            numberMovesMax = numberMoves;
        }
        numberMovesAvg += numberMoves;
    }

    public static void addWin(int playerWonIndex, int winType) {
        if (playerWonIndex == 0) {
            if (winType == 0) {
                playerOneWonFlagCapture++;
            } else if (winType == 1) {
                playerOneWonNoMovingPieces++;
            }
        } else if (playerWonIndex == 1) {
            if (winType == 0) {
                playerTwoWonFlagCapture++;
            } else if (winType == 1) {
                playerTwoWonNoMovingPieces++;
            }
        }
    }

    public static void addPlayTime(long playTimeMillis) {
        if (playTimeMillis < playTimeMillisMin) {
            playTimeMillisMin = playTimeMillis;
        }
        if (playTimeMillis > playTimeMillisMax) {
            playTimeMillisMax = playTimeMillis;
        }
        playTimeMillisAvg += playTimeMillis;
    }

    public static void addNodesSearched(int nodesSearched) {
        if (nodesSearched < nodesSearchedMin) {
            nodesSearchedMin = nodesSearched;
        }
        if (nodesSearched > nodesSearchedMax) {
            nodesSearchedMax = nodesSearched;
        }
        nodesSearchedAvg += nodesSearched;
    }

    public static void addMoveSearchTime(int playerIndex, long searchtimeNanos) {
        if (playerIndex == 0) {
            if (searchtimeNanos < playerOneSearchTimeMin) {
                playerOneSearchTimeMin = searchtimeNanos;
            }
            if (searchtimeNanos > playerOneSearchTimeMax) {
                playerOneSearchTimeMax = searchtimeNanos;
            }
            playerOneSearchTimeAvg += searchtimeNanos;
        } else if (playerIndex == 1) {
            if (searchtimeNanos < playerTwoSearchTimeMin) {
                playerTwoSearchTimeMin = searchtimeNanos;
            }
            if (searchtimeNanos > playerTwoSearchTimeMax) {
                playerTwoSearchTimeMax = searchtimeNanos;
            }
            playerTwoSearchTimeAvg += searchtimeNanos;
        }

    }

    public static void addWinReason() {
        // wtf is this name
        // also, wtf is this method
    }

    public static void printStats() {
        System.out.println("\n-----------------------------------------------------------");
        System.out.println("RESULTS:");
        System.out.println("-----------------------------------------------------------");
        System.out.println("NUMBER OF MOVES:");
        System.out.println("Minimum: " + numberMovesMin);
        System.out.println("Average: " + (numberMovesAvg / (double) numberGames));
        System.out.println("Maximum: " + numberMovesMax + "\n");

        System.out.println("PLAYTIME:");
        System.out.println("Minimum: " + playTimeMillisMin);
        System.out.println("Average: " + (playTimeMillisAvg / (double) numberGames));
        System.out.println("Maximum: " + playTimeMillisMax + "\n");

        System.out.println("NODES SEARCHED:");
        System.out.println("Minimum: " + nodesSearchedMin);
        System.out.println("Average: " + (nodesSearchedAvg / (double) numberGames));
        System.out.println("Maximum: " + nodesSearchedMax + "\n");

        System.out.println("PLAYER ONE SEARCH TIME:");
        System.out.println("Minimum: " + playerOneSearchTimeMin);
        System.out.println("Average: " + (playerOneSearchTimeAvg / (double) numberGames));
        System.out.println("Maximum: " + playerOneSearchTimeMax + "\n");

        System.out.println("PLAYER TWO SEARCH TIME:");
        System.out.println("Minimum: " + playerTwoSearchTimeMin);
        System.out.println("Average: " + (playerTwoSearchTimeAvg / (double) numberGames));
        System.out.println("Maximum: " + playerTwoSearchTimeMax + "\n");

        System.out.println("WINS BY FLAG CAPTURE:");
        System.out.println("Player 1: " + playerOneWonFlagCapture);
        System.out.println("Player 2: " + playerTwoWonFlagCapture + "\n");

        System.out.println("WINS BY PIECE CAPTURE");
        System.out.println("Player 1: " + playerOneWonNoMovingPieces);
        System.out.println("Player 2: " + playerTwoWonNoMovingPieces);
        System.out.println("-----------------------------------------------------------\n");
    }

}
