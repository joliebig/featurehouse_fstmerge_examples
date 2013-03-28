



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.ColopediaPanel;



public class ColopediaFatherAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColopediaFatherAction.class.getName());


    public static final String id = "colopediaFatherAction";
    
    
    ColopediaFatherAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.colopedia.father", null, KeyEvent.VK_F);        
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new ColopediaPanel(canvas, ColopediaPanel.PanelType.FATHERS, null));
    }
}
