

package org.jfree.experimental.chart.swt.editor;

import java.awt.BasicStroke;
import java.awt.Stroke;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


class SWTStrokeCanvas extends Canvas {
    
    
    public SWTStrokeCanvas(Composite parent, int style, Image image) {
        this(parent, style);
    }

    
    public SWTStrokeCanvas(Composite parent, int style) {
        super(parent, style);
        addPaintListener(new PaintListener() {
            public void paintControl(PaintEvent e) {
                BasicStroke stroke = (BasicStroke) getStroke();
                if (stroke != null) {
                    int x, y;
                    Rectangle rect = getClientArea();
                    x = (rect.width - 100) / 2;
                    y = (rect.height - 16) / 2;
                    Transform swtTransform = new Transform(e.gc.getDevice()); 
                    e.gc.getTransform(swtTransform);
                    swtTransform.translate(x, y);
                    e.gc.setTransform(swtTransform);
                    swtTransform.dispose();
                    e.gc.setBackground(getDisplay().getSystemColor(
                            SWT.COLOR_BLACK));
                    e.gc.setLineWidth((int) stroke.getLineWidth());
                    e.gc.drawLine(10, 8, 90, 8);
                }
            }
        });
    }
    
    
    public void setStroke(Stroke stroke) {
        if (stroke instanceof BasicStroke) {
            this.setData( stroke );
        }
        else { 
            throw new RuntimeException(
                "Can only handle 'Basic Stroke' at present.");
        }
    }

    
    public BasicStroke getStroke() {
        return (BasicStroke) this.getData();
    }
}
