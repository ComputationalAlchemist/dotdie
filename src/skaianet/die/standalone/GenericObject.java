package skaianet.die.standalone;

import skaianet.die.front.ColoredIdentifier;

import java.util.HashMap;

public class GenericObject {
    public final HashMap<ColoredIdentifier, Object> fields = new HashMap<>();

    @Override
    public String toString() {
        return "OBJ" + fields;
    }
}
