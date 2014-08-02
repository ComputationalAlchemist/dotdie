package skaianet.die.standalone;

import skaianet.die.back.EmptyExtension;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.ColoredIdentifier;
import skaianet.die.gui.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
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
            case "window":
                return new Window(name.name);
            case "label":
                return new Label(name.name);
            case "panel":
                return new Panel();
            case "icon":
                return new Icon();
            case "button":
                return new Button(name.name);
            case "signalqueue":
                return new SignalQueue();
            case "dream":
                final int length = name.name.length();
                return new Dream(length);
            case "object":
                return new GenericObject();
            case "color":
                return namespace.color;
            case "uncolor":
                return new Uncolor(namespace.color);
            case "entropy":
                return new Entropy();
            case "dictionary":
                return new Dictionary();
            case "png":
                try {
                    if (Standalone.resourceDir == null) {
                        throw new RuntimeException("No such " + namespace.name + " " + name.name);
                    } else {
                        return ImageIO.read(new File(Standalone.resourceDir, name.name + "." + namespace.name));
                    }
                } catch (IOException e) {
                    throw new RuntimeException("No such " + namespace.name + " " + name.name, e);
                }
            default:
                return super.calcImport(namespace, name);
        }
    }

    @Override
    public Object fieldRef(Object object, ColoredIdentifier field, ExecutionContext context) {
        if (field.name.equals("length")) {
            if (object instanceof String) {
                return ((String) object).length();
            } else if (object != null && object.getClass().isArray()) {
                return Array.getLength(object);
            }
        }
        if (object instanceof Object[]) {
            Object[] out = new Object[((Object[]) object).length];
            for (int i = 0; i < out.length; i++) {
                out[i] = context.fieldRef(((Object[]) object)[i], field);
            }
            return out;
        }
        if (object instanceof GenericObject) {
            return ((GenericObject) object).fields.get(field);
        }
        return super.fieldRef(object, field, context);
    }

    @Override
    public void fieldPut(Object object, ColoredIdentifier field, ExecutionContext context, Object value) {
        if (object instanceof GenericObject) {
            ((GenericObject) object).fields.put(field, value);
            return;
        }
        super.fieldPut(object, field, context, value);
    }

    @Override
    public Object invoke(Object procedure, ExecutionContext context, Object... arguments) {
        if (procedure instanceof Object[]) {
            Object[] out = new Object[((Object[]) procedure).length];
            for (int i = 0; i < out.length; i++) {
                out[i] = context.invoke(((Object[]) procedure)[i], arguments);
            }
            return out;
        }
        return super.invoke(procedure, context, arguments);
    }

    @Override
    public Object arrayRef(Object array, Object index) {
        if (index instanceof Integer) {
            if (array instanceof String) {
                return (int) ((String) array).charAt((Integer) index);
            }
        }
        if (array instanceof Dictionary) {
            return ((Dictionary) array).get(index);
        }
        return super.arrayRef(array, index);
    }

    @Override
    public void arrayPut(Object array, Object index, Object value) {
        if (array instanceof Dictionary) {
            ((Dictionary) array).put(index, value);
            return;
        }
        super.arrayPut(array, index, value);
    }
}
