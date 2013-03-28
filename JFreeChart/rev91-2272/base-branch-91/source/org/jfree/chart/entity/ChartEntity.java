

package org.jfree.chart.entity;

import java.awt.Shape;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;
import org.jfree.chart.imagemap.URLTagFragmentGenerator;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;


public class ChartEntity implements Cloneable, PublicCloneable, Serializable {

    
    private static final long serialVersionUID = -4445994133561919083L;
    
    
    private transient Shape area;

    
    private String toolTipText;

    
    private String urlText;

    
    public ChartEntity(Shape area) {
        
        this(area, null);
    }

    
    public ChartEntity(Shape area, String toolTipText) {
        
        this(area, toolTipText, null);
    }

    
    public ChartEntity(Shape area, String toolTipText, String urlText) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");   
        }
        this.area = area;
        this.toolTipText = toolTipText;
        this.urlText = urlText;
    }

    
    public Shape getArea() {
        return this.area;
    }

    
    public void setArea(Shape area) {
        if (area == null) {
            throw new IllegalArgumentException("Null 'area' argument.");   
        }
        this.area = area;
    }

    
    public String getToolTipText() {
        return this.toolTipText;
    }

    
    public void setToolTipText(String text) {
        this.toolTipText = text;
    }

    
    public String getURLText() {
        return this.urlText;
    }

    
    public void setURLText(String text) {
        this.urlText = text;
    }

    
    public String getShapeType() {
        if (this.area instanceof Rectangle2D) {
            return "rect";
        }
        else {
            return "poly";
        }
    }

    
    public String getShapeCoords() {
        if (this.area instanceof Rectangle2D) {
            return getRectCoords((Rectangle2D) this.area);
        }
        else {
            return getPolyCoords(this.area);
        }
    }

    
    private String getRectCoords(Rectangle2D rectangle) {
        if (rectangle == null) {
            throw new IllegalArgumentException("Null 'rectangle' argument.");   
        }
        int x1 = (int) rectangle.getX();
        int y1 = (int) rectangle.getY();
        int x2 = x1 + (int) rectangle.getWidth();
        int y2 = y1 + (int) rectangle.getHeight();
        
        if (x2 == x1) {
            x2++;
        }
        if (y2 == y1) {
            y2++;
        }
        
        return x1 + "," + y1 + "," + x2 + "," + y2;
    }

    
    private String getPolyCoords(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("Null 'shape' argument.");   
        }
        StringBuffer result = new StringBuffer();
        boolean first = true;
        float[] coords = new float[6];
        PathIterator pi = shape.getPathIterator(null, 1.0);
        while (!pi.isDone()) {
            pi.currentSegment(coords);
            if (first) {
                first = false;
                result.append((int) coords[0]);
                result.append(",").append((int) coords[1]);
            }
            else {
                result.append(",");
                result.append((int) coords[0]);
                result.append(",");
                result.append((int) coords[1]);
            }
            pi.next();
        }
        return result.toString();
    }

    
    public String getImageMapAreaTag(
            ToolTipTagFragmentGenerator toolTipTagFragmentGenerator,
            URLTagFragmentGenerator urlTagFragmentGenerator) {

        StringBuffer tag = new StringBuffer();
        boolean hasURL = (this.urlText == null ? false 
                : !this.urlText.equals(""));
        boolean hasToolTip = (this.toolTipText == null ? false 
                : !this.toolTipText.equals(""));
        if (hasURL || hasToolTip) {
            tag.append("<area shape=\"" + getShapeType() + "\"" + " coords=\"" 
                    + getShapeCoords() + "\"");
            if (hasToolTip) {
                tag.append(toolTipTagFragmentGenerator.generateToolTipFragment(
                        this.toolTipText));
            }
            if (hasURL) {
                tag.append(urlTagFragmentGenerator.generateURLFragment(
                        this.urlText));
            }
            
            
            if (!hasToolTip) {
                tag.append(" alt=\"\"");
            }
            tag.append("/>");
        }
        return tag.toString();
    }
    
    
    public String toString() {
        StringBuffer buf = new StringBuffer("ChartEntity: ");
        buf.append("tooltip = ");
        buf.append(this.toolTipText);
        return buf.toString();
    }
    
    
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;   
        }
        if (obj instanceof ChartEntity) {
            ChartEntity that = (ChartEntity) obj;
            if (!this.area.equals(that.area)) {
                return false;   
            }
            if (!ObjectUtilities.equal(this.toolTipText, that.toolTipText)) {
                return false;   
            }
            if (!ObjectUtilities.equal(this.urlText, that.urlText)) {
                return false;   
            }
            return true;
        }
        return false;
    }
    
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();    
    }
    
    
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.area, stream);
     }

    
    private void readObject(ObjectInputStream stream) 
        throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.area = SerialUtilities.readShape(stream);
    }

}
