

package org.jfree.chart.util;

import java.awt.Stroke;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class StrokeList extends AbstractObjectList {

    
    public StrokeList() {
        super();
    }

    
    public Stroke getStroke(int index) {
        return (Stroke) get(index);
    }

    
    public void setStroke(int index, Stroke stroke) {
        set(index, stroke);
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        if (obj instanceof StrokeList) {
            return super.equals(obj);
        }

        return false;

    }

    
    public int hashCode() {
        return super.hashCode();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {

        stream.defaultWriteObject();
        int count = size();
        stream.writeInt(count);
        for (int i = 0; i < count; i++) {
            Stroke stroke = getStroke(i);
            if (stroke != null) {
                stream.writeInt(i);
                SerialUtilities.writeStroke(stroke, stream);
            }
            else {
                stream.writeInt(-1);
            }
        }

    }

    
    private void readObject(ObjectInputStream stream) throws IOException,
            ClassNotFoundException {

        stream.defaultReadObject();
        int count = stream.readInt();
        for (int i = 0; i < count; i++) {
            int index = stream.readInt();
            if (index != -1) {
                setStroke(index, SerialUtilities.readStroke(stream));
            }
        }

    }

}

