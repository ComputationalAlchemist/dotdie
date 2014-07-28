package skaianet.die.standalone;

import skaianet.die.front.ParsingException;
import skaianet.die.middle.CompilingException;

import java.io.*;
import java.util.Scanner;

/**
 * Created on 2014-07-27.
 */
public class Tests {
    public static void main(String[] args) throws ParsingException, CompilingException, IOException {
        for (File test : new File("src").listFiles()) {
            if (test.getName().toLowerCase().endsWith(".~ath")) {
                File inputFile = new File(test.getPath() + ".test.input");
                try (InputStream testInputStream = inputFile.exists() ? new FileInputStream(inputFile) : new NullInputStream()) {
                    File testFile = new File(test.getPath() + ".test");
                    if (!testFile.exists()) {
                        System.out.print("Cannot find test file: " + testFile + " - would you like to create a log now? (y/n) ");
                        if (new Scanner(System.in).nextLine().toLowerCase().startsWith("y")) {
                            System.out.println("Creating.");
                            try (PrintStream outputStream = new PrintStream(new FileOutputStream(testFile))) {
                                Standalone.execute(test.getPath(), false, outputStream, testInputStream);
                            }
                            continue;
                        } else {
                            System.out.println("Okay. This will probably fail.");
                        }
                    }
                    try (PrintStream testOutputStream = new PrintStream(new TestStream(new FileInputStream(testFile)))) {
                        System.out.print("Testing \"" + test + "\"...");
                        Standalone.execute(test.getPath(), false, testOutputStream, testInputStream);
                        System.out.println(" [PASSED]");
                    }
                }
            }
        }
        System.out.println("Completed successfully!");
    }

    private static class TestStream extends OutputStream {
        private final BufferedInputStream stream;

        public TestStream(InputStream stream) {
            this.stream = new BufferedInputStream(stream);
        }

        @Override
        public void write(int i) throws IOException {
            int next = stream.read();
            if (next != i) {
                throw new RuntimeException("Unexpected character: " + ((char) i) + " instead of " + ((char) next) + ".");
            }
        }

        public void close() throws IOException {
            int next = stream.read();
            if (next != -1) {
                throw new RuntimeException("Premature EOF instead of " + ((char) next) + ".");
            }
            stream.close();
        }
    }

    private static class NullInputStream extends InputStream {
        @Override
        public int read() throws IOException {
            return -1;
        }
    }
}
