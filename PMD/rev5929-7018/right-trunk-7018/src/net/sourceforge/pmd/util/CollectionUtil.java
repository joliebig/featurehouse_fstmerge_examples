package net.sourceforge.pmd.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public final class CollectionUtil {

    @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
    public static final TypeMap COLLECTION_INTERFACES_BY_NAMES = new TypeMap(new Class[] { java.util.List.class,
	    java.util.Collection.class, java.util.Map.class, java.util.Set.class, });

    @SuppressWarnings({"PMD.LooseCoupling", "PMD.UnnecessaryFullyQualifiedName"})
    public static final TypeMap COLLECTION_CLASSES_BY_NAMES = new TypeMap(new Class[] { java.util.ArrayList.class,
	    java.util.LinkedList.class, java.util.Vector.class, java.util.HashMap.class, java.util.LinkedHashMap.class,
	    java.util.TreeMap.class, java.util.TreeSet.class, java.util.HashSet.class, java.util.LinkedHashSet.class });

    private CollectionUtil() {
    };

    
    public static Class<?> getCollectionTypeFor(String shortName) {
	Class<?> cls = COLLECTION_CLASSES_BY_NAMES.typeFor(shortName);
	if (cls != null) {
	    return cls;
	}

	return COLLECTION_INTERFACES_BY_NAMES.typeFor(shortName);
    }

    
    public static boolean isCollectionType(String typeName, boolean includeInterfaces) {

	if (COLLECTION_CLASSES_BY_NAMES.contains(typeName)) {
	    return true;
	}

	return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(typeName);
    }

    
    public static boolean isCollectionType(Class<?> clazzType, boolean includeInterfaces) {

	if (COLLECTION_CLASSES_BY_NAMES.contains(clazzType)) {
	    return true;
	}

	return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(clazzType);
    }

    
    public static <T> Set<T> asSet(T[] items) {

	return new HashSet<T>(Arrays.asList(items));
    }

    
    public static <K, V> Map<K, V> mapFrom(K[] keys, V[] values) {
	if (keys.length != values.length) {
	    throw new RuntimeException("mapFrom keys and values arrays have different sizes");
	}
	Map<K, V> map = new HashMap<K, V>(keys.length);
	for (int i = 0; i < keys.length; i++) {
	    map.put(keys[i], values[i]);
	}
	return map;
    }

    
    public static <K, V> Map<V, K> invertedMapFrom(Map<K, V> source) {
	Map<V, K> map = new HashMap<V, K>(source.size());
	for (Map.Entry<K, V> entry : source.entrySet()) {
	    map.put(entry.getValue(), entry.getKey());
	}
	return map;
    }

    
    public static boolean arraysAreEqual(Object value, Object otherValue) {
	if (value instanceof Object[]) {
	    if (otherValue instanceof Object[]) {
		return valuesAreTransitivelyEqual((Object[]) value, (Object[]) otherValue);
	    }
	    return false;
	}
	return false;
    }

    
    public static boolean valuesAreTransitivelyEqual(Object[] thisArray, Object[] thatArray) {
	if (thisArray == thatArray) {
	    return true;
	}
	if (thisArray == null || thatArray == null) {
	    return false;
	}
	if (thisArray.length != thatArray.length) {
	    return false;
	}
	for (int i = 0; i < thisArray.length; i++) {
	    if (!areEqual(thisArray[i], thatArray[i])) {
		return false; 
	    }
	}
	return true;
    }

    
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static boolean areEqual(Object value, Object otherValue) {
    	if (value == otherValue) {
    	    return true;
    	}
    	if (value == null) {
    	    return false;
    	}
    	if (otherValue == null) {
    	    return false;
    	}

    	if (value.getClass().getComponentType() != null) {
    	    return arraysAreEqual(value, otherValue);
    	    }
	    return value.equals(otherValue);
    }

    
    public static boolean isEmpty(Object[] items) {
        return items == null || items.length == 0;
    }

    
    public static <T> boolean areSemanticEquals(T[] a, T[] b) {

        if (a == null) { return isEmpty(b); }
        if (b == null) { return isEmpty(a); }
        return a.equals(b);
    }

    
    public static <T> T[] addWithoutDuplicates(T[] values, T newValue) {

        for (T value : values) {
            if (value.equals(newValue)) {
                return values;
            }
        }

        T[] largerOne = (T[])Array.newInstance(values.getClass().getComponentType(), values.length + 1);
        System.arraycopy(values, 0, largerOne, 0, values.length);
        largerOne[values.length] = newValue;
        return largerOne;
    }

    
    public static <T> T[] addWithoutDuplicates(T[] values, T[] newValues) {

        Set<T> originals = new HashSet<T>(values.length);
        for (T value : values) { originals.add(value); }
        List<T> newOnes = new ArrayList<T>(newValues.length);
        for (T value : newValues) {
            if (originals.contains(value)) { continue; }
            newOnes.add(value);
        }

        T[] largerOne = (T[])Array.newInstance(values.getClass().getComponentType(), values.length + newOnes.size());
        System.arraycopy(values, 0, largerOne, 0, values.length);
        for (int i=values.length; i<largerOne.length; i++) { largerOne[i] = newOnes.get(i-values.length); }
        return largerOne;
    }
}
