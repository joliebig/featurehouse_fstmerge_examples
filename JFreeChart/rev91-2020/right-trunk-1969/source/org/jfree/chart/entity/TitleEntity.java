

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.title.Title;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.SerialUtilities;


public class TitleEntity extends ChartEntity {

    
    private static final long serialVersionUID = -4445994133561919083L;
            

    
    private Title title;

    
    public TitleEntity(Shape area, Title title) {
        
        this(area, title, null);
    }

    
    public TitleEntity(Shape area, Title title, String toolTipText) {
        
        this(area, title, toolTipText, null);
    }

    
    public TitleEntity(Shape area, Title title, String toolTipText,
            String urlText) {
        super(area, toolTipText, urlText);
        if (title == null) {
            throw new IllegalArgumentException("Null 'title' argument.");
        }

        this.title = title;
    }

    
    public Title getTitle() {
        return this.title;
    }

    
    public String toString() {
        StringBuffer buf = new StringBuffer("TitleEntity: ");
        buf.append("tooltip = ");
        buf.append(getToolTipText());
        return buf.toString();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TitleEntity)) {
            return false;
        }
        TitleEntity that = (TitleEntity) obj;
        if (!getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtilities.equal(getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtilities.equal(getURLText(), that.getURLText())) {
            return false;
        }
        if (!(this.title.equals(that.title))) {
            return false;
        }
        return true;
    }

    
    public int hashCode() {
        int result = 41;
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