

package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.panel.ReportHighScoresPanel;



public class ReportHighScoresAction extends MapboardAction {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ReportHighScoresAction.class.getName());

    public static final String id = "reportHighScoresAction";

    
    
    public ReportHighScoresAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.game.highScores", null, null);
    }
    
    
    protected boolean shouldBeEnabled() {
        return super.shouldBeEnabled();
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        freeColClient.getCanvas().showPanel(new ReportHighScoresPanel(freeColClient.getCanvas()));
    }
}
