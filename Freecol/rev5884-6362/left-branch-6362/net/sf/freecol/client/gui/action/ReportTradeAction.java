



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportTradePanel;



public class ReportTradeAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportTradeAction.class.getName());


    public static final String id = "reportTradeAction";
    
    
    ReportTradeAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.trade", null, KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportTradePanel(freeColClient.getCanvas()));
    }
}
