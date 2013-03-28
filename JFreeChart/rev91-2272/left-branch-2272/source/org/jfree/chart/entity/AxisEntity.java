

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.HashUtilities;
import org.jfree.chart.axis.Axis;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;


public class AxisEntity extends ChartEntity {

    
    private static final long serialVersionUID = -4445994133561919083L;
                  

    
    private Axis axis;

    
    public AxisEntity(Shape area, Axis axis) {
        
        this(area, axis, null);
    }

    
    public AxisEntity(Shape area, Axis axis, String toolTipText) {
        
        this(area, axis, toolTipText, null);
    }

    
    public AxisEntity(Shape area, Axis axis, String toolTipText,
            String urlText) {
        super(area, toolTipText, urlText);
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }

        this.axis = axis;
    }

    
    public Axis getAxis() {
        return this.axis;
    }

    
    public String toString() {
        StringBuffer buf = new StringBuffer("AxisEntity: ");
        buf.append("tooltip = ");
        buf.append(getToolTipText());
        return buf.toString();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AxisEntity)) {
            return false;
        }
        AxisEntity that = (AxisEntity) obj;
        if (!getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtilities.equal(getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtilities.equal(getURLText(), that.getURLText())) {
            return false;
        }
        if (!(this.axis.equals(that.axis))) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 39;
        result = HashUtilities.hashCode(result, getToolTipText());
        result = HashUtilities.hashCode(result, getURLText());
        return result;
    }

    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(getArea(), stream);
    }

    
    private void readObject(ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        setArea(SerialUtilities.readShape(stream));
    }

}