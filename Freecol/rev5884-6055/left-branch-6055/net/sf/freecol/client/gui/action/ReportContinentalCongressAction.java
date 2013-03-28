



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportContinentalCongressPanel;



public class ReportContinentalCongressAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportContinentalCongressAction.class.getName());


    public static final String id = "reportContinentalCongressAction";
    
    
    ReportContinentalCongressAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.congress", null, KeyStroke.getKeyStroke(KeyEvent.VK_F6, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportContinentalCongressPanel(freeColClient.getCanvas()));
    }
}
