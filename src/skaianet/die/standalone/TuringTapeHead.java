package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

/**
 * Created on 2014-07-26.
 */
public class TuringTapeHead {
    private TuringTape inserted;
    private int index = 0;

    @ATHcessible
    public void insert(TuringTape t) {
        if (inserted != null) {
            throw new IllegalStateException("Already contains a tape!");
        }
        inserted = t;
    }

    @ATHcessible
    public TuringTape eject() {
        TuringTape out = inserted;
        inserted = null;
        return out.master;
    }

    @ATHcessible
    public int read() {
        return inserted.getLocal(index);
    }

    @ATHcessible
    public void erase() {
        inserted.eraseLocal(index);
    }

    @ATHcessible
    public void write(int value) {
        inserted.writeLocal(index, (byte) value);
    }

    @ATHcessible
    public void play(int relative) {
        index += relative;
        normalizePosition();
    }

    @ATHcessible
    public void rewind(int relative) {
        index -= relative;
        normalizePosition();
    }

    private void normalizePosition() {
        while (index < 0) {
            inserted = inserted.previous();
            index += inserted.local.length;
        }
        while (index >= inserted.local.length) {
            index -= inserted.local.length;
            inserted = inserted.next();
        }
    }
}
