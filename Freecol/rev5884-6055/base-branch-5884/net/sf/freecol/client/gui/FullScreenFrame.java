

package net.sf.freecol.client.gui;

import java.awt.GraphicsDevice;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;

import net.sf.freecol.FreeCol;


public final class FullScreenFrame extends JFrame {

    private static final Logger logger = Logger.getLogger(FullScreenFrame.class
                                                          .getName());


    private Canvas canvas;

    
    public FullScreenFrame(GraphicsDevice gd) {
        super("Freecol " + FreeCol.getVersion(), gd.getDefaultConfiguration());
		
        logger.info("FullScreenFrame's JFrame created.");

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        gd.setFullScreenWindow(this);

        logger.info("Switched to full screen mode.");

        

        logger.info("FullScreenFrame created.");
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        addWindowListener(new WindowedFrameListener(canvas));
    }

    
    public void addComponent(JComponent c) {
        canvas.add(c);
    }
}

