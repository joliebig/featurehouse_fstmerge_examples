package net.sourceforge.pmd.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class TypeMap {

    private Map<String, Class<?>> typesByName;

    
    public TypeMap(int initialSize) {
        typesByName = new HashMap<String, Class<?>>(initialSize);
    }

    
    public TypeMap(Class<?>... types) {
        this(types.length);
        add(types);
    }

    
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public void add(Class<?> type) {
        final String shortName = ClassUtil.withoutPackageName(type.getName());
        Class<?> existingType = typesByName.get(shortName);
        if (existingType == null) {
            typesByName.put(type.getName(), type);
            typesByName.put(shortName, type);
            return;
        }

        if (existingType != type) {
            throw new IllegalArgumentException("Short name collision between existing " + existingType + " and new "
                    + type);
        }
    }

    
    public boolean contains(Class<?> type) {
        return typesByName.containsValue(type);
    }

    
    public boolean contains(String typeName) {
        return typesByName.containsKey(typeName);
    }

    
    public Class<?> typeFor(String typeName) {
        return typesByName.get(typeName);
    }

    
    public void add(Class<?>... types) {
        for (Class<?> element : types) {
            add(element);
        }
    }

    
    public Map<Class<?>, String> asInverseWithShortName() {
        Map<Class<?>, String> inverseMap = new HashMap<Class<?>, String>(typesByName.size() / 2);

        Iterator iter = typesByName.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            storeShortest(inverseMap, entry.getValue(), (String) entry.getKey());
        }

        return inverseMap;
    }

    
    public int size() {
        return typesByName.size();
    }

    
    private void storeShortest(Map map, Object key, String value) {
        String existingValue = (String) map.get(key);

        if (existingValue == null) {
            map.put(key, value);
            return;
        }

        if (existingValue.length() < value.length()) {
            return;
        }

        map.put(key, value);
    }
}
