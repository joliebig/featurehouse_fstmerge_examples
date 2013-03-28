

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.ImageLibrary;


public class ZoomOutAction extends FreeColAction {

    private static final Logger logger = Logger.getLogger(ZoomOutAction.class.getName());

    public static final String id = "zoomOutAction";

    
    ZoomOutAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.zoomOut", null, KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0));
    }

    
    protected boolean shouldBeEnabled() {
        if (!super.shouldBeEnabled()) {
            return false;
        } 
        
        Canvas canvas = getFreeColClient().getCanvas();
        
        if (canvas == null || !canvas.isMapboardActionsEnabled())
        	return false;
        
        float oldScaling = getFreeColClient().getGUI().getMapScale();
   
        return ((oldScaling - 1/8f) * 8 > 1);
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getGUI().scaleMap(-1/4f);
        update();
        freeColClient.getActionManager().getFreeColAction(ZoomInAction.id).update();
    }
}
