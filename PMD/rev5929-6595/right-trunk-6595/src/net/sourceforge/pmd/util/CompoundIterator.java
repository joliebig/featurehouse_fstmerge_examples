package net.sourceforge.pmd.util;

import java.util.Iterator;
import java.util.NoSuchElementException;


public class CompoundIterator<T> implements Iterator<T> {
    private final Iterator<T>[] iterators;
    private int index;

    
    public CompoundIterator(Iterator<T>... iterators) {
	this.iterators = iterators;
	this.index = 0;
    }

    
    public boolean hasNext() {
	return getNextIterator() != null;
    }

    
    public T next() {
	Iterator<T> iterator = getNextIterator();
	if (iterator != null) {
	    return iterator.next();
	} else {
	    throw new NoSuchElementException();
	}
    }

    
    public void remove() {
	Iterator<T> iterator = getNextIterator();
	if (iterator != null) {
	    iterator.remove();
	} else {
	    throw new IllegalStateException();
	}
    }

    
    private Iterator<T> getNextIterator() {
	while (index < iterators.length) {
	    if (iterators[index].hasNext()) {
		return iterators[index];
	    } else {
		index++;
	    }
	}
	return null;
    }
}
