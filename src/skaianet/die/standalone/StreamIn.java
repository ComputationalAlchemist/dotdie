package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamIn {
    private final BufferedReader in;
    private final String name;

    public StreamIn(InputStream in, String name) {
        this.name = name;
        this.in = new BufferedReader(new InputStreamReader(in));
    }

    @ATHcessible
    public String readLine() throws IOException {
        return this.in.readLine();
    }

    @Override
    public String toString() {
        return "<" + name + ">";
    }
}
