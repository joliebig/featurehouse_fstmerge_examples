

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;


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
        
        float oldScaling = getFreeColClient().getGUI().getMapScale();

        return oldScaling < 1.0;
    }

    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        getFreeColClient().getGUI().scaleMap(1/4f);
        update();
        freeColClient.getActionManager().getFreeColAction(ZoomOutAction.id).update();
    }
}
