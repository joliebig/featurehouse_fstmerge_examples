



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportReligiousPanel;



public class ReportReligionAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportReligionAction.class.getName());


    public static final String id = "reportReligionAction";
    
    
    ReportReligionAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.religion", null, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportReligiousPanel(freeColClient.getCanvas()));
    }
}
