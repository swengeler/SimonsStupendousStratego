package project.stratego.ai.tests;

import project.stratego.control.managers.AIComManager;
import project.stratego.control.managers.ModelComManager;

public class AITestsMain {

    public static void main(String[] args) {
        ModelComManager.getInstance().configureAIMatch();
        AIComManager.getInstance().start();
    }

}
