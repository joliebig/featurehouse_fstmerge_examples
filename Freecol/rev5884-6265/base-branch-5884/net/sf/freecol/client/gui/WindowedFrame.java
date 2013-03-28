

package net.sf.freecol.client.gui;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.resources.ResourceManager;



public final class WindowedFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(WindowedFrame.class.getName());

    private Canvas canvas;
    

    
    public WindowedFrame() {
        super("FreeCol " + FreeCol.getVersion());
        logger.info("WindowedFrame's JFrame created.");

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(true);
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                ResourceManager.startBackgroundPreloading(canvas.getSize());
            }
        });

        logger.info("WindowedFrame created.");
    }

    
    
    

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        addWindowListener(new WindowedFrameListener(canvas));
    }


    
    public void addComponent(JComponent c) {
        canvas.add(c);
    }
}
