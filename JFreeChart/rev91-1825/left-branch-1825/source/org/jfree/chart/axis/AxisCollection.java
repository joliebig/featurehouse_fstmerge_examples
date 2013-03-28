

package org.jfree.chart.axis;

import java.util.List;

import org.jfree.ui.RectangleEdge;


public class AxisCollection {

    
    private List axesAtTop;

    
    private List axesAtBottom;

    
    private List axesAtLeft;

    
    private List axesAtRight;

    
    public AxisCollection() {
        this.axesAtTop = new java.util.ArrayList();
        this.axesAtBottom = new java.util.ArrayList();
        this.axesAtLeft = new java.util.ArrayList();
        this.axesAtRight = new java.util.ArrayList();
    }

    
    public List getAxesAtTop() {
        return this.axesAtTop;
    }

   
   public List getAxesAtBottom() {
        return this.axesAtBottom;
    }

    
    public List getAxesAtLeft() {
        return this.axesAtLeft;
    }

    
    public List getAxesAtRight() {
        return this.axesAtRight;
    }

    
    public void add(Axis axis, RectangleEdge edge) {
        if (axis == null) {
            throw new IllegalArgumentException("Null 'axis' argument.");
        }
        if (edge == null) {
            throw new IllegalArgumentException("Null 'edge' argument.");
        }
        if (edge == RectangleEdge.TOP) {
            this.axesAtTop.add(axis);
        }
        else if (edge == RectangleEdge.BOTTOM) {
            this.axesAtBottom.add(axis);
        }
        else if (edge == RectangleEdge.LEFT) {
            this.axesAtLeft.add(axis);
        }
        else if (edge == RectangleEdge.RIGHT) {
            this.axesAtRight.add(axis);
        }
    }

}
