package net.sourceforge.pmd.util.filter;


public interface Filter<T> {
	boolean filter(T obj);
}
