

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.TradeRouteDialog;


public class TradeRouteAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(TradeRouteAction.class.getName());

    public static final String id = "tradeRouteAction";


    
    TradeRouteAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.view.tradeRoutes", null, KeyStroke.getKeyStroke('T', 0));
    }
    
    
    
    protected boolean shouldBeEnabled() { 
        return super.shouldBeEnabled() 
            && getFreeColClient().getMyPlayer() != null;
    }
    
    
    public String getId() {
        return id;
    }


        
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = getFreeColClient().getCanvas();
        canvas.showFreeColDialog(new TradeRouteDialog(canvas, null));
    }
}
