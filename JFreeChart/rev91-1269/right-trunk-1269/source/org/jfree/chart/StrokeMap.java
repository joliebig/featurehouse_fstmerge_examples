

package org.jfree.chart;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.SerialUtilities;


public class StrokeMap implements Cloneable, Serializable {

    
    private transient Map store;

    
    public StrokeMap() {
        this.store = new TreeMap();
    }

    
    public Stroke getStroke(Comparable key) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        return (Stroke) this.store.get(key);
    }

    
    public boolean containsKey(Comparable key) {
        return this.store.containsKey(key);
    }

    
    public void put(Comparable key, Stroke stroke) {
        if (key == null) {
            throw new IllegalArgumentException("Null 'key' argument.");
        }
        this.store.put(key, stroke);
    }

    
    public void clear() {
        this.store.clear();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StrokeMap)) {
            return false;
        }
        StrokeMap that = (StrokeMap) obj;
        if (this.store.size() != that.store.size()) {
            return false;
        }
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            Stroke s1 = getStroke(key);
            Stroke s2 = that.getStroke(key);
            if (!ObjectUtilities.equal(s1, s2)) {
                return false;
            }
        }
        return true;
    }

    
    public Object clone() throws CloneNotSupportedException {
        
        
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        stream.writeInt(this.store.size());
        Set keys = this.store.keySet();
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Comparable key = (Comparable) iterator.next();
            stream.writeObject(key);
            Stroke stroke = getStroke(key);
            SerialUtilities.writeStroke(stroke, stream);
        }
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.store = new TreeMap();
        int keyCount = stream.readInt();
        for (int i = 0; i < keyCount; i++) {
            Comparable key = (Comparable) stream.readObject();
            Stroke stroke = SerialUtilities.readStroke(stream);
            this.store.put(key, stroke);
        }
    }

}
