import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created on 2014-07-27.
 */
public class LineCount {
    private int totalLines, totalChars;

    public static void main(String[] args) throws IOException {
        LineCount java = new LineCount();
        for (File f : new File("src/skaianet/die").listFiles()) {
            LineCount module = new LineCount();
            module.walk(f, ".java");
            module.collect("DotDie " + f.getName());
            java.add(module);
        }
        java.collect("All DotDie");
        LineCount ath = new LineCount();
        ath.walk(new File("src"), ".~ath");
        ath.collect("~ATH examples");
    }

    public static String repeat(char c, int count) {
        if (count <= 0) {
            return "";
        }
        char[] out = new char[count];
        Arrays.fill(out, c);
        return new String(out);
    }

    public void add(LineCount module) {
        totalLines += module.totalLines;
        totalChars += module.totalChars;
    }

    public void collect(String module) {
        System.out.println("\tTotal for " + module + ":" + repeat(' ', 19 - module.length()) + "\t" + totalLines + " lines\t" + totalChars + " chars\t" + (10 * totalChars / totalLines) / 10.0 + " density.");
    }

    public void walk(File file, String suffix) throws IOException {
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                walk(f, suffix);
            }
        } else if (file.getName().endsWith(suffix)) {
            try (FileReader fileReader = new FileReader(file)) {
                char[] buf = new char[1024];
                boolean wasNewline = false;
                int lineCount = 0, charCount = 0;
                while (true) {
                    int count = fileReader.read(buf);
                    if (count == -1) {
                        break;
                    }
                    for (int i = 0; i < count; i++) {
                        if (buf[i] == '\n') {
                            lineCount++;
                        }
                    }
                    charCount += count;
                    wasNewline = buf[count - 1] == '\n';
                }
                if (!wasNewline) {
                    lineCount++; // Include final line.
                }
                //System.out.println("Total for " + file.getName() + ": " + lineCount);
                totalLines += lineCount;
                totalChars += charCount;
            }
        }
    }
}
