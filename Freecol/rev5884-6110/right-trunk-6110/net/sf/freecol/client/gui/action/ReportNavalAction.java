



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportNavalPanel;



public class ReportNavalAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportNavalAction.class.getName());


    public static final String id = "reportNavalAction";
    
    
    ReportNavalAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.naval", null, KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportNavalPanel(freeColClient.getCanvas()));
    }
}
