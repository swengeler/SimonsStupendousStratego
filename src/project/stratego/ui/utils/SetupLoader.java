package project.stratego.ui.utils;

import java.io.*;

public class SetupLoader {

    public static String load(File file) {
        String setupEncoding = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            setupEncoding = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return setupEncoding;
    }

}
