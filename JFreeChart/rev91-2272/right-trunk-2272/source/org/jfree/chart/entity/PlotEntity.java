

package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.plot.Plot;
import org.jfree.chart.util.HashUtilities;
import org.jfree.chart.util.ObjectUtilities;
import org.jfree.chart.util.SerialUtilities;


public class PlotEntity extends ChartEntity {

    
    private static final long serialVersionUID = -4445994133561919083L;
            

    
    private Plot plot;

    
    public PlotEntity(Shape area, Plot plot) {
        
        this(area, plot, null);
    }

    
    public PlotEntity(Shape area, Plot plot, String toolTipText) {
        
        this(area, plot, toolTipText, null);
    }

    
    public PlotEntity(Shape area, Plot plot, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        if (plot == null) {
            throw new IllegalArgumentException("Null 'plot' argument.");
        }

        this.plot = plot;
    }

    
    public Plot getPlot() {
        return this.plot;
    }

    
    public String toString() {
        StringBuffer buf = new StringBuffer("PlotEntity: ");
        buf.append("tooltip = ");
        buf.append(getToolTipText());
        return buf.toString();
    }

    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PlotEntity)) {
            return false;
        }
        PlotEntity that = (PlotEntity) obj;
        if (!getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtilities.equal(getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtilities.equal(getURLText(), that.getURLText())) {
            return false;
        }
        if (!(this.plot.equals(that.plot))) {
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
