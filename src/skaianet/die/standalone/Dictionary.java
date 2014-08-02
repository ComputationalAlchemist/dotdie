package skaianet.die.standalone;

import skaianet.die.back.ATHcessible;

import java.util.HashMap;

public class Dictionary {
    private final HashMap<Object, Object> map = new HashMap<>();

    @ATHcessible
    public Object get(Object o) {
        return map.get(o);
    }

    @ATHcessible
    public boolean has(Object o) {
        return map.containsKey(o);
    }

    @ATHcessible
    public void put(Object o, Object v) {
        map.put(o, v);
    }
}
