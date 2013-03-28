

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportExplorationPanel;


public class ReportExplorationAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportExplorationAction.class.getName());

    public static final String id = "reportExplorationAction";
    
    
    ReportExplorationAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.exploration", null, KeyStroke.getKeyStroke(KeyEvent.VK_F13, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportExplorationPanel(freeColClient.getCanvas()));
    }
}
