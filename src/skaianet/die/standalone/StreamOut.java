package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

import java.io.PrintStream;

public class StreamOut {
    private final PrintStream output;
    private final String name;

    public StreamOut(PrintStream output, String name) {
        this.output = output;
        this.name = name;
    }

    @ATHcessible
    public void write(int c) {
        output.write(c);
    }

    @ATHcessible
    public void print(Object o) {
        output.print(o);
    }

    @ATHcessible
    public void println(Object o) {
        output.println(o);
    }

    @ATHcessible
    public void println() {
        output.println();
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
