

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.ImageLibrary;


public class ZoomInAction extends FreeColAction {
    private static final Logger logger = Logger.getLogger(ZoomInAction.class.getName());




    public static final String id = "zoomInAction";


    
    ZoomInAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.zoomIn", null, KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0));
    }

    
    protected boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        } 
        
        Canvas canvas = getFreeColClient().getCanvas();
        
        if (canvas == null || !canvas.isMapboardActionsEnabled())
        	return false;
        
        float oldScaling = getFreeColClient().getGUI().getImageLibrary().getScalingFactor();

        return oldScaling < 1.0;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        float oldScaling = getFreeColClient().getGUI().getImageLibrary().getScalingFactor();
        float newScaling = oldScaling + 1/4f;
        ImageLibrary im;
        if (newScaling >= 1f) {
            newScaling = 1f;
            im = getFreeColClient().getImageLibrary();
        } else {
            try {
                im = getFreeColClient().getImageLibrary().getScaledImageLibrary(newScaling);
            } catch(Exception ex) {
                logger.warning("Failed to retrieve scaled image library.");
                im = getFreeColClient().getImageLibrary();
            }
        }
        getFreeColClient().getGUI().setImageLibrary(im);
        getFreeColClient().getGUI().forceReposition();
        getFreeColClient().getCanvas().refresh();

        update();
        freeColClient.getActionManager().getFreeColAction(ZoomOutAction.id).update();
    }
}
