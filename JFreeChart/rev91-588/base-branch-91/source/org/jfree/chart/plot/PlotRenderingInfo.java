
 
package org.jfree.chart.plot;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;


public class PlotRenderingInfo implements Cloneable, Serializable {

    
    private static final long serialVersionUID = 8446720134379617220L;
    
    
    private ChartRenderingInfo owner;
    
    
    private transient Rectangle2D plotArea;
    
    
    private transient Rectangle2D dataArea;
    
    
    private List subplotInfo;
      
    
    public PlotRenderingInfo(ChartRenderingInfo owner) {
        this.owner = owner;        
        this.dataArea = new Rectangle2D.Double();
        this.subplotInfo = new java.util.ArrayList();    
    }
    
    
    public ChartRenderingInfo getOwner() {
        return this.owner;
    }
    
    
    public Rectangle2D getPlotArea() {
        return this.plotArea;
    }
    
    
    public void setPlotArea(Rectangle2D area) {
        this.plotArea = area;
    }
    
    
    public Rectangle2D getDataArea() {
        return this.dataArea;
    }
    
    
    public void setDataArea(Rectangle2D area) {
        this.dataArea = area;
    }
    
    
    public int getSubplotCount() {
        return this.subplotInfo.size();
    }
    
    
    public void addSubplotInfo(PlotRenderingInfo info) {
        this.subplotInfo.add(info);    
    }
    
    
    public PlotRenderingInfo getSubplotInfo(int index) {
        return (PlotRenderingInfo) this.subplotInfo.get(index);
    }
    
    
    public int getSubplotIndex(Point2D source) {
        if (source == null) {
            throw new IllegalArgumentException("Null 'source' argument.");
        }
        int subplotCount = getSubplotCount();
        for (int i = 0; i < subplotCount; i++) {
            PlotRenderingInfo info = getSubplotInfo(i);
            Rectangle2D area = info.getDataArea();
            if (area.contains(source)) {
                return i;
            }
        }
        return -1;
    }
    
    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;   
        }
        if (!(obj instanceof PlotRenderingInfo)) {
            return false;
        }
        PlotRenderingInfo that = (PlotRenderingInfo) obj;
        if (!ObjectUtilities.equal(this.dataArea, that.dataArea)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.plotArea, that.plotArea)) {
            return false;   
        }
        if (!ObjectUtilities.equal(this.subplotInfo, that.subplotInfo)) {
            return false;   
        }
        return true;   
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        PlotRenderingInfo clone = (PlotRenderingInfo) super.clone();
        if (this.plotArea != null) {
            clone.plotArea = (Rectangle2D) this.plotArea.clone();
        }
        if (this.dataArea != null) {
            clone.dataArea = (Rectangle2D) this.dataArea.clone();
        }
        clone.subplotInfo = new java.util.ArrayList(this.subplotInfo.size());
        for (int i = 0; i < this.subplotInfo.size(); i++) {
            PlotRenderingInfo info 
                    = (PlotRenderingInfo) this.subplotInfo.get(i);
            clone.subplotInfo.add(info.clone());
        }
        return clone;
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.dataArea, stream);
        SerialUtilities.writeShape(this.plotArea, stream);
    }

    
    private void readObject(ObjectInputStream stream) 
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.dataArea = (Rectangle2D) SerialUtilities.readShape(stream);
        this.plotArea = (Rectangle2D) SerialUtilities.readShape(stream);
    }

}
