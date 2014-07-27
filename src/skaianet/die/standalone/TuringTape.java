package skaianet.die.standalone;

import java.util.Arrays;

/**
 * Created on 2014-07-26.
 */
public class TuringTape {
    public final byte[] local;
    public final TuringTape master;
    private TuringTape previous;
    private TuringTape next;

    public TuringTape() {
        this.local = new byte[16];
        master = this;
    }

    public TuringTape(TuringTape previous, int length, TuringTape next, TuringTape master) {
        this.previous = previous;
        this.next = next;
        this.master = master;
        this.local = new byte[length];
    }

    public TuringTape previous() {
        if (previous == null) {
            previous = new TuringTape(null, local.length * 2, this, master);
        }
        return previous;
    }

    public TuringTape next() {
        if (next == null) {
            next = new TuringTape(this, local.length * 2, null, master);
        }
        return next;
    }

    public byte getLocal(int index) {
        return local[index];
    }

    public void eraseLocal(int index) {
        local[index] = 0;
    }

    public void writeLocal(int index, byte value) {
        local[index] |= value;
    }

    public String toString() {
        return toString(true, true);
    }

    private String toString(boolean goBack, boolean goForward) {
        return (goBack && previous != null ? previous.toString(true, false) : "") + Arrays.toString(local) + (goForward && next != null ? next.toString(false, true) : "");
    }
}
