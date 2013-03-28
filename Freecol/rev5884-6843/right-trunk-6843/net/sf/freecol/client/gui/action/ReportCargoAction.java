

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportCargoPanel;



public class ReportCargoAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportCargoAction.class.getName());


    public static final String id = "reportCargoAction";
    
    
    ReportCargoAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.cargo", null, 
              KeyStroke.getKeyStroke(KeyEvent.VK_F1, KeyEvent.SHIFT_MASK));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportCargoPanel(freeColClient.getCanvas()));
    }
}
