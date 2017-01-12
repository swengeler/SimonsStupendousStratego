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
            encoding = encoding.substring(0, encoding.length() - 1);
            //System.out.println("encoding:\n" + encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return encoding;
    }

}
