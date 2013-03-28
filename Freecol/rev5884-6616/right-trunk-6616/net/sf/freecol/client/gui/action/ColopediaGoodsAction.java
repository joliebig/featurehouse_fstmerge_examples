



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.ColopediaPanel;



public class ColopediaGoodsAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColopediaGoodsAction.class.getName());


    public static final String id = "colopediaGoodsAction";
    
    
    ColopediaGoodsAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.colopedia.goods", null, KeyEvent.VK_G);        
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new ColopediaPanel(canvas, ColopediaPanel.PanelType.GOODS, null));
    }
}
