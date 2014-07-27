package skaianet.die.standalone;

import skaianet.die.back.EmptyExtension;

import java.lang.reflect.Array;

/**
 * Created on 2014-07-26.
 */
public class StandaloneExtension extends EmptyExtension {
    @Override
    public Object calcImport(String namespace, String name) {
        if ("universe".equals(namespace)) {
            return new Universe(name);
        } else if ("author".equals(namespace)) {
            return new Author(name);
        } else if ("stream".equals(namespace)) {
            if ("stdin".equals(name)) {
                return new StreamIn(System.in, name);
            } else if ("stdout".equals(name)) {
                return new StreamOut(System.out, name);
            } else if ("stderr".equals(name)) {
                return new StreamOut(System.err, name);
            }
            throw new IllegalArgumentException("No such stream " + name);
        } else if ("turingtape".equals(namespace)) {
            return new TuringTape();
        } else if ("turingtapehead".equals(namespace)) {
            return new TuringTapeHead();
        } else if ("bit".equals(namespace)) {
            return new Bit();
        }
        return super.calcImport(namespace, name);
    }

    @Override
    public Object fieldRef(Object object, String field) {
        if (object instanceof String && field.equals("length")) {
            return ((String) object).length();
        } else if (object != null && object.getClass().isArray()) {
            return Array.getLength(object);
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
