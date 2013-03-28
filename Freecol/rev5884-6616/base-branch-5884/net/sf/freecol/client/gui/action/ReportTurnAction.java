



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import javax.swing.KeyStroke;

import net.sf.freecol.client.FreeColClient;



public class ReportTurnAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportTurnAction.class.getName());


    public static final String id = "reportTurnAction";
    
    
    ReportTurnAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.report.turn", null, KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getInGameController().displayModelMessages(true);
    }
}
