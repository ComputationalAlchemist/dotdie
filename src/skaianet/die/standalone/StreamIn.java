package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created on 2014-07-26.
 */
class StreamIn {
    private final Scanner in;
    private final String name;

    public StreamIn(InputStream in, String name) {
        this.name = name;
        this.in = new Scanner(in);
    }

    @ATHcessible
    public String readLine() {
        return this.in.nextLine();
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
