package skaianet.die.standalone;

import skaianet.die.back.ATHAlive;
import skaianet.die.back.ATHcessible;
import skaianet.die.back.EmptyExtension;
import skaianet.die.back.ExecutionContext;
import skaianet.die.front.ColoredIdentifier;
import skaianet.die.gui.Label;
import skaianet.die.gui.Panel;
import skaianet.die.gui.Window;

import java.lang.reflect.Array;
import java.util.concurrent.atomic.AtomicInteger;

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
            case "dream":
                final int length = name.name.length();
                return new ATHAlive() {
                    private final AtomicInteger ctr = new AtomicInteger();

                    @ATHcessible
                    public void sleep() throws InterruptedException {
                        ctr.incrementAndGet();
                        try {
                            Thread.sleep(length);
                        } finally {
                            ctr.decrementAndGet();
                        }
                    }

                    @Override
                    public boolean isAlive() {
                        return ctr.get() > 0;
                    }

                    @Override
                    public double getEnergy() {
                        return 0;
                    }
                };
            case "color":
                return namespace.color;
            case "uncolor":
                return new Uncolor(namespace.color);
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
        } else if (object instanceof Object[]) {
            Object[] out = new Object[((Object[]) object).length];
            for (int i = 0; i < out.length; i++) {
                out[i] = context.fieldRef(((Object[]) object)[i], field);
            }
            return out;
        }
        return super.fieldRef(object, field, context);
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
            } else if (array != null && array.getClass().isArray()) {
                return Array.get(array, (Integer) index);
            }
        }
        return super.arrayRef(array, index);
    }
}
