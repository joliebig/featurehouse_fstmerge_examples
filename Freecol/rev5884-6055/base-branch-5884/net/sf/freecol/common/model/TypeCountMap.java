

package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class TypeCountMap<T extends FreeColGameObjectType> {

    private Map<T, Integer> values = new HashMap<T, Integer>();

    public Map<T, Integer> getValues() {
        return values;
    }

    public int getCount(T key) {
        Integer value = values.get(key);
        return value == null ? 0 : value.intValue();
    }

    public Integer incrementCount(T key, int newCount) {
        Integer oldValue = values.get(key);
        if (oldValue == null) {
            return values.put(key, new Integer(newCount));
        } else if (oldValue == -newCount) {
            values.remove(key);
            return null;
        } else {
            return values.put(key, oldValue + newCount);
        }
    }

    public void clear() {
        values.clear();
    }

    public Set<T> keySet() {
        return values.keySet();
    }

    public Collection<Integer> values() {
        return values.values();
    }

    public boolean containsKey(T key) {
        return values.containsKey(key);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public int size() {
        return values.size();
    }

}