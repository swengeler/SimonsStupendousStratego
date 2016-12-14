package project.stratego.ai.tests;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;

public class AITestsMain {

    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            System.out.println("\n|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            System.out.println("RUN NUMBER " + (i + 1));
            System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||\n");
            ModelComManager.getInstance().configureAIMatch();
            AIComManager.getInstance().runAIMatch();
        }
    }

}
