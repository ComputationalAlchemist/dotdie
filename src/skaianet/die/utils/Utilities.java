package skaianet.die.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Created on 2014-07-27.
 */
public class Utilities {
    public static String readEntireFile(Reader reader) throws IOException {
        BufferedReader byline = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
        StringBuilder code = new StringBuilder(1024);
        while (true) {
            String line = byline.readLine();
            if (line == null) {
                break;
            }
            code.append(line).append('\n');
        }
        return code.toString();
    }

    public static String readEntireFile(String filename) throws IOException {
        try (FileReader reader = new FileReader(filename)) {
            return readEntireFile(reader);
        }
    }

    public static int countCharacters(String code, char c, int start, int end) {
        int count = 0;
        for (int i = start; i < end; i++) {
            if (code.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
