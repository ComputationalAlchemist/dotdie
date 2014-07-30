package skaianet.die.standalone;

import skaianet.die.back.EmptyExtension;
import skaianet.die.front.ColoredIdentifier;

import java.lang.reflect.Array;

public class StandaloneExtension extends EmptyExtension {
    @Override
    public Object calcImport(ColoredIdentifier namespace, ColoredIdentifier name) {
        switch (namespace.name) {
            case "universe":
                return new Universe(name);
            case "author":
                return new Author(name);
            case "stream":
                switch (name.name) {
                    case "stdin":
                        return new StreamIn(Standalone.standaloneInput, name.name);
                    case "stdout":
                        return new StreamOut(Standalone.standaloneOutput, name.name);
                    case "stderr":
                        return new StreamOut(System.err, name.name);
                    default:
                        throw new IllegalArgumentException("No such stream " + name);
                }
            case "turingtape":
                return new TuringTape();
            case "turingtapehead":
                return new TuringTapeHead();
            case "bit":
                return new Bit();
            default:
                return super.calcImport(namespace, name);
        }
    }

    @Override
    public Object fieldRef(Object object, ColoredIdentifier field) {
        if (field.name.equals("length")) {
            if (object instanceof String) {
                return ((String) object).length();
            } else if (object != null && object.getClass().isArray()) {
                return Array.getLength(object);
            }
        }
        return super.fieldRef(object, field);
    }

    @Override
    public Object arrayRef(Object array, Object index) {
        if (index instanceof Integer) {
            if (array instanceof String) {
                return (int) ((String) array).charAt((Integer) index);
            } else if (array != null && array.getClass().isArray()) {
                return Array.get(array, (Integer) index);
            }
        }
        return super.arrayRef(array, index);
    }
}
