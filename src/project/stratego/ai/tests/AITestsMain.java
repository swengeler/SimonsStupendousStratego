package project.stratego.ai.tests;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

public class AITestsMain {

    private static final boolean REAL_TESTS = false;
    private static int REP_PER_CONFIG = 1;

    private static int numberGames = 1;
    private static int counter = 0;

    private static int numberMovesMin = Integer.MAX_VALUE;
    private static int numberMovesAvg = 0;
    private static int numberMovesMax = -Integer.MAX_VALUE;

    private static int playerOneNumberMovesSum = 0;
    private static int playerTwoNumberMovesSum = 0;

    private static int playerOneWonFlagCapture = 0;
    private static int playerTwoWonFlagCapture = 0;
    private static int playerOneWonNoMovingPieces = 0;
    private static int playerTwoWonNoMovingPieces = 0;
    private static int ties = 0;

    private static long playTimeMillisMin = Long.MAX_VALUE;
    private static long playTimeMillisAvg = 0;
    private static long playTimeMillisMax = -Long.MAX_VALUE;

    private static int playerOneLeafNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerOneLeafNodesSearchedAvg = 0;
    private static int playerOneLeafNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerTwoLeafNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerTwoLeafNodesSearchedAvg = 0;
    private static int playerTwoLeafNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerOneMinMaxNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerOneMinMaxNodesSearchedAvg = 0;
    private static int playerOneMinMaxNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerTwoMinMaxNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerTwoMinMaxNodesSearchedAvg = 0;
    private static int playerTwoMinMaxNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerOneChanceNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerOneChanceNodesSearchedAvg = 0;
    private static int playerOneChanceNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerTwoChanceNodesSearchedMin = Integer.MAX_VALUE;
    private static int playerTwoChanceNodesSearchedAvg = 0;
    private static int playerTwoChanceNodesSearchedMax = -Integer.MAX_VALUE;

    private static int playerOneDepthReachedAvg = 0;
    private static int playerTwoDepthReachedAvg = 0;

    private static int playerOneBestMoveDepthAvg = 0;
    private static int playerTwoBestMoveDepthAvg = 0;

    private static long playerOneSearchTimeMin = Long.MAX_VALUE;
    private static long playerOneSearchTimeAvg = 0;
    private static long playerOneSearchTimeMax = -Long.MAX_VALUE;

    private static long playerTwoSearchTimeMin = Long.MAX_VALUE;
    private static long playerTwoSearchTimeAvg = 0;
    private static long playerTwoSearchTimeMax = -Long.MAX_VALUE;

    public static void main(String[] args) {



        Scanner in = new Scanner(System.in);
        System.out.print("Please enter a command:\n");
        String command = in.nextLine();

        if (command.equals("real")) {
            File directory = new File("/src/project/resources/");

            String[] setups = null;
            if (directory.listFiles() != null) {
                setups = new String[directory.listFiles().length];
                int c = 0;
                for (File f : directory.listFiles()) {
                    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
                        setups[c] = br.readLine();
                        System.out.println("Setup registered: " + f.getName());
                        c++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (setups == null) {
                System.out.println("Problem with loading setups, please try again or fix.");
                return;
            }

            System.out.println(setups.length + " setup(s) loaded.");

            System.out.print("Setups to be used for AI 1 (NORTH):\n");
            command = in.nextLine();
            String[] indeces = command.split(" ");
            int lowerIndexOne = Integer.parseInt(indeces[0]);
            int upperIndexOne = Integer.parseInt(indeces[1]);

            System.out.print("Setups to be used for AI 2 (SOUTH):\n");
            command = in.nextLine();
            indeces = command.split(" ");
            int lowerIndexTwo = Integer.parseInt(indeces[0]);
            int upperIndexTwo = Integer.parseInt(indeces[1]);

            System.out.print("Specify AIs used? (y/n)\n");
            command = in.nextLine();
            String configOne = null;
            String configTwo = null;
            if (command.equalsIgnoreCase("y")) {
                System.out.print("Specify AI 1 (NORTH):\n");
                configOne = in.nextLine();
                if (configOne.equals("")) {
                    configOne = null;
                }
                System.out.print("Specify AI 2 (SOUTH):\n");
                configTwo = in.nextLine();
                if (configTwo.equals("")) {
                    configTwo = null;
                }
            }

            long before;
            counter = 0;
            for (int i = 0; i < REP_PER_CONFIG; i++) {
                for (int j = lowerIndexOne; j <= upperIndexOne; j++) {
                    for (int k = lowerIndexTwo; k <= upperIndexTwo; k++) {
                        System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                        System.out.println("RUN NUMBER " + (counter + 1) + " (with setups " + j + " and " + k + ")");
                        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
                        ModelComManager.getInstance().configureAIMatch(setups[j], setups[k], configOne, configTwo);
                        before = System.currentTimeMillis();
                        AIComManager.getInstance().runAutomaticAIMatch();
                        System.out.println("Completed in " + (System.currentTimeMillis() - before) + " ms");
                        counter++;
                    }
                }
            }
        } else if (command.equals("simple")) {
            String testSetup = "7-3-3-6-3-7-4-11-3-7-6-5-1-2-10-3-8-8-9-3-5-1-5-8-9-6-1-6-7-5-3-4-1-3-4-1-0-1-4-4";

            long before;
            for (counter = 0; counter < numberGames; counter++) {
                System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
                System.out.println("RUN NUMBER " + (counter + 1));
                System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
                ModelComManager.getInstance().configureAIMatch(testSetup, testSetup, null, null);
                before = System.currentTimeMillis();
                AIComManager.getInstance().runAutomaticAIMatch();
                System.out.println("Completed in " + (System.currentTimeMillis() - before) + " ms");
            }
        }

        System.out.println("\n----------------------------------------------------------");
        System.out.println("RESULTS over " + (playerOneWonFlagCapture + playerOneWonNoMovingPieces + playerTwoWonFlagCapture + playerTwoWonNoMovingPieces + ties) + " games:");
        System.out.println("-----------------------------------------------------------");
        System.out.println("NUMBER OF MOVES:");
        System.out.println("Minimum: " + numberMovesMin);
        System.out.println("Average: " + (numberMovesAvg / (double) numberGames));
        System.out.println("Maximum: " + numberMovesMax + "\n");

        System.out.println("PLAYER ONE NUMBER OF MOVES:");
        System.out.println("Average: " + (playerOneNumberMovesSum / (double) numberGames) + "\n");

        System.out.println("PLAYER TWO NUMBER OF MOVES:");
        System.out.println("Average: " + (playerTwoNumberMovesSum / (double) numberGames) + "\n");

        System.out.println("PLAYTIME (MS):");
        System.out.println("Minimum: " + playTimeMillisMin);
        System.out.println("Average: " + (playTimeMillisAvg / (double) numberGames));
        System.out.println("Maximum: " + playTimeMillisMax + "\n");

        System.out.println("PLAYER ONE LEAF NODES SEARCHED:");
        System.out.println("Minimum: " + playerOneLeafNodesSearchedMin);
        System.out.println("Average: " + (playerOneLeafNodesSearchedAvg / ((double) playerOneNumberMovesSum)));
        System.out.println("Maximum: " + playerOneLeafNodesSearchedMax + "\n");

        System.out.println("PLAYER TWO LEAF NODES SEARCHED:");
        System.out.println("Minimum: " + playerTwoLeafNodesSearchedMin);
        System.out.println("Average: " + (playerTwoLeafNodesSearchedAvg / ((double) playerTwoNumberMovesSum)));
        System.out.println("Maximum: " + playerTwoLeafNodesSearchedMax + "\n");

        System.out.println("PLAYER ONE NORMAL NODES SEARCHED:");
        System.out.println("Minimum: " + playerOneMinMaxNodesSearchedMin);
        System.out.println("Average: " + (playerOneMinMaxNodesSearchedAvg / ((double) playerOneNumberMovesSum)));
        System.out.println("Maximum: " + playerOneMinMaxNodesSearchedMax + "\n");

        System.out.println("PLAYER TWO NORMAL NODES SEARCHED:");
        System.out.println("Minimum: " + playerTwoMinMaxNodesSearchedMin);
        System.out.println("Average: " + (playerTwoMinMaxNodesSearchedAvg / ((double) playerTwoNumberMovesSum)));
        System.out.println("Maximum: " + playerTwoMinMaxNodesSearchedMax + "\n");

        System.out.println("PLAYER ONE CHANCE NODES SEARCHED:");
        System.out.println("Minimum: " + playerOneChanceNodesSearchedMin);
        System.out.println("Average: " + (playerOneChanceNodesSearchedAvg / ((double) playerOneNumberMovesSum)));
        System.out.println("Maximum: " + playerOneChanceNodesSearchedMax + "\n");

        System.out.println("PLAYER TWO CHANCE NODES SEARCHED:");
        System.out.println("Minimum: " + playerTwoChanceNodesSearchedMin);
        System.out.println("Average: " + (playerTwoChanceNodesSearchedAvg / ((double) playerTwoNumberMovesSum)));
        System.out.println("Maximum: " + playerTwoChanceNodesSearchedMax + "\n");

        System.out.println("PLAYER ONE TOTAL NODES SEARCHED:");
        System.out.println("Average: " + ((playerOneLeafNodesSearchedAvg + playerOneMinMaxNodesSearchedAvg + playerOneChanceNodesSearchedAvg) / ((double) playerOneNumberMovesSum)) + "\n");

        System.out.println("PLAYER TWO TOTAL NODES SEARCHED:");
        System.out.println("Average: " + ((playerTwoLeafNodesSearchedAvg + playerTwoMinMaxNodesSearchedAvg + playerTwoChanceNodesSearchedAvg) / ((double) playerTwoNumberMovesSum)) + "\n");

        System.out.println("PLAYER ONE DEPTH REACHED:");
        System.out.println("Average: " + (playerOneDepthReachedAvg / ((double) playerOneNumberMovesSum)) + "\n");

        System.out.println("PLAYER TWO DEPTH REACHED:");
        System.out.println("Average: " + (playerTwoDepthReachedAvg / ((double) playerTwoNumberMovesSum)) + "\n");

        System.out.println("PLAYER ONE BEST MOVE DEPTH:");
        System.out.println("Average: " + (playerOneBestMoveDepthAvg / ((double) playerOneNumberMovesSum)) + "\n");

        System.out.println("PLAYER TWO BEST MOVE DEPTH:");
        System.out.println("Average: " + (playerTwoBestMoveDepthAvg / ((double) playerTwoNumberMovesSum)) + "\n");

        System.out.println("PLAYER ONE SEARCH TIME PER MOVE:");
        System.out.println("Minimum: " + playerOneSearchTimeMin);
        System.out.println("Average: " + (playerOneSearchTimeAvg / ((double) playerOneNumberMovesSum)));
        System.out.println("Maximum: " + playerOneSearchTimeMax + "\n");

        System.out.println("PLAYER TWO SEARCH TIME PER MOVE:");
        System.out.println("Minimum: " + playerTwoSearchTimeMin);
        System.out.println("Average: " + (playerTwoSearchTimeAvg / ((double) playerTwoNumberMovesSum)));
        System.out.println("Maximum: " + playerTwoSearchTimeMax + "\n");

        System.out.println("WINS BY FLAG CAPTURE:");
        System.out.println("Player 1: " + playerOneWonFlagCapture);
        System.out.println("Player 2: " + playerTwoWonFlagCapture + "\n");

        System.out.println("WINS BY PIECE CAPTURE");
        System.out.println("Player 1: " + playerOneWonNoMovingPieces);
        System.out.println("Player 2: " + playerTwoWonNoMovingPieces + "\n");

        System.out.println("ties: " + ties);
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

    public static void addResult(int playerWonIndex, boolean wonByFlag) {
        if (playerWonIndex == 0) {
            if (wonByFlag) {
                playerOneWonFlagCapture++;
            } else {
                playerOneWonNoMovingPieces++;
            }
        } else if (playerWonIndex == 1) {
            if (wonByFlag) {
                playerTwoWonFlagCapture++;
            } else {
                playerTwoWonNoMovingPieces++;
            }
        } else {
            ties++;
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

    public static void addMoveStatistics(int playerIndex, int leafNodesSearched, int minMaxNodesSearched, int chanceNodeSearched, long searchTimeNanos, int depthReached, int bestMoveFromDepth) {
        if (playerIndex == 0) {
            playerOneNumberMovesSum++;
            playerOneDepthReachedAvg += depthReached;
            playerOneBestMoveDepthAvg += bestMoveFromDepth;

            // leaf nodes
            if (leafNodesSearched < playerOneLeafNodesSearchedMin) {
                playerOneLeafNodesSearchedMin = leafNodesSearched;
            }
            if (leafNodesSearched > playerOneLeafNodesSearchedMax) {
                playerOneLeafNodesSearchedMax = leafNodesSearched;
            }
            playerOneLeafNodesSearchedAvg += leafNodesSearched;
            // normal intermediate nodes
            if (minMaxNodesSearched < playerOneMinMaxNodesSearchedMin) {
                playerOneMinMaxNodesSearchedMin = leafNodesSearched;
            }
            if (minMaxNodesSearched > playerOneMinMaxNodesSearchedMax) {
                playerOneMinMaxNodesSearchedMax = minMaxNodesSearched;
            }
            playerOneMinMaxNodesSearchedAvg += minMaxNodesSearched;
            // intermediate chance nodes
            if (chanceNodeSearched < playerOneChanceNodesSearchedMin) {
                playerOneChanceNodesSearchedMin = chanceNodeSearched;
            }
            if (chanceNodeSearched > playerOneChanceNodesSearchedMax) {
                playerOneChanceNodesSearchedMax = chanceNodeSearched;
            }
            playerOneChanceNodesSearchedAvg += chanceNodeSearched;
            // search time
            if (searchTimeNanos < playerOneSearchTimeMin) {
                playerOneSearchTimeMin = searchTimeNanos;
            }
            if (searchTimeNanos > playerOneSearchTimeMax) {
                playerOneSearchTimeMax = searchTimeNanos;
            }
            playerOneSearchTimeAvg += searchTimeNanos;
        } if (playerIndex == 1) {
            playerTwoNumberMovesSum++;
            playerTwoDepthReachedAvg += depthReached;
            playerTwoBestMoveDepthAvg += bestMoveFromDepth;

            // leaf nodes
            if (leafNodesSearched < playerTwoLeafNodesSearchedMin) {
                playerTwoLeafNodesSearchedMin = leafNodesSearched;
            }
            if (leafNodesSearched > playerTwoLeafNodesSearchedMax) {
                playerTwoLeafNodesSearchedMax = leafNodesSearched;
            }
            playerTwoLeafNodesSearchedAvg += leafNodesSearched;
            // normal intermediate nodes
            if (minMaxNodesSearched < playerTwoMinMaxNodesSearchedMin) {
                playerTwoMinMaxNodesSearchedMin = leafNodesSearched;
            }
            if (minMaxNodesSearched > playerTwoMinMaxNodesSearchedMax) {
                playerTwoMinMaxNodesSearchedMax = minMaxNodesSearched;
            }
            playerTwoMinMaxNodesSearchedAvg += minMaxNodesSearched;
            // intermediate chance nodes
            if (chanceNodeSearched < playerTwoChanceNodesSearchedMin) {
                playerTwoChanceNodesSearchedMin = chanceNodeSearched;
            }
            if (chanceNodeSearched > playerTwoChanceNodesSearchedMax) {
                playerTwoChanceNodesSearchedMax = chanceNodeSearched;
            }
            playerTwoChanceNodesSearchedAvg += chanceNodeSearched;
            // search time
            if (searchTimeNanos < playerTwoSearchTimeMin) {
                playerTwoSearchTimeMin = searchTimeNanos;
            }
            if (searchTimeNanos > playerTwoSearchTimeMax) {
                playerTwoSearchTimeMax = searchTimeNanos;
            }
            playerTwoSearchTimeAvg += searchTimeNanos;
        }
    }

    public static void addNodesSearched(int nodesSearched) {
        if (nodesSearched < playerTwoLeafNodesSearchedMin) {
            playerTwoLeafNodesSearchedMin = nodesSearched;
        }
        if (nodesSearched > playerTwoLeafNodesSearchedMax) {
            playerTwoLeafNodesSearchedMax = nodesSearched;
        }
        playerTwoLeafNodesSearchedAvg += nodesSearched;
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

    public static void printStats() {
        System.out.println("\n----------------------------------------------------------");
        System.out.println("RESULTS (for " + counter + " games):");
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
        System.out.println("Minimum: " + playerTwoLeafNodesSearchedMin);
        System.out.println("Average: " + (playerTwoLeafNodesSearchedAvg / (double) numberGames));
        System.out.println("Maximum: " + playerTwoLeafNodesSearchedMax + "\n");

        System.out.println("PLAYER ONE SEARCH TIME:");
        System.out.println("Minimum: " + playerOneSearchTimeMin);
        System.out.println("Average: " + (playerOneSearchTimeAvg / (double) numberMovesAvg));
        System.out.println("Maximum: " + playerOneSearchTimeMax + "\n");

        System.out.println("PLAYER TWO SEARCH TIME:");
        System.out.println("Minimum: " + playerTwoSearchTimeMin);
        System.out.println("Average: " + (playerTwoSearchTimeAvg / (double) numberMovesAvg));
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
