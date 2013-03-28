

package tree.graphics;

import genj.report.Report;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;


public class ScreenOutput implements GraphicsOutput {

    
    private GraphicsRenderer renderer = null;

    
    private JComponent view;

    
    private JScrollPane pane = new JScrollPane();

    private Point lastPoint;

    
    public ScreenOutput() {
        view = new JComponent() {
            public void paint(Graphics g) {
            	Graphics2D g2 = (Graphics2D)g;
            	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            			RenderingHints.VALUE_ANTIALIAS_ON);
                renderer.render(g2);
            }
        };

        pane.setViewportView(view);
        pane.setPreferredSize(new Dimension(300, 200));

        pane.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		lastPoint = e.getPoint();
        		pane.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        	}

        	public void mouseReleased(MouseEvent e) {
        		pane.setCursor(Cursor.getDefaultCursor());
        	}
        });

        pane.addMouseMotionListener(new MouseMotionAdapter() {

      		private JScrollBar hSb = pane.getHorizontalScrollBar();
    		private JScrollBar vSb = pane.getVerticalScrollBar();

        	public void mouseDragged(MouseEvent e) {
          		int dX = lastPoint.x - e.getX();
        		int dY = lastPoint.y - e.getY();

        		hSb.setValue(hSb.getValue()+dX);
        		vSb.setValue(vSb.getValue()+dY);
        		lastPoint = e.getPoint();
        	}

        });
    }

    
    public void output(GraphicsRenderer renderer) {
        this.renderer = renderer;
        view.setPreferredSize(new Dimension(renderer.getImageWidth(),
                renderer.getImageHeight()));


        
    }

    
    public Object result(Report report) {
        return pane;
    }

    
	public String getFileExtension()
	{
		return null;
	}
}
