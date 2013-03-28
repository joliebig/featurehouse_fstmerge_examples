package net.sourceforge.pmd.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class CollectionUtil {

	public static final TypeMap collectionInterfacesByNames = new TypeMap( new Class[] {
		java.util.List.class,
		java.util.Collection.class,
		java.util.Map.class,
		java.util.Set.class,
		});
		
	public static final TypeMap collectionClassesByNames = new TypeMap( new Class[] {
		java.util.ArrayList.class,
		java.util.LinkedList.class,
		java.util.Vector.class,
		java.util.HashMap.class,
		java.util.LinkedHashMap.class,
		java.util.TreeMap.class,
		java.util.TreeSet.class,
		java.util.HashSet.class,
		java.util.LinkedHashSet.class
		});
	
	private CollectionUtil() {};
	
	
	public static Class getCollectionTypeFor(String shortName) {
		Class cls = collectionClassesByNames.typeFor(shortName);
		if (cls != null) return cls;
		
		return collectionInterfacesByNames.typeFor(shortName);
	}
	
	
	public static boolean isCollectionType(String typeName, boolean includeInterfaces) {
		
		if (collectionClassesByNames.contains(typeName)) return true;

		return includeInterfaces && collectionInterfacesByNames.contains(typeName);
	}
	
    
    public static boolean isCollectionType(Class clazzType, boolean includeInterfaces) {

        if (collectionClassesByNames.contains(clazzType)) {
            return true;
        }

        return includeInterfaces && collectionInterfacesByNames.contains(clazzType);
    }

    
    public static <T> Set<T> asSet(T[] items) {
    	
    	Set<T> set = new HashSet<T>(items.length);
    	for (int i=0; i<items.length; i++) {
    		set.add(items[i]);
    	}
    	return set;
    }	
    
	
	public static <K, V> Map<K, V> mapFrom(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new RuntimeException("mapFrom keys and values arrays have different sizes");
        }
		Map<K, V> map = new HashMap<K, V>(keys.length);
		for (int i=0; i<keys.length; i++) {
			map.put(keys[i], values[i]);
		}
		return map;
	}
	
	
	public static <K, V> Map<V, K> invertedMapFrom(Map<K, V> source) {
		Map<V, K> map = new HashMap<V, K>(source.size());
        for (Map.Entry<K, V> entry: source.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
		return map;
	}
	
	
	public static final boolean arraysAreEqual(Object value, Object otherValue) {
		if (value instanceof Object[]) {
			if (otherValue instanceof Object[]) return valuesAreTransitivelyEqual((Object[])value, (Object[])otherValue);
			return false;
		}
		return false;
	}
	
	
	public static final boolean valuesAreTransitivelyEqual(Object[] thisArray, Object[] thatArray) {
		if (thisArray == thatArray) return true;
		if ((thisArray == null) || (thatArray == null)) return false;
		if (thisArray.length != thatArray.length) return false;
		for (int i = 0; i < thisArray.length; i++) {
			if (!areEqual(thisArray[i], thatArray[i])) return false;	
		}
		return true;
	}

	
	public static final boolean areEqual(Object value, Object otherValue) {
		if (value == otherValue) return true;
		if (value == null) return false;
		if (otherValue == null) return false;

		if (value.getClass().getComponentType() != null) return arraysAreEqual(value, otherValue);
		return value.equals(otherValue);
	}
}
