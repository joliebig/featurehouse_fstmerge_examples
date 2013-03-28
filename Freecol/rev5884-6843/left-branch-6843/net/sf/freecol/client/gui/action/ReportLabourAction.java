



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.ClientOptions;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.CompactLabourReport;
import net.sf.freecol.client.gui.panel.ReportLabourPanel;



public class ReportLabourAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportLabourAction.class.getName());


    public static final String id = "reportLabourAction";
    
    
    ReportLabourAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.labour", null, KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));        
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        if (freeColClient.getClientOptions().getInteger(ClientOptions.LABOUR_REPORT) ==
            ClientOptions.LABOUR_REPORT_CLASSIC) {
            freeColClient.getCanvas().showPanel(new ReportLabourPanel(freeColClient.getCanvas()));
        } else {
            freeColClient.getCanvas().showPanel(new CompactLabourReport(freeColClient.getCanvas()));
        }
    }
}
