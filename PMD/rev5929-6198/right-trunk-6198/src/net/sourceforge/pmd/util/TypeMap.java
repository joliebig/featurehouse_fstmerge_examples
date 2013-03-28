package net.sourceforge.pmd.util;

import java.util.HashMap;
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

    
    public void add(Class<?> type) {
	typesByName.put(type.getName(), type);
	typesByName.put(ClassUtil.withoutPackageName(type.getName()), type);
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
}
