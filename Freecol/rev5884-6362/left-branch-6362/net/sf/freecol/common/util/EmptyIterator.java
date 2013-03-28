

package net.sf.freecol.common.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class EmptyIterator<T> implements Iterator<T> {




    
    public static <T> EmptyIterator<T> getInstance() {
        return new EmptyIterator<T>();
    }

    

    private EmptyIterator() {
    }

    

    public boolean hasNext() {
        return false;
    }

    public T next() {
        throw new NoSuchElementException("Programming error: next() should never be called on the EmptyIterator");
    }

    public void remove() {
        throw new IllegalStateException();
    }

}
