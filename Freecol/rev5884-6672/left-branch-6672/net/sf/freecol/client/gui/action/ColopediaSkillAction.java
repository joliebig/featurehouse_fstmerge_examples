



package net.sf.freecol.client.gui.action;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;

import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.Canvas;
import net.sf.freecol.client.gui.panel.ColopediaPanel;



public class ColopediaSkillAction extends MapboardAction {
    @SuppressWarnings("unused")
    private static final Logger logger = Logger.getLogger(ColopediaSkillAction.class.getName());


    public static final String id = "colopediaSkillAction";
    
    
    ColopediaSkillAction(FreeColClient freeColClient) {
        super(freeColClient, "menuBar.colopedia.skill", null, KeyEvent.VK_S);        
    }
    
    
    protected boolean shouldBeEnabled() {
        return true;
    }    
    
    
    public String getId() {
        return id;
    }

    
    public void actionPerformed(ActionEvent e) {
        Canvas canvas = freeColClient.getCanvas();
        canvas.showPanel(new ColopediaPanel(canvas, ColopediaPanel.PanelType.SKILLS, null));
    }
}
