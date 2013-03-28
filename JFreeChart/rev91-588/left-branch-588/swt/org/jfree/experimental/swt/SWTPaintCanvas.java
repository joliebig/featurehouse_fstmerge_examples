

package org.jfree.experimental.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class SWTPaintCanvas extends Canvas {
    private Color myColor;
    
    
    public SWTPaintCanvas(Composite parent, int style, Color color) {
        this(parent, style);
        this.setColor(color);
    }
    
    
    public SWTPaintCanvas(Composite parent, int style) {
        super(parent, style);
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                e.gc.setForeground(e.gc.getDevice().getSystemColor(
                        SWT.COLOR_BLACK));
                e.gc.setBackground(SWTPaintCanvas.this.myColor);
                e.gc.fillRectangle(getClientArea());
                e.gc.drawRectangle(getClientArea().x, getClientArea().y, 
                        getClientArea().width - 1, getClientArea().height - 1);
            }
        });
    }
    
    
    public void setColor(Color color) {
        if (this.myColor != null) {
            this.myColor.dispose();
        }
        
        this.myColor = color;
    }

    
    public Color getColor() {
        return this.myColor;
    }
    
    
    public void setBackground(Color c) {
        return;
    }

    
    public void setForeground(Color c) {
        return;
    }
    
    
    public void dispose() {
        this.myColor.dispose();
    }
}
