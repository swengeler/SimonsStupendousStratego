package project.stratego.ui.utils;

import java.io.*;

public class FileLoader {

    public static String load(File file) {
        if (file == null) {
            return null;
        }
        String encoding = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                encoding += temp + "\n";
            }
            System.out.println("encoding:\n" + encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoding;
    }

}
